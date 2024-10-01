package org.data.exBet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.common.exception.ApiException;
import org.data.dto.ex.*;
import org.data.dto.ex.EightXBetEventsResponse.EightXBetTournamentResponse;
import org.data.exBet.repository.ExBetRepository;
import org.data.exBet.response.ExBetResponse;
import org.data.exBet.response.ExBetTournamentResponse;
import org.data.service.fetch.FetchSofaEvent;
import org.data.service.sap.SapService;
import org.data.common.model.BaseResponse;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.data.dto.ex.EightXBetEventDTO.*;
import static org.data.dto.ex.EightXBetEventsResponse.*;
import static org.data.dto.sf.GetSofaEventsByDateDto.SCHEDULED_EVENTS;
import static org.data.dto.sf.GetSofaEventsByDateDto.SCHEDULED_EVENTS_INVERSE;
import static org.data.util.TeamUtils.areTeamNamesEqual;


@Service
@AllArgsConstructor
@Log4j2
public class ExBetServiceImpl implements ExBetService {

	private final SapService sapService;
	private final ExBetRepository exBetRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final FetchSofaEvent fetchSofaEvent;
//	private final HistoryFetchEventService historyFetchEventService;


	private static final String SF_EVENT_BY_DATE_KEY = "sfEventByDate::";
	private static final String SF_INVERSE_EVENT_BY_DATE_KEY = "sfInverseEventsByDate::";

//	@Cacheable(value = "sofaEvents", key = "#date")
//	public SofaEventsResponse getSofaEventsResponse(String date) {
//		log.info("#getEventsByDate - fetching [Sofa events] for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
//		return sapService.restSofaScoreGet(SCHEDULED_EVENTS + date, SofaEventsResponse.class);
//	}
//
//	@Cacheable(value = "sofaInverseEvents", key = "#date")
//	public SofaEventsResponse getSofaInverseEventsResponse(String date) {
//		log.info("#getEventsByDate - fetching [Inverse events events] for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
//		return sapService.restSofaScoreGet(SCHEDULED_EVENTS + date + SCHEDULED_EVENTS_INVERSE, SofaEventsResponse.class);
//	}

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

		CompletableFuture<List<ExBetCommonDto.ExBetMatchResponseDto>> exBetCompletableFuture = CompletableFuture.supplyAsync(() -> exBetRepository.getExBetByDate(date));
		CompletableFuture<SfEventsResponse> sfEventsResponseCompletableFuture = getSfEventsResponseCompletableFuture(date, Boolean.FALSE);
		CompletableFuture<SfEventsResponse> sfInverseEventsResponseFuture = getSfEventsResponseCompletableFuture(date, Boolean.TRUE);
		CompletableFuture<List<SfEventsCommonDto.SfEventDto>> sfEventDtoByDate = getSfEventDtoByDate(sfEventsResponseCompletableFuture, sfInverseEventsResponseFuture, date);

		sfEventDtoByDate
				.thenCombine(exBetCompletableFuture, (sf, ex) -> {
//					List<ExBetCommonDto.ExBetMatchResponseDto> exBetMatches = new ArrayList<>();
					List<ExBetCommonDto.ExBetMatchDetailsResponseDto> exBetSfMatches = new ArrayList<>();

					for (ExBetCommonDto.ExBetMatchResponseDto exBetMatchResponseDto : ex) {
						SfEventsCommonDto.SfEventDto sfEventDto = checkEightXBetMatcInEventDTO(exBetMatchResponseDto, sf);
						if (Objects.isNull(sfEventDto)) {
//							exBetMatches.add(exBetMatchResponseDto);
						} else {
							ExBetCommonDto.ExBetMatchDetailsResponseDto exBetMatchSfDetailsResponseDto = ExBetCommonDto.ExBetMatchDetailsResponseDto.of(exBetMatchResponseDto, sfEventDto);
							exBetSfMatches.add(exBetMatchSfDetailsResponseDto);
						}
					}


					// get all the id of home and away in sofaDetail
					List<Integer> sfIds = new ArrayList<>();
					for (ExBetCommonDto.ExBetMatchDetailsResponseDto exBetMatchDetailsResponseDto : exBetSfMatches) {
						SfEventsCommonDto.SfEventDto sfDetail = exBetMatchDetailsResponseDto.getSofaDetail();
						sfIds.add(sfDetail.getHomeDetails().getIdTeam());
						sfIds.add(sfDetail.getAwayDetails().getIdTeam());
					}

					fetchSofaEvent.fetchHistoricalMatches(sfIds);

				})
				.thenApply(GetExBetEventByDateWithDetails.Response::of
				)
				.whenComplete((result, throwable) -> {
					if (Objects.nonNull(throwable)) {
						log.error("#getEventsByDate - Error occurred: {}", throwable.getMessage());
					}
				})
				.join();
		return null;
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

