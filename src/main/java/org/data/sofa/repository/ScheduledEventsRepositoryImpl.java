package org.data.sofa.repository;

import jdk.jfr.Event;
import lombok.AllArgsConstructor;
import org.data.sofa.dto.ScheduledEventsCommonResponse;
import org.data.sofa.dto.SofaScheduledEventsResponse;
import org.data.sofa.dto.ScheduledEventsResponseConverter;
import org.data.persistent.common.ScheduledEventsCommonEntity;
import org.data.persistent.entity.ScheduledEventsSofaScoreEntity;
import org.data.persistent.common.ScheduledEventsEntityConverter;
import org.data.persistent.repository.ScheduledEventMongoRepository;
import org.data.sofa.dto.SofaScheduledEventsResponse.EventResponse;
import org.data.sofa.repository.impl.ScheduledEventsRepository;
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
public class ScheduledEventsRepositoryImpl implements ScheduledEventsRepository {

	private final ScheduledEventMongoRepository scheduledEventMongoRepository;


	@Override
	public EventResponse getAllEventByDate(LocalDateTime date, Pageable pageable) {
		Page<ScheduledEventsSofaScoreEntity> evensByStartTime = scheduledEventMongoRepository.findAllByStartTimestamp(date, pageable);
		return null;
	}

	@Override
	public List<EventResponse> getEventsById(Integer id) {
		return List.of();
	}

	@Override
	public List<ScheduledEventsSofaScoreEntity> saveEvents(List<EventResponse> eventResponses) {
		List<ScheduledEventsSofaScoreEntity> scheduledEventsEntities = new ArrayList<>();

		eventResponses.forEach(eventResponse -> {

			ScheduledEventsCommonResponse.TournamentResponse tournamentResponse = eventResponse.getTournament();
			ScheduledEventsCommonResponse.SeasonResponse seasonResponse = eventResponse.getSeason();
			ScheduledEventsCommonResponse.RoundInfo roundInfoResponse = eventResponse.getRoundInfo();
			String customIdResponse = eventResponse.getCustomId();
			ScheduledEventsCommonResponse.Status statusResponse = eventResponse.getStatus();
			Integer winnerCodeResponse = eventResponse.getWinnerCode();
			ScheduledEventsCommonResponse.TeamResponse homeTeamResponseResponse = eventResponse.getHomeTeam();
			ScheduledEventsCommonResponse.TeamResponse awayTeamResponseResponse = eventResponse.getAwayTeam();
			ScheduledEventsCommonResponse.Score homeScoreResponse = eventResponse.getHomeScore();
			ScheduledEventsCommonResponse.Score awayScoreResponse = eventResponse.getAwayScore();
			ScheduledEventsCommonResponse.Time timeResponse = eventResponse.getTime();
			ScheduledEventsCommonResponse.Changes changesResponse = eventResponse.getChanges();
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

			ScheduledEventsSofaScoreEntity scheduledEventsSofaScoreEntity = ScheduledEventsSofaScoreEntity.builder()
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

			scheduledEventsEntities.add(scheduledEventsSofaScoreEntity);
		});



		return scheduledEventMongoRepository.saveAll(scheduledEventsEntities);
	}

	@Override
	public List<EventResponse> getAllEvents() {

		List<ScheduledEventsSofaScoreEntity> scheduledEventsEntities = scheduledEventMongoRepository.findAll();
		List<EventResponse> eventResponses = new ArrayList<>();

		for (int i = 0; i < scheduledEventsEntities.size(); i++) {
			ScheduledEventsSofaScoreEntity scheduledEventsSofaScoreEntity = scheduledEventsEntities.get(i);
			ScheduledEventsCommonResponse.TournamentResponse tournamentResponse = ScheduledEventsResponseConverter.fromTournamentEntity(scheduledEventsSofaScoreEntity.getTournament());
			ScheduledEventsCommonResponse.SeasonResponse seasonResponse = ScheduledEventsResponseConverter.fromSesSeasonEntity(scheduledEventsSofaScoreEntity.getSeason());
			ScheduledEventsCommonResponse.Status statusResponse = ScheduledEventsResponseConverter.fromStatusEntity(scheduledEventsSofaScoreEntity.getStatus());
			ScheduledEventsCommonResponse.TeamResponse homeTeamResponse = ScheduledEventsResponseConverter.fromTeamEntity(scheduledEventsSofaScoreEntity.getHomeTeam());
			ScheduledEventsCommonResponse.TeamResponse awayTeamResponse = ScheduledEventsResponseConverter.fromTeamEntity(scheduledEventsSofaScoreEntity.getAwayTeam());
			ScheduledEventsCommonResponse.Score homeScoreResponse = ScheduledEventsResponseConverter.fromScoreResponse(scheduledEventsSofaScoreEntity.getHomeScore());
			ScheduledEventsCommonResponse.Score awayScoreResponse = ScheduledEventsResponseConverter.fromScoreResponse(scheduledEventsSofaScoreEntity.getAwayScore());
			ScheduledEventsCommonResponse.Time timeResponse = ScheduledEventsResponseConverter.fromTimeResponse(scheduledEventsSofaScoreEntity.getTime());
			ScheduledEventsCommonResponse.Changes changesResponse = ScheduledEventsResponseConverter.fromChangesResponse(scheduledEventsSofaScoreEntity.getChanges());
			ScheduledEventsCommonResponse.RoundInfo roundInfoResponse = ScheduledEventsResponseConverter.fromRoundInfoResponse(scheduledEventsSofaScoreEntity.getRoundInfo());
			EventResponse eventResponse = EventResponse.builder()
					.tournament(tournamentResponse)
					.season(seasonResponse)
					.roundInfo(roundInfoResponse)
					.customId(scheduledEventsSofaScoreEntity.getCustomId())
					.status(statusResponse)
					.winnerCode(scheduledEventsSofaScoreEntity.getWinnerCode())
					.homeTeam(homeTeamResponse)
					.awayTeam(awayTeamResponse)
					.homeScore(homeScoreResponse)
					.awayScore(awayScoreResponse)
					.time(timeResponse)
					.changes(changesResponse)
					.hasGlobalHighlights(scheduledEventsSofaScoreEntity.getHasGlobalHighlights())
					.hasEventPlayerStatistics(scheduledEventsSofaScoreEntity.getHasEventPlayerStatistics())
					.hasEventPlayerHeatMap(scheduledEventsSofaScoreEntity.getHasEventPlayerHeatMap())
					.detailId(scheduledEventsSofaScoreEntity.getDetailId())
					.crowdsourcingDataDisplayEnabled(scheduledEventsSofaScoreEntity.getCrowdsourcingDataDisplayEnabled())
					.id(scheduledEventsSofaScoreEntity.getIdEvent())
					.crowdsourcingEnabled(scheduledEventsSofaScoreEntity.getCrowdsourcingEnabled())
					.startTimestamp(TimeUtil.convertLocalDateTimeToUnixTimestamp(scheduledEventsSofaScoreEntity.getStartTimestamp()))
					.slug(scheduledEventsSofaScoreEntity.getSlug())
					.finalResultOnly(scheduledEventsSofaScoreEntity.getFinalResultOnly())
					.feedLocked(scheduledEventsSofaScoreEntity.getFeedLocked())
					.isEditor(scheduledEventsSofaScoreEntity.getIsEditor())
					.build();
			eventResponses.add(eventResponse);
		}
		return eventResponses;
	}


	@Override
	public EventResponse saveEvent(EventResponse eventResponse) {
		return null;
	}



}
