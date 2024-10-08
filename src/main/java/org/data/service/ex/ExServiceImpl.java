package org.data.service.ex;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.dto.ex.*;
import org.data.dto.ex.EightXBetEventsResponse.EightXBetTournamentResponse;
import org.data.dto.history.HistoryFetchCommonDto;
import org.data.repository.ex.ExBetRepository;
import org.data.repository.history.HistoryFetchEventRepository;
import org.data.response.ex.ExBetResponse;
import org.data.response.ex.ExBetTournamentResponse;
import org.data.service.fetch.FetchSfEventService;
import org.data.service.sap.SapService;
import org.data.dto.sf.SofaCommonResponse;
import org.data.dto.sf.SfEventsCommonDto;
import org.data.dto.sf.SfEventsResponse;
import org.data.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.data.dto.ex.EightXBetEventDTO.*;
import static org.data.dto.sf.GetSofaEventsByDateDto.SCHEDULED_EVENTS;
import static org.data.dto.sf.GetSofaEventsByDateDto.SCHEDULED_EVENTS_INVERSE;
import static org.data.util.TeamUtils.areTeamNamesEqual;


@Service
@AllArgsConstructor
@Log4j2
public class ExServiceImpl implements ExService {

	private final SapService sapService;
	private final ExBetRepository exBetRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final FetchSfEventService fetchSofaEvent;
	private final HistoryFetchEventRepository historyFetchEventRepository;


	private static final String SF_EVENT_BY_DATE_KEY = "sfEventByDate::";
	private static final String SF_INVERSE_EVENT_BY_DATE_KEY = "sfInverseEventsByDate::";

	private List<SfEventsResponse.EventResponse> combineSofaEvents(List<SfEventsResponse.EventResponse> events,
																   List<SfEventsResponse.EventResponse> inverseEvents) {
		List<SfEventsResponse.EventResponse> combinedEvents = new ArrayList<>(events);
		combinedEvents.addAll(inverseEvents);
		return combinedEvents;
	}

	private CompletableFuture<List<SfEventsCommonDto.SfEventDto>> getSfEventDtoByDate(CompletableFuture<SfEventsResponse> sfEventsResponseCompletableFuture,
																					  CompletableFuture<SfEventsResponse> sfInverseEventsResponseFuture,
																					  String date) {
		return sfEventsResponseCompletableFuture.thenCombineAsync(sfInverseEventsResponseFuture, (sfEventResponse, sfInverseEvents) ->
				getSfEventDtoByDate(
						combineSofaEvents(
								sfEventResponse.getEvents(),
								sfInverseEvents.getEvents()
						),
						TimeUtil.convertStringToLocalDateTime(date)
				)
		);
	}


	@Override

