package org.data.tournament.repository;

import lombok.AllArgsConstructor;
import org.data.tournament.dto.ScheduledEventsCommonResponse;
import org.data.tournament.dto.ScheduledEventsResponse;
import org.data.tournament.dto.ScheduledEventsResponseConverter;
import org.data.tournament.persistent.entity.ScheduledEventsCommonEntity;
import org.data.tournament.persistent.entity.ScheduledEventsEntity;
import org.data.tournament.persistent.entity.ScheduledEventsEntityConverter;
import org.data.tournament.persistent.repository.ScheduledEventMongoRepository;
import org.data.tournament.repository.impl.ScheduledEventsRepository;
import org.data.tournament.util.TimeUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.data.tournament.util.TimeUtil.convertUnixTimestampToLocalDateTime;

@Repository
@AllArgsConstructor
public class ScheduledEventsRepositoryImpl implements ScheduledEventsRepository {

	private final ScheduledEventMongoRepository scheduledEventMongoRepository;


	@Override
	public ScheduledEventsResponse.Event getAllEventByDate(LocalDateTime date, Pageable pageable) {
		Page<ScheduledEventsEntity> evensByStartTime = scheduledEventMongoRepository.findAllByStartTimestamp(date, pageable);
		return null;
	}

	@Override
	public List<ScheduledEventsEntity> saveEvents(List<ScheduledEventsResponse.Event> events) {
		List<ScheduledEventsEntity> scheduledEventsEntities = new ArrayList<>();

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

			ScheduledEventsEntity scheduledEventsEntity = ScheduledEventsEntity.builder()
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

			scheduledEventsEntities.add(scheduledEventsEntity);
		});


		scheduledEventMongoRepository.saveAll(scheduledEventsEntities);
		return null;
	}

	@Override
	public List<ScheduledEventsResponse.Event> getAllEvents() {

		List<ScheduledEventsEntity> scheduledEventsEntities = scheduledEventMongoRepository.findAll();
		List<ScheduledEventsResponse.Event> events = new ArrayList<>();

		for (int i = 0; i < scheduledEventsEntities.size(); i++) {
			ScheduledEventsEntity scheduledEventsEntity = scheduledEventsEntities.get(i);
			ScheduledEventsCommonResponse.TournamentResponse tournamentResponse = ScheduledEventsResponseConverter.fromTournamentEntity(scheduledEventsEntity.getTournament());
			ScheduledEventsCommonResponse.SeasonResponse seasonResponse = ScheduledEventsResponseConverter.fromSesSeasonEntity(scheduledEventsEntity.getSeason());
			ScheduledEventsCommonResponse.Status statusResponse = ScheduledEventsResponseConverter.fromStatusEntity(scheduledEventsEntity.getStatus());
			ScheduledEventsCommonResponse.TeamResponse homeTeamResponse = ScheduledEventsResponseConverter.fromTeamEntity(scheduledEventsEntity.getHomeTeam());
			ScheduledEventsCommonResponse.TeamResponse awayTeamResponse = ScheduledEventsResponseConverter.fromTeamEntity(scheduledEventsEntity.getAwayTeam());
			ScheduledEventsCommonResponse.Score homeScoreResponse = ScheduledEventsResponseConverter.fromScoreResponse(scheduledEventsEntity.getHomeScore());
			ScheduledEventsCommonResponse.Score awayScoreResponse = ScheduledEventsResponseConverter.fromScoreResponse(scheduledEventsEntity.getAwayScore());
			ScheduledEventsCommonResponse.Time timeResponse = ScheduledEventsResponseConverter.fromTimeResponse(scheduledEventsEntity.getTime());
			ScheduledEventsCommonResponse.Changes changesResponse = ScheduledEventsResponseConverter.fromChangesResponse(scheduledEventsEntity.getChanges());
			ScheduledEventsCommonResponse.RoundInfo roundInfoResponse = ScheduledEventsResponseConverter.fromRoundInfoResponse(scheduledEventsEntity.getRoundInfo());
			ScheduledEventsResponse.Event event = ScheduledEventsResponse.Event.builder()
					.idDB(scheduledEventsEntity.getId())
					.tournament(tournamentResponse)
					.season(seasonResponse)
					.roundInfo(roundInfoResponse)
					.customId(scheduledEventsEntity.getCustomId())
					.status(statusResponse)
					.winnerCode(scheduledEventsEntity.getWinnerCode())
					.homeTeamResponse(homeTeamResponse)
					.awayTeamResponse(awayTeamResponse)
					.homeScore(homeScoreResponse)
					.awayScore(awayScoreResponse)
					.time(timeResponse)
					.changes(changesResponse)
					.hasGlobalHighlights(scheduledEventsEntity.isHasGlobalHighlights())
					.hasEventPlayerStatistics(scheduledEventsEntity.isHasEventPlayerStatistics())
					.hasEventPlayerHeatMap(scheduledEventsEntity.isHasEventPlayerHeatMap())
					.detailId(scheduledEventsEntity.getDetailId())
					.crowdsourcingDataDisplayEnabled(scheduledEventsEntity.isCrowdsourcingDataDisplayEnabled())
					.id(scheduledEventsEntity.getIdEvent())
					.crowdsourcingEnabled(scheduledEventsEntity.isCrowdsourcingEnabled())
					.startTimestamp(TimeUtil.convertLocalDateTimeToUnixTimestamp(scheduledEventsEntity.getStartTimestamp()))
					.slug(scheduledEventsEntity.getSlug())
					.finalResultOnly(scheduledEventsEntity.isFinalResultOnly())
					.feedLocked(scheduledEventsEntity.isFeedLocked())
					.isEditor(scheduledEventsEntity.isEditor())
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
