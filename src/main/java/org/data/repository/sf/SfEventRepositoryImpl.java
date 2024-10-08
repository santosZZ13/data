package org.data.repository.sf;

import lombok.AllArgsConstructor;
import org.data.dto.sf.SfEventsResponse;
import org.data.dto.sf.SofaCommonResponse;
import org.data.dto.sf.ScheduledEventsResponseConverter;
import org.data.persistent.common.ScheduledEventsCommonEntity;
import org.data.persistent.entity.HistoryEntity;
import org.data.persistent.entity.ScheduledEventsSofaScoreEntity;
import org.data.persistent.common.ScheduledEventsEntityConverter;
import org.data.persistent.repository.ScheduledEventMongoRepository;
import org.data.dto.sf.SfEventsResponse.EventResponse;
import org.data.repository.sf.impl.SfEventRepository;
import org.data.util.TimeUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.data.util.TimeUtil.convertUnixTimestampToLocalDateTime;

@Repository
@AllArgsConstructor
public class SfEventRepositoryImpl implements SfEventRepository {

	private final ScheduledEventMongoRepository scheduledEventMongoRepository;


	@Override
	public EventResponse getAllEventByDate(LocalDateTime date, Pageable pageable) {
//		Page<ScheduledEventsSofaScoreEntity> evensByStartTime = scheduledEventMongoRepository.findAll(date, pageable);
		return null;
	}

	@Override
	public List<EventResponse> getEventsById(Integer id) {
		return List.of();
	}