	public FetchExBetWithSfEventByDate.Response fetchExBetWithSfEventByDate(FetchExBetWithSfEventByDate.Request request) {
		String date = request.getDate();
		CompletableFuture<List<ExCommonDto.ExMatchResponseDto>> exBetCompletableFuture = CompletableFuture.supplyAsync(() -> exBetRepository.getExBetByDate(date));
		CompletableFuture<SfEventsResponse> sfEventsResponseCompletableFuture = getSfEventsResponseCompletableFuture(date, Boolean.FALSE);
		CompletableFuture<SfEventsResponse> sfInverseEventsResponseFuture = getSfEventsResponseCompletableFuture(date, Boolean.TRUE);
		CompletableFuture<List<SfEventsCommonDto.SfEventDto>> sfEventDtoByDateCompletableFuture = getSfEventDtoByDate(sfEventsResponseCompletableFuture, sfInverseEventsResponseFuture, date);

		return sfEventDtoByDateCompletableFuture
				.thenCombine(exBetCompletableFuture, (sf, ex) -> {
//					List<ExBetCommonDto.ExBetMatchResponseDto> exBetMatches = new ArrayList<>();
					List<ExCommonDto.ExMatchDetailsResponseDto> exBetSfMatches = new ArrayList<>();
					for (ExCommonDto.ExMatchResponseDto exMatchResponseDto : ex) {
						SfEventsCommonDto.SfEventDto sfEventDto = checkNameExWithSf(exMatchResponseDto, sf);
						if (Objects.isNull(sfEventDto)) {
//							exBetMatches.add(exBetMatchResponseDto);
						} else {
							ExCommonDto.ExMatchDetailsResponseDto exBetMatchSfDetailsResponseDto = ExCommonDto.ExMatchDetailsResponseDto
									.of(exMatchResponseDto, sfEventDto);
							exBetSfMatches.add(exBetMatchSfDetailsResponseDto);
						}
					}
					return exBetSfMatches;
				})
				.thenApply(exSfMatch -> {
					Map<Integer, ExCommonDto.ExMatchDetailsResponseDto> exMatchDetailsResponseDtoMap = new HashMap<>();

					for (ExCommonDto.ExMatchDetailsResponseDto exBetMatchDetailsResponseDto : exSfMatch) {
						SfEventsCommonDto.SfEventDto sfDetail = exBetMatchDetailsResponseDto.getSofaDetail();
						exMatchDetailsResponseDtoMap.put(sfDetail.getHomeDetails().getIdTeam(), exBetMatchDetailsResponseDto);
						exMatchDetailsResponseDtoMap.put(sfDetail.getAwayDetails().getIdTeam(), exBetMatchDetailsResponseDto);
					}

					List<Integer> idsForFetch = exMatchDetailsResponseDtoMap.keySet().stream().toList();
					fetchSofaEvent.fetchHistoricalMatches(idsForFetch.subList(0, 30));
					return exMatchDetailsResponseDtoMap;
				})
				.thenApplyAsync(map -> {

					List<FetchExBetWithSfEventByDate.FetchHistoryExWithSf> fetchHistoryExWithSfs = new ArrayList<>();

					for (Map.Entry<Integer, ExCommonDto.ExMatchDetailsResponseDto> entry : map.entrySet()) {
						HistoryFetchCommonDto.HistoryFetchEventDto historyFetchEventByTeamId = historyFetchEventRepository.getHistoryFetchEventByTeamId(entry.getKey());
						FetchExBetWithSfEventByDate.FetchHistoryExWithSf fetchHistoryExWithSf = FetchExBetWithSfEventByDate.FetchHistoryExWithSf.builder()
								.historyFetchData(historyFetchEventByTeamId)
								.exData(entry.getValue())
								.build();

						fetchHistoryExWithSfs.add(fetchHistoryExWithSf);
					}

					return FetchExBetWithSfEventByDate.ResponseData.builder()
							.totalMatches(fetchHistoryExWithSfs.size())
							.metaData(fetchHistoryExWithSfs)
							.build();
				})
				.thenApply(fetchHistoryExWithSfs -> FetchExBetWithSfEventByDate.Response.builder()
						.data(fetchHistoryExWithSfs)
						.build()
				)
				.whenComplete((result, throwable) -> {
					if (Objects.nonNull(throwable)) {
						log.error("#fetchExBetWithSfEventByDate - Error occurred: {}", throwable.getMessage());
					}
				})
//				.exceptionally(throwable -> {
//					log.error("#getEventsByDate - Error occurred: {}", throwable.getMessage());
//					return null;
//				})
				.join();
	}

