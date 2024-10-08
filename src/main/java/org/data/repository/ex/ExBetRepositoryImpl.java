package org.data.repository.ex;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.dto.ex.ExCommonDto;
import org.data.dto.ex.ImportExBetFromFile;
import org.data.response.ex.ExBetMatchResponse;
import org.data.response.ex.ExBetTournamentResponse;
import org.data.persistent.entity.ExBetEntity;
import org.data.persistent.projection.EventsEightXBetProjection;
import org.data.persistent.repository.ExBetMongoRepository;
import org.data.util.TimeUtil;
import org.data.util.TournamentEightXBetConverter;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
@Log4j2
public class ExBetRepositoryImpl implements ExBetRepository {

	private final ExBetMongoRepository exBetMongoRepository;


	@Override
	public ImportExBetFromFile.ExBetResponseDto saveExBetEntity(List<ExBetTournamentResponse> exBetTournamentResponses) {
		List<ExBetEntity> exBetEntities = new ArrayList<>();
		List<Integer> iIds = exBetMongoRepository.findAllByIId()
				.stream()
				.map(EventsEightXBetProjection::getIId)
				.toList();

		List<ImportExBetFromFile.ExBetMatchResponseDto> exBetMatchResponseDtos = new ArrayList<>();
		ImportExBetFromFile.ExBetResponseDto exBetResponseDto = new ImportExBetFromFile.ExBetResponseDto();

		for (ExBetTournamentResponse exBetTournamentResponse : exBetTournamentResponses) {
			List<ExBetMatchResponse> matchResponses = exBetTournamentResponse.getMatches();

			for (ExBetMatchResponse matchRs : matchResponses) {
				if (!iIds.contains(matchRs.getIid())) {
					ExBetEntity exBetEntity = populateEventsExBetEntity(matchRs);
					exBetEntities.add(exBetEntity);
					ImportExBetFromFile.ExBetMatchResponseDto exBetMatchResponseDto = populatedToExBetMatchResponseDto(exBetEntity);
					exBetMatchResponseDtos.add(exBetMatchResponseDto);
				}
			}
		}
		log.info("Saving {} new matches", exBetEntities.size());

		List<ExBetEntity> savedExBetEntity = exBetMongoRepository.saveAll(exBetEntities);
		exBetResponseDto.setMatches(exBetMatchResponseDtos);

		return exBetResponseDto;
	}

	private ImportExBetFromFile.ExBetMatchResponseDto populatedToExBetMatchResponseDto(ExBetEntity exBetEntity) {
		return ImportExBetFromFile.ExBetMatchResponseDto
				.builder()
				.iid(exBetEntity.getIId())
				.inPlay(exBetEntity.getInPlay())
				.homeName(exBetEntity.getHomeTeam().getName())
				.awayName(exBetEntity.getAwayTeam().getName())
				.slug(exBetEntity.getName())
				.build();
	}


	@Override
	public List<ExCommonDto.ExMatchResponseDto> getExBetByDate(String date) {
		LocalDateTime startOfDay = TimeUtil.convertStringToLocalDateTime(date);
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		exBetMongoRepository.findAllByKickoffTime(startOfDay, endOfDay);

		List<ExBetEntity> allExBetByKickoffTime = exBetMongoRepository.findAllByKickoffTime(startOfDay, endOfDay);
		if (!allExBetByKickoffTime.isEmpty()) {
			List<ExCommonDto.ExMatchResponseDto> exMatchResponseDtos = new ArrayList<>();
			for (ExBetEntity exBetEntity : allExBetByKickoffTime) {
				ExCommonDto.ExMatchResponseDto exMatchResponseDto = ExCommonDto.ExMatchResponseDto
						.builder()
						.tntName(exBetEntity.getTnName())
						.iid(exBetEntity.getIId())
						.inPlay(exBetEntity.getInPlay())
						.homeName(exBetEntity.getHomeTeam().getName())
						.awayName(exBetEntity.getAwayTeam().getName())
						.slug(exBetEntity.getName())
						.kickoffTime(exBetEntity.getKickoffTime())
						.fetchedDate(exBetEntity.getFetchedDate())
						.build();
				exMatchResponseDtos.add(exMatchResponseDto);
			}
			return exMatchResponseDtos;
		}

		return List.of();
	}