	@Override
	public ScheduledEventsSofaScoreEntity saveSfEvent( List<EventResponse> eventResponses, int teamId) {
		SfEventsResponse.EventResponse eventRp = eventResponses.get(0);
		String teamNameFromId = eventRp.getTeamNameFromId(teamId, List.of(eventRp.getHomeTeam(), eventRp.getAwayTeam()));
		List<HistoryEntity> historyEntities = new ArrayList<>();

		eventResponses.forEach(eventResponse -> {

			SofaCommonResponse.TournamentResponse tournamentResponse = eventResponse.getTournament();
			SofaCommonResponse.SeasonResponse seasonResponse = eventResponse.getSeason();
			SofaCommonResponse.RoundInfo roundInfoResponse = eventResponse.getRoundInfo();
			String customIdResponse = eventResponse.getCustomId();
			SofaCommonResponse.Status statusResponse = eventResponse.getStatus();
			Integer winnerCodeResponse = eventResponse.getWinnerCode();
			SofaCommonResponse.TeamResponse homeTeamResponseResponse = eventResponse.getHomeTeam();
			SofaCommonResponse.TeamResponse awayTeamResponseResponse = eventResponse.getAwayTeam();
			SofaCommonResponse.Score homeScoreResponse = eventResponse.getHomeScore();
			SofaCommonResponse.Score awayScoreResponse = eventResponse.getAwayScore();
			SofaCommonResponse.Time timeResponse = eventResponse.getTime();
			SofaCommonResponse.Changes changesResponse = eventResponse.getChanges();
			Boolean hasGlobalHighlightsResponse = eventResponse.getHasGlobalHighlights();
			Boolean hasEventPlayerStatisticsResponse = eventResponse.getHasEventPlayerStatistics();
			Boolean hasEventPlayerHeatMapResponse = eventResponse.getHasEventPlayerHeatMap();
			Integer detailIdResponse = eventResponse.getDetailId();
			Boolean crowdSourcingDataDisplayEnabledResponse = eventResponse.getCrowdsourcingDataDisplayEnabled();
			Integer idResponse = eventResponse.getId();
			Boolean crowdSourcingEnabledResponse = eventResponse.getCrowdsourcingEnabled();
			Long startTimestampResponse = eventResponse.getStartTimestamp();
			String slugResponse = eventResponse.getSlug();
			Boolean finalResultOnlyResponse = eventResponse.getFinalResultOnly();
			Boolean feedLockedResponse = eventResponse.getFeedLocked();
			Boolean editorResponse = eventResponse.getIsEditor();

			ScheduledEventsCommonEntity.TournamentEntity tournamentEntity = ScheduledEventsEntityConverter.fromTournamentResponse(tournamentResponse);
			ScheduledEventsCommonEntity.SeasonEntity seasonEntity = ScheduledEventsEntityConverter.fromSesSeasonResponse(seasonResponse);
			ScheduledEventsCommonEntity.RoundInfoEntity roundInfoEntity = ScheduledEventsEntityConverter.fromRoundInfoResponse(roundInfoResponse);
			ScheduledEventsCommonEntity.StatusEntity statusEntity = ScheduledEventsEntityConverter.fromStatusResponse(statusResponse);
			ScheduledEventsCommonEntity.TeamEntity homeTeamEntity = ScheduledEventsEntityConverter.fromTeamResponse(homeTeamResponseResponse);
			ScheduledEventsCommonEntity.TeamEntity awayTeamEntity = ScheduledEventsEntityConverter.fromTeamResponse(awayTeamResponseResponse);
			ScheduledEventsCommonEntity.ScoreEntity homeScoreEntity = Objects.isNull(homeScoreResponse) ? null : ScheduledEventsEntityConverter.fromScoreResponse(homeScoreResponse);
			ScheduledEventsCommonEntity.ScoreEntity awayeScoreEntity = Objects.isNull(homeScoreResponse) ? null : ScheduledEventsEntityConverter.fromScoreResponse(awayScoreResponse);
			ScheduledEventsCommonEntity.TimeEntity timeEntity = ScheduledEventsEntityConverter.fromTimeResponse(timeResponse);
			ScheduledEventsCommonEntity.ChangesEntity changesEntity = ScheduledEventsEntityConverter.fromChangesResponse(changesResponse);



			HistoryEntity scheduledEventsSofaScoreEntity = HistoryEntity.builder()
					.tournament(tournamentEntity)
					.season(seasonEntity)
					.roundInfo(roundInfoEntity)
					.customId(customIdResponse)
					.status(statusEntity)
					.winnerCode(winnerCodeResponse)
					.homeTeam(homeTeamEntity)
					.awayTeam(awayTeamEntity)
					.homeScore(homeScoreEntity)
					.awayScore(awayeScoreEntity)
					.time(timeEntity)
					.changes(changesEntity)
					.hasGlobalHighlights(hasGlobalHighlightsResponse)
					.hasEventPlayerStatistics(hasEventPlayerStatisticsResponse)
					.hasEventPlayerHeatMap(hasEventPlayerHeatMapResponse)
					.detailId(detailIdResponse)
					.crowdsourcingDataDisplayEnabled(crowdSourcingDataDisplayEnabledResponse)
					.idEvent(idResponse)
					.crowdsourcingEnabled(crowdSourcingEnabledResponse)
					.startTimestamp(convertUnixTimestampToLocalDateTime(startTimestampResponse))
					.slug(slugResponse)
					.finalResultOnly(finalResultOnlyResponse)
					.feedLocked(feedLockedResponse)
					.isEditor(editorResponse)
					.build();

			historyEntities.add(scheduledEventsSofaScoreEntity);

		});

		ScheduledEventsSofaScoreEntity scheduledEventsEntities = ScheduledEventsSofaScoreEntity.builder()
				.teamId(teamId)
				.team(teamNameFromId)
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.histories(historyEntities)
				.build();

		return scheduledEventMongoRepository.save(scheduledEventsEntities);
	}