	private CompletableFuture<SfEventsResponse> getSfEventsResponseCompletableFuture(String date, boolean isInverse) {
		return CompletableFuture.supplyAsync(() -> {
			final String cacheKey;
			final String requestPath;
			SfEventsResponse cachedResponse;

			if (isInverse) {
				cacheKey = SF_INVERSE_EVENT_BY_DATE_KEY + date;
				cachedResponse = (SfEventsResponse) redisTemplate.opsForValue().get(cacheKey);
				if (cachedResponse != null) {
					log.info("#getExBetEventByDateWithDetails - [Found] cached sofa inverse events for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
					return cachedResponse;
				} else {
					log.info("#getExBetEventByDateWithDetails - [Fetching] sofa inverse events for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
					requestPath = SCHEDULED_EVENTS + date + SCHEDULED_EVENTS_INVERSE;
					SfEventsResponse sfEventsResponse = sapService.restSofaScoreGet(requestPath, SfEventsResponse.class);
					redisTemplate.opsForValue().set(cacheKey, sfEventsResponse);
					return sfEventsResponse;
				}
			} else {
				cacheKey = SF_EVENT_BY_DATE_KEY + date;
				cachedResponse = (SfEventsResponse) redisTemplate.opsForValue().get(cacheKey);
				if (cachedResponse != null) {
					log.info("#getExBetEventByDateWithDetails - [Found] cached sofa events for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
					return cachedResponse;
				} else {
					log.info("#getExBetEventByDateWithDetails - [Fetching] sofa events for date: [{}] in [Thread: {}]", EventsByDateDTO.date, Thread.currentThread().getName());
					requestPath = SCHEDULED_EVENTS + date;
					SfEventsResponse sfEventsResponse = sapService.restSofaScoreGet(requestPath, SfEventsResponse.class);
					redisTemplate.opsForValue().set(cacheKey, sfEventsResponse);
					return sfEventsResponse;
				}
			}
		});
	}


	@Override
	public GetExBetEventByDateWithDetails.Response getExBetEventByDateWithDetails(GetExBetEventByDateWithDetails.Request request) {
		String date = request.getDate();
		LocalDateTime localDateTime = TimeUtil.convertStringToLocalDateTime(date);

		CompletableFuture<List<ExCommonDto.ExMatchResponseDto>> exBetByDateFuture = CompletableFuture.supplyAsync(() -> exBetRepository.getExBetByDate(date));
		CompletableFuture<SfEventsResponse> sfEventsResponseCompletableFuture = getSfEventsResponseCompletableFuture(date, Boolean.FALSE);
		CompletableFuture<SfEventsResponse> sfInverseEventsResponseFuture = getSfEventsResponseCompletableFuture(date, Boolean.TRUE);

		return getSfEventDtoByDate(sfEventsResponseCompletableFuture, sfInverseEventsResponseFuture, date)
				.thenCombine(exBetByDateFuture, (sofaDto, exBet) -> {
					List<ExCommonDto.ExMatchResponseDto> exBetMatches = new ArrayList<>();
					List<ExCommonDto.ExMatchDetailsResponseDto> exBetSfMatches = new ArrayList<>();

					for (ExCommonDto.ExMatchResponseDto exMatchResponseDto : exBet) {
						//TODO: Optimize this method
						SfEventsCommonDto.SfEventDto sfEventDto = checkNameExWithSf(exMatchResponseDto, sofaDto);
						if (Objects.isNull(sfEventDto)) {
							exBetMatches.add(exMatchResponseDto);
						} else {
							ExCommonDto.ExMatchDetailsResponseDto exBetMatchSfDetailsResponseDto = ExCommonDto.ExMatchDetailsResponseDto.of(exMatchResponseDto, sfEventDto);
							exBetSfMatches.add(exBetMatchSfDetailsResponseDto);
						}
					}
					GetExBetEventByDateWithDetails.ExBetDetail exBetDetail = GetExBetEventByDateWithDetails.ExBetDetail.of(exBetMatches);
					GetExBetEventByDateWithDetails.ExBetSfDetail exBetSfDetail = GetExBetEventByDateWithDetails.ExBetSfDetail.of(exBetSfMatches);
					return GetExBetEventByDateWithDetails.ExBetDetailsResponseDto.of(date, exBetDetail, exBetSfDetail);
				})
				.thenApply(GetExBetEventByDateWithDetails.Response::of
				)
				.whenComplete((result, throwable) -> {
					if (Objects.nonNull(throwable)) {
						log.error("#getEventsByDate - Error occurred: {}", throwable.getMessage());
					}
				})
				.join();
	}


	@Override
	public ImportExBetFromFile.Response getDataFile(MultipartFile file) {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			InputStream inputStream = file.getInputStream();
			ExBetResponse exBetResponse = objectMapper.readValue(inputStream, ExBetResponse.class);
			ExBetResponse.Data data = exBetResponse.getData();
			List<ExBetTournamentResponse> tournaments = data.getTournaments();
			ImportExBetFromFile.ExBetResponseDto exBetResponseDto = exBetRepository.saveExBetEntity(tournaments);
			return ImportExBetFromFile.Response.builder()
					.data(exBetResponseDto)
					.build();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public GetExBetEventByDate.Response getExBetEventByDate(GetExBetEventByDate.Request request) {
		List<ExCommonDto.ExMatchResponseDto> exMatchResponseDtos = exBetRepository.getExBetByDate(request.getDate());
		return GetExBetEventByDate.Response.builder()
				.data(GetExBetEventByDate.ExBetResponseDto.builder()
						.total(exMatchResponseDtos.size())
						.date(request.getDate())
						.matches(exMatchResponseDtos)
						.build())
				.build();
	}


	private List<SfEventsCommonDto.SfEventDto> getSfEventDtoByDate(List<SfEventsResponse.EventResponse> eventResponses,
																   LocalDateTime requestDate) {
		List<SfEventsCommonDto.SfEventDto> sofaSfEventDto = new ArrayList<>();
		for (SfEventsResponse.EventResponse eventResponse : eventResponses) {
			LocalDateTime responseDate = TimeUtil.convertUnixTimestampToLocalDateTime(eventResponse.getStartTimestamp());
			if (responseDate.getDayOfMonth() == requestDate.getDayOfMonth() &&
					responseDate.getMonth() == requestDate.getMonth() &&
					responseDate.getYear() == requestDate.getYear()) {
				SfEventsCommonDto.SfEventDto sfEventDTO = populatedToEventDTO(eventResponse);
				sofaSfEventDto.add(sfEventDTO);
			}
		}
		return sofaSfEventDto;
	}

	public SfEventsCommonDto.SfEventDto populatedToEventDTO(SfEventsResponse.EventResponse eventResponse) {

		SofaCommonResponse.Score homeScoreResponse = eventResponse.getHomeScore();
		SofaCommonResponse.Score eventResponseAwayScore = eventResponse.getAwayScore();

		return SfEventsCommonDto.SfEventDto.builder()
				.id(eventResponse.getId())
				.tntName(eventResponse.getTournament().getName())
				.seasonName(Objects.isNull(eventResponse.getSeason()) ? null : eventResponse.getSeason().getName())
				.round(Objects.isNull(eventResponse.getRoundInfo()) ? null : eventResponse.getRoundInfo().getRound())
				.status(Objects.isNull(eventResponse.getStatus()) ? null : eventResponse.getStatus().getDescription())
				.homeDetails(SfEventsCommonDto.TeamDetails.builder()
						.idTeam(eventResponse.getHomeTeam().getId())
						.name(eventResponse.getHomeTeam().getName())
						.build())
				.awayDetails(SfEventsCommonDto.TeamDetails.builder()
						.idTeam(eventResponse.getAwayTeam().getId())
						.name(eventResponse.getAwayTeam().getName())
						.build())
				.kickOffMatch(TimeUtil.convertUnixTimestampToLocalDateTime(eventResponse.getStartTimestamp()))
				.build();
	}


	private @Nullable SfEventsCommonDto.SfEventDto checkNameExWithSf(ExCommonDto.ExMatchResponseDto exMatchDTO, List<SfEventsCommonDto.SfEventDto> sfEventDto) {

		String exNameHome = exMatchDTO.getHomeName();
		String exAwayName = exMatchDTO.getAwayName();
		LocalDateTime kickoffTime = exMatchDTO.getKickoffTime();

		boolean isEqualNameFirst;
		boolean isEqualNameSecond;

		for (SfEventsCommonDto.SfEventDto sfEventDTO : sfEventDto) {
			isEqualNameFirst = false;
			isEqualNameSecond = false;

			String sofaNameHome = sfEventDTO.getHomeDetails().getName();
			String sofaAwayName = sfEventDTO.getAwayDetails().getName();
			LocalDateTime kickOffMatch = sfEventDTO.getKickOffMatch();

			if (areTeamNamesEqual(exNameHome, sofaNameHome) || areTeamNamesEqual(exAwayName, sofaNameHome)) {
				if (TimeUtil.isEqual(kickoffTime, kickOffMatch)) {
					isEqualNameFirst = true;
				}
			}

			if (areTeamNamesEqual(exNameHome, sofaAwayName) || areTeamNamesEqual(exAwayName, sofaAwayName)) {
				if (TimeUtil.isEqual(kickoffTime, kickOffMatch)) {
					isEqualNameSecond = true;
				}
			}

			if (isEqualNameFirst && isEqualNameSecond) {
				return sfEventDTO;
			}
		}
		return null;
	}


	/**
	 * Convert EightXBetTournamentResponse to EightXBetTournamentDTO
	 *
	 * @param eightXBetTournamentResponses List of EightXBetTournamentResponse
	 * @return Map<Integer, List < EightXBetTournamentDTO>>
	 */
	private @NotNull @Unmodifiable Map<Integer, List<EightXBetTournamentDTO>> convertToEightXBetTournamentDTO(@NotNull List<EightXBetTournamentResponse> eightXBetTournamentResponses,
																											  LocalDateTime requestDate) {
		List<EightXBetTournamentDTO> eightXBetTournamentDTOS = new ArrayList<>();
		int totalMatches = 0;

		for (EightXBetTournamentResponse eightXBetTournamentResponse : eightXBetTournamentResponses) {

			String name = eightXBetTournamentResponse.getName();
			List<EightXBetMatchDTO> eightXBetMatchDTOS = new ArrayList<>();

			for (EightXBetCommonResponse.EightXBetMatchResponse eightXBetMatchRsp : eightXBetTournamentResponse.getMatches()) {
				if (!Objects.isNull(requestDate)) {
					LocalDateTime localDateTimeResponse = TimeUtil.convertUnixTimestampToLocalDateTime(eightXBetMatchRsp.getKickoffTime());
					if (requestDate.getDayOfMonth() == localDateTimeResponse.getDayOfMonth()) {
						EightXBetMatchDTO eightXBetMatchDTO = buildEightXBetMatchDTO(eightXBetMatchRsp);
						totalMatches++;
						eightXBetMatchDTOS.add(eightXBetMatchDTO);
					}
				} else {
					EightXBetMatchDTO eightXBetMatchDTO = buildEightXBetMatchDTO(eightXBetMatchRsp);
					totalMatches++;
					eightXBetMatchDTOS.add(eightXBetMatchDTO);
				}
			}

			if (!eightXBetMatchDTOS.isEmpty()) {
				EightXBetTournamentDTO eightXBetTournamentDTO = EightXBetTournamentDTO
						.builder()
						.tntName(name)
						.count(eightXBetMatchDTOS.size())
						.matches(eightXBetMatchDTOS)
						.build();

				eightXBetTournamentDTOS.add(eightXBetTournamentDTO);
			}
		}
		return Map.of(totalMatches, eightXBetTournamentDTOS);
	}


	private EightXBetMatchDTO buildEightXBetMatchDTO(EightXBetCommonResponse.EightXBetMatchResponse eightXBetMatchResponse) {
		EightXBetCommonResponse.EightXBetTeamResponse eightXBetMatchResponseHome = eightXBetMatchResponse.getHome();
		EightXBetCommonResponse.EightXBetTeamResponse eightXBetMatchResponseAway = eightXBetMatchResponse.getAway();
		long kickoffTime = eightXBetMatchResponse.getKickoffTime();
		return EightXBetMatchDTO.builder()
				.iid(eightXBetMatchResponse.getIid())
				.homeName(eightXBetMatchResponseHome.getName())
				.awayName(eightXBetMatchResponseAway.getName())
				.slug(eightXBetMatchResponse.getName())
				.inPlay(eightXBetMatchResponse.getInplay())
				.kickoffTime(TimeUtil.convertUnixTimestampToLocalDateTime(kickoffTime))
				.build();
	}
}