	@Override
	public void updateInplayEvent() {
		List<ExBetEntity> inPlayEvent = exBetMongoRepository.findAllByInPlay();
		List<ExBetEntity> inPlayEventUpdated = new ArrayList<>();

		for (ExBetEntity exBetEntity : inPlayEvent) {
			LocalDateTime kickoffTime = exBetEntity.getKickoffTime();
			LocalDateTime now = LocalDateTime.now();
			if (now.isAfter(kickoffTime)) {
				exBetEntity.setInPlay(false);
				inPlayEventUpdated.add(exBetEntity);
			}
		}

		exBetMongoRepository.saveAll(inPlayEventUpdated);
	}

//	@Override
//	public List<ExBetEntity> saveTournamentResponse(List<ExBetTournamentResponse> tournamentResponses) {
//
//		List<ExBetEntity> scheduledEventsEightXBetEntities = new ArrayList<>();
//		List<Integer> iIds = scheduledEventsEightXBetMongoRepository.findAllByIId()
//				.stream()
//				.map(EventsEightXBetProjection::getIId)
//				.toList();
//
//		for (ExBetTournamentResponse tournamentResponse : tournamentResponses) {
//			List<ExBetMatchResponse> matchesResponses = tournamentResponse.getMatches();
//			for (ExBetMatchResponse matchesResponse : matchesResponses) {
//				if (!iIds.contains(matchesResponse.getIid())) {
//					log.info("Detected new match with iid: {}", matchesResponse);
//					ExBetEntity exBetEntity = populateEventsExBetEntity(matchesResponse);
//					scheduledEventsEightXBetEntities.add(exBetEntity);
//				}
//
//			}
//		}
//
//		scheduledEventsEightXBetMongoRepository.saveAll(scheduledEventsEightXBetEntities);
//		return scheduledEventsEightXBetEntities;
//	}

	@Override
	public List<ExBetEntity> getAllEventsEntity() {
		return exBetMongoRepository.findAll();
	}

//	@Override
//	public void saveMatchesMap(Map<EightXBetTournamentResponse, EightXBetCommonResponse.EightXBetMatchResponse> tournamentMatchResponseMap) {
//		List<ExBetEntity> eventsEightXBetEntities = new ArrayList<>();
//		for (Map.Entry<EightXBetTournamentResponse, EightXBetCommonResponse.EightXBetMatchResponse> entry : tournamentMatchResponseMap.entrySet()) {
//			ExBetEntity exBetEntity = populateEventsExBetEntity(entry.getKey(), entry.getValue());
//			eventsEightXBetEntities.add(exBetEntity);
//		}
//		scheduledEventsEightXBetMongoRepository.saveAll(eventsEightXBetEntities);
//	}

	@Override
	public void saveMatch(ExBetTournamentResponse tournament, ExBetMatchResponse match) {
		ExBetEntity exBetEntity = populateEventsExBetEntity(match);
		exBetMongoRepository.save(exBetEntity);
	}

	private ExBetEntity populateEventsExBetEntity(ExBetMatchResponse matchesResponse) {
		return ExBetEntity
				.builder()
				.sId(matchesResponse.getSid())
				.cId(matchesResponse.getCid())
				.tId(matchesResponse.getTid())
				.iId(matchesResponse.getIid())
				.countdown(matchesResponse.getCountdown())
				.state(matchesResponse.getState())
				.series(matchesResponse.getSeries())
				.vd(matchesResponse.getVd())
				.streaming(matchesResponse.getStreaming())
				.chatMid(matchesResponse.getChatMid())
				.gifMid(matchesResponse.getGifMid())
				.graphMid(matchesResponse.getGraphMid())
				.inPlay(matchesResponse.getInplay())
				.video(matchesResponse.getVideo())
				.nv(matchesResponse.getNv())
				.scoreId(matchesResponse.getScoreId())
				.tnName(matchesResponse.getTnName())
				.tnPriority(matchesResponse.getTnPriority())
				.homeTeam(TournamentEightXBetConverter.convertToTeamEntity(matchesResponse.getHome()))
				.awayTeam(TournamentEightXBetConverter.convertToTeamEntity(matchesResponse.getAway()))
				.round(TournamentEightXBetConverter.convertToRoundEntity(matchesResponse.getRound()))
				.marketInfo(TournamentEightXBetConverter.convertToMarketInfoEntity(matchesResponse.getMarketInfo()))
				.mids(TournamentEightXBetConverter.convertToMidsEntity(matchesResponse.getMids()))
				.gifts(TournamentEightXBetConverter.convertToGiftEntity(matchesResponse.getGifs()))
				.videos(TournamentEightXBetConverter.convertToVideoEntity(matchesResponse.getVideos()))
				.anchors(TournamentEightXBetConverter.convertToAnchorEntity(matchesResponse.getAnchors()))
				.name(matchesResponse.getName())
				.kickoffTime(TimeUtil.convertUnixTimestampToLocalDateTime(matchesResponse.getKickoffTime()))
				.fetchedDate(LocalDateTime.now())
				.build();
	}

}