	@Override
	public List<EventResponse> getAllEvents() {
		return null;
//		List<ScheduledEventsSofaScoreEntity> scheduledEventsEntities = scheduledEventMongoRepository.findAll();
//		List<EventResponse> eventResponses = new ArrayList<>();
//
//		for (int i = 0; i < scheduledEventsEntities.size(); i++) {
//			ScheduledEventsSofaScoreEntity scheduledEventsSofaScoreEntity = scheduledEventsEntities.get(i);
//			SofaCommonResponse.TournamentResponse tournamentResponse = ScheduledEventsResponseConverter.fromTournamentEntity(scheduledEventsSofaScoreEntity.getTournament());
//			SofaCommonResponse.SeasonResponse seasonResponse = ScheduledEventsResponseConverter.fromSesSeasonEntity(scheduledEventsSofaScoreEntity.getSeason());
//			SofaCommonResponse.Status statusResponse = ScheduledEventsResponseConverter.fromStatusEntity(scheduledEventsSofaScoreEntity.getStatus());
//			SofaCommonResponse.TeamResponse homeTeamResponse = ScheduledEventsResponseConverter.fromTeamEntity(scheduledEventsSofaScoreEntity.getHomeTeam());
//			SofaCommonResponse.TeamResponse awayTeamResponse = ScheduledEventsResponseConverter.fromTeamEntity(scheduledEventsSofaScoreEntity.getAwayTeam());
//			SofaCommonResponse.Score homeScoreResponse = ScheduledEventsResponseConverter.fromScoreResponse(scheduledEventsSofaScoreEntity.getHomeScore());
//			SofaCommonResponse.Score awayScoreResponse = ScheduledEventsResponseConverter.fromScoreResponse(scheduledEventsSofaScoreEntity.getAwayScore());
//			SofaCommonResponse.Time timeResponse = ScheduledEventsResponseConverter.fromTimeResponse(scheduledEventsSofaScoreEntity.getTime());
//			SofaCommonResponse.Changes changesResponse = ScheduledEventsResponseConverter.fromChangesResponse(scheduledEventsSofaScoreEntity.getChanges());
//			SofaCommonResponse.RoundInfo roundInfoResponse = ScheduledEventsResponseConverter.fromRoundInfoResponse(scheduledEventsSofaScoreEntity.getRoundInfo());
//			EventResponse eventResponse = EventResponse.builder()
//					.tournament(tournamentResponse)
//					.season(seasonResponse)
//					.roundInfo(roundInfoResponse)
//					.customId(scheduledEventsSofaScoreEntity.getCustomId())
//					.status(statusResponse)
//					.winnerCode(scheduledEventsSofaScoreEntity.getWinnerCode())
//					.homeTeam(homeTeamResponse)
//					.awayTeam(awayTeamResponse)
//					.homeScore(homeScoreResponse)
//					.awayScore(awayScoreResponse)
//					.time(timeResponse)
//					.changes(changesResponse)
//					.hasGlobalHighlights(scheduledEventsSofaScoreEntity.getHasGlobalHighlights())
//					.hasEventPlayerStatistics(scheduledEventsSofaScoreEntity.getHasEventPlayerStatistics())
//					.hasEventPlayerHeatMap(scheduledEventsSofaScoreEntity.getHasEventPlayerHeatMap())
//					.detailId(scheduledEventsSofaScoreEntity.getDetailId())
//					.crowdsourcingDataDisplayEnabled(scheduledEventsSofaScoreEntity.getCrowdsourcingDataDisplayEnabled())
//					.id(scheduledEventsSofaScoreEntity.getIdEvent())
//					.crowdsourcingEnabled(scheduledEventsSofaScoreEntity.getCrowdsourcingEnabled())
//					.startTimestamp(TimeUtil.convertLocalDateTimeToUnixTimestamp(scheduledEventsSofaScoreEntity.getStartTimestamp()))
//					.slug(scheduledEventsSofaScoreEntity.getSlug())
//					.finalResultOnly(scheduledEventsSofaScoreEntity.getFinalResultOnly())
//					.feedLocked(scheduledEventsSofaScoreEntity.getFeedLocked())
//					.isEditor(scheduledEventsSofaScoreEntity.getIsEditor())
//					.build();
//			eventResponses.add(eventResponse);
//		}
//		return eventResponses;
	}


	@Override
	public EventResponse saveEvent(EventResponse eventResponse) {
		return null;
	}



}