		CompletableFuture<List<ExBetCommonDto.ExBetMatchResponseDto>> exBetByDateFuture = CompletableFuture.supplyAsync(() -> exBetRepository.getExBetByDate(date));
		CompletableFuture<SfEventsResponse> sfEventsResponseCompletableFuture = getSfEventsResponseCompletableFuture(date, Boolean.FALSE);
		CompletableFuture<SfEventsResponse> sfInverseEventsResponseFuture = getSfEventsResponseCompletableFuture(date, Boolean.TRUE);

		return getSfEventDtoByDate(sfEventsResponseCompletableFuture, sfInverseEventsResponseFuture, date)
				.thenCombine(exBetByDateFuture, (sofaDto, exBet) -> {
					List<ExBetCommonDto.ExBetMatchResponseDto> exBetMatches = new ArrayList<>();
					List<ExBetCommonDto.ExBetMatchDetailsResponseDto> exBetSfMatches = new ArrayList<>();

					for (ExBetCommonDto.ExBetMatchResponseDto exBetMatchResponseDto : exBet) {
						//TODO: Optimize this method
						SfEventsCommonDto.SfEventDto sfEventDto = checkEightXBetMatcInEventDTO(exBetMatchResponseDto, sofaDto);
						if (Objects.isNull(sfEventDto)) {
							exBetMatches.add(exBetMatchResponseDto);
						} else {
							ExBetCommonDto.ExBetMatchDetailsResponseDto exBetMatchSfDetailsResponseDto = ExBetCommonDto.ExBetMatchDetailsResponseDto.of(exBetMatchResponseDto, sfEventDto);
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
		List<ExBetCommonDto.ExBetMatchResponseDto> exBetMatchResponseDtos = exBetRepository.getExBetByDate(request.getDate());
		return GetExBetEventByDate.Response.builder()
				.data(GetExBetEventByDate.ExBetResponseDto.builder()
						.total(exBetMatchResponseDtos.size())
						.date(request.getDate())
						.matches(exBetMatchResponseDtos)
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


	private @Nullable SfEventsCommonDto.SfEventDto checkEightXBetMatcInEventDTO(ExBetCommonDto.ExBetMatchResponseDto eightXBetMatchDTO, List<SfEventsCommonDto.SfEventDto> sfEventDtos) {

		String eightXBetNameHome = eightXBetMatchDTO.getHomeName();
		String eightXBetAwayName = eightXBetMatchDTO.getAwayName();
		LocalDateTime kickoffTime = eightXBetMatchDTO.getKickoffTime();

		boolean isEqualNameFirst;
		boolean isEqualNameSecond;

		for (SfEventsCommonDto.SfEventDto sfEventDTO : sfEventDtos) {
			isEqualNameFirst = false;
			isEqualNameSecond = false;

			String sofaNameHome = sfEventDTO.getHomeDetails().getName();
			String sofaAwayName = sfEventDTO.getAwayDetails().getName();
			LocalDateTime kickOffMatch = sfEventDTO.getKickOffMatch();

			if (areTeamNamesEqual(eightXBetNameHome, sofaNameHome) || areTeamNamesEqual(eightXBetAwayName, sofaNameHome)) {
				if (TimeUtil.isEqual(kickoffTime, kickOffMatch)) {
					isEqualNameFirst = true;
				}
			}

			if (areTeamNamesEqual(eightXBetNameHome, sofaAwayName) || areTeamNamesEqual(eightXBetAwayName, sofaAwayName)) {
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
