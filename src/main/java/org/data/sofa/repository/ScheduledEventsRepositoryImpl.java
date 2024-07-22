package org.data.sofa.repository;

import lombok.AllArgsConstructor;
import org.data.sofa.dto.ScheduledEventsCommonResponse;
import org.data.sofa.dto.ScheduledEventsResponse;
import org.data.sofa.dto.ScheduledEventsResponseConverter;
import org.data.persistent.common.ScheduledEventsCommonEntity;
import org.data.persistent.entity.ScheduledEventsSofaScoreEntity;
import org.data.persistent.common.ScheduledEventsEntityConverter;
import org.data.persistent.repository.ScheduledEventMongoRepository;
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
	public ScheduledEventsResponse.Event getAllEventByDate(LocalDateTime date, Pageable pageable) {
		Page<ScheduledEventsSofaScoreEntity> evensByStartTime = scheduledEventMongoRepository.findAllByStartTimestamp(date, pageable);
		return null;
	}

	@Override
	public List<ScheduledEventsSofaScoreEntity> saveEvents(List<ScheduledEventsResponse.Event> events) {
		List<ScheduledEventsSofaScoreEntity> scheduledEventsEntities = new ArrayList<>();

		events.forEach(event -> {

			ScheduledEventsCommonResponse.TournamentResponse tournamentResponse = event.getTournament();
			ScheduledEventsCommonResponse.SeasonResponse seasonResponse = event.getSeason();
			ScheduledEventsCommonResponse.RoundInfo roundInfoResponse = event.getRoundInfo();
			String customIdResponse = event.getCustomId();
			ScheduledEventsCommonResponse.Status statusResponse = event.getStatus();
			Integer winnerCodeResponse = event.getWinnerCode();
			ScheduledEventsCommonResponse.TeamResponse homeTeamResponseResponse = event.getHomeTeamResponse();
			ScheduledEventsCommonResponse.TeamResponse awayTeamResponseResponse = event.getAwayTeamResponse();
			ScheduledEventsCommonResponse.Score homeScoreResponse = event.getHomeScore();
			ScheduledEventsCommonResponse.Score awayScoreResponse = event.getAwayScore();
			ScheduledEventsCommonResponse.Time timeResponse = event.getTime();
			ScheduledEventsCommonResponse.Changes changesResponse = event.getChanges();
			boolean hasGlobalHighlightsResponse = event.isHasGlobalHighlights();
			boolean hasEventPlayerStatisticsResponse = event.isHasEventPlayerStatistics();
			boolean hasEventPlayerHeatMapResponse = event.isHasEventPlayerHeatMap();

			int detailIdResponse = event.getDetailId();
			boolean crowdSourcingDataDisplayEnabledResponse = event.isCrowdsourcingDataDisplayEnabled();
			int idResponse = event.getId();
			boolean crowdSourcingEnabledResponse = event.isCrowdsourcingEnabled();
			long startTimestampResponse = event.getStartTimestamp();
			String slugResponse = event.getSlug();
			boolean finalResultOnlyResponse = event.isFinalResultOnly();
			boolean feedLockedResponse = event.isFeedLocked();
			boolean editorResponse = event.isEditor();

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


		scheduledEventMongoRepository.saveAll(scheduledEventsEntities);
		return null;
	}

	@Override
	public List<ScheduledEventsResponse.Event> getAllEvents() {

		List<ScheduledEventsSofaScoreEntity> scheduledEventsEntities = scheduledEventMongoRepository.findAll();
		List<ScheduledEventsResponse.Event> events = new ArrayList<>();

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
			ScheduledEventsResponse.Event event = ScheduledEventsResponse.Event.builder()
					.idDB(scheduledEventsSofaScoreEntity.getId())
					.tournament(tournamentResponse)
					.season(seasonResponse)
					.roundInfo(roundInfoResponse)
					.customId(scheduledEventsSofaScoreEntity.getCustomId())
					.status(statusResponse)
					.winnerCode(scheduledEventsSofaScoreEntity.getWinnerCode())
					.homeTeamResponse(homeTeamResponse)
					.awayTeamResponse(awayTeamResponse)
					.homeScore(homeScoreResponse)
					.awayScore(awayScoreResponse)
					.time(timeResponse)
					.changes(changesResponse)
					.hasGlobalHighlights(scheduledEventsSofaScoreEntity.isHasGlobalHighlights())
					.hasEventPlayerStatistics(scheduledEventsSofaScoreEntity.isHasEventPlayerStatistics())
					.hasEventPlayerHeatMap(scheduledEventsSofaScoreEntity.isHasEventPlayerHeatMap())
					.detailId(scheduledEventsSofaScoreEntity.getDetailId())
					.crowdsourcingDataDisplayEnabled(scheduledEventsSofaScoreEntity.isCrowdsourcingDataDisplayEnabled())
					.id(scheduledEventsSofaScoreEntity.getIdEvent())
					.crowdsourcingEnabled(scheduledEventsSofaScoreEntity.isCrowdsourcingEnabled())
					.startTimestamp(TimeUtil.convertLocalDateTimeToUnixTimestamp(scheduledEventsSofaScoreEntity.getStartTimestamp()))
					.slug(scheduledEventsSofaScoreEntity.getSlug())
					.finalResultOnly(scheduledEventsSofaScoreEntity.isFinalResultOnly())
					.feedLocked(scheduledEventsSofaScoreEntity.isFeedLocked())
					.isEditor(scheduledEventsSofaScoreEntity.isEditor())
					.build();
			events.add(event);
		}
		return events;
	}


	@Override
	public ScheduledEventsResponse.Event saveEvent(ScheduledEventsResponse.Event event) {
		return null;
	}



}
