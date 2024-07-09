package org.data.tournament.repository;

import lombok.AllArgsConstructor;
import org.data.tournament.dto.ScheduledEventsCommonResponse;
import org.data.tournament.dto.ScheduledEventsResponse;
import org.data.tournament.persistent.entity.ScheduledEventsCommon;
import org.data.tournament.persistent.entity.ScheduledEventsEntity;
import org.data.tournament.persistent.repository.ScheduledEventMongoRepository;
import org.data.tournament.repository.impl.ScheduledEventsRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class ScheduledEventsRepositoryImpl implements ScheduledEventsRepository {

	private final ScheduledEventMongoRepository scheduledEventMongoRepository;


	@Override
	public List<ScheduledEventsEntity> saveEvents(List<ScheduledEventsResponse.Event> events) {
		List<ScheduledEventsEntity> scheduledEventsEntities = new ArrayList<>();

		events.forEach(event -> {

			ScheduledEventsResponse.TournamentResponse tournamentResponse = event.getTournament();
			ScheduledEventsCommonResponse.Category categoryResponse = tournamentResponse.getCategory();

			ScheduledEventsCommonResponse.Sport sportResponse = categoryResponse.getSport();
			ScheduledEventsCommonResponse.Country countryResponse = categoryResponse.getCountry();

			ScheduledEventsCommonResponse.UniqueTournament uniqueTournamentResponse = tournamentResponse.getUniqueTournament();


			ScheduledEventsResponse.Season seasonResponse = event.getSeason();
			ScheduledEventsResponse.RoundInfo roundInfoResponse = event.getRoundInfo();
			String customIdResponse = event.getCustomId();
			ScheduledEventsResponse.Status statusResponse = event.getStatus();


			ScheduledEventsResponse.Score homeScoreResponse = event.getHomeScore();
			ScheduledEventsResponse.Score awayScoreResponse = event.getAwayScore();
			ScheduledEventsResponse.Time timeResponse = event.getTime();
			ScheduledEventsResponse.Changes changesResponse = event.getChanges();
			boolean hasGlobalHighlightsResponse = event.isHasGlobalHighlights();
			int detailIdResponse = event.getDetailId();
			boolean crowdSourcingDataDisplayEnabledResponse = event.isCrowdsourcingDataDisplayEnabled();
			int idResponse = event.getId();
			boolean crowdSourcingEnabledResponse = event.isCrowdsourcingEnabled();
			long startTimestampResponse = event.getStartTimestamp();
			String slugResponse = event.getSlug();
			boolean finalResultOnlyResponse = event.isFinalResultOnly();
			boolean feedLockedResponse = event.isFeedLocked();
			boolean editorResponse = event.isEditor();


			ScheduledEventsCommon.Sport sport = ScheduledEventsCommon.Sport.builder()
					.id(sportResponse.getId())
					.name(sportResponse.getName())
					.slug(sportResponse.getSlug())
					.build();

			ScheduledEventsCommon.Country country = ScheduledEventsCommon.Country.builder()
					.name(countryResponse.getName())
					.alpha2(countryResponse.getAlpha2())
					.alpha3(countryResponse.getAlpha3())
					.build();

			ScheduledEventsCommon.Category category = ScheduledEventsCommon.Category
					.builder()
					.name(categoryResponse.getName())
					.slug(categoryResponse.getSlug())
					.sport(sport)
					.country(country)
					.build();

			ScheduledEventsCommon.UniqueTournament uniqueTournament = ScheduledEventsCommon.UniqueTournament.builder()
					.name(uniqueTournamentResponse.getName())
					.slug(uniqueTournamentResponse.getSlug())
					.category(category)
					.userCount(uniqueTournamentResponse.getUserCount())
					.crowdsourcingEnabled(uniqueTournamentResponse.isCrowdsourcingEnabled())
					.hasPerformanceGraphFeature(uniqueTournamentResponse.isHasPerformanceGraphFeature())
					.id(uniqueTournamentResponse.getId())
					.hasEventPlayerStatistics(uniqueTournamentResponse.isHasEventPlayerStatistics())
					.displayInverseHomeAwayTeams(uniqueTournamentResponse.isDisplayInverseHomeAwayTeams())
					.build();

			ScheduledEventsEntity.TournamentEntity tournamentEntity = ScheduledEventsEntity.TournamentEntity
					.builder()
					.name(tournamentResponse.getName())
					.slug(tournamentResponse.getSlug())
					.category(category)
					.uniqueTournament(uniqueTournament)
					.priority(tournamentResponse.getPriority())
					.id(tournamentResponse.getId())
					.build();


			ScheduledEventsEntity.SeasonEntity sessionEntity = ScheduledEventsEntity.SeasonEntity.builder()
					.name(seasonResponse.getName())
					.year(seasonResponse.getYear())
					.editor(seasonResponse.isEditor())
					.id(seasonResponse.getId())
					.build();

			ScheduledEventsEntity.RoundInfoEntity roundInfoEntity = ScheduledEventsEntity.RoundInfoEntity.builder()
					.round(roundInfoResponse.getRound())
					.name(roundInfoResponse.getName())
					.cupRoundType(roundInfoResponse.getCupRoundType())
					.build();

			ScheduledEventsEntity.StatusEntity statusEntity = ScheduledEventsEntity.StatusEntity.builder()
					.code(statusResponse.getCode())
					.description(statusResponse.getDescription())
					.type(statusResponse.getType())
					.build();

			ScheduledEventsResponse.Team homeTeamResponse = event.getHomeTeam();
			ScheduledEventsResponse.Team awayTeamResponse = event.getAwayTeam();


			ScheduledEventsEntity scheduledEventsEntity = ScheduledEventsEntity.builder()
					.tournament(tournamentEntity)
					.season(sessionEntity)
					.roundInfo(roundInfoEntity)
					.customId(customIdResponse)
					.status(statusEntity)
					.homeTeam(getTeamEntity(homeTeamResponse))
					.awayTeam(getTeamEntity(awayTeamResponse))
					.homeScore(ScheduledEventsEntity.ScoreEntity.builder()
							.current(homeScoreResponse.getCurrent())
							.display(homeScoreResponse.getDisplay())
							.build())
					.awayScore(ScheduledEventsEntity.ScoreEntity.builder()
							.current(awayScoreResponse.getCurrent())
							.display(awayScoreResponse.getDisplay())
							.build())
					.time(ScheduledEventsEntity.TimeEntity.builder()
							.currentPeriodStartTimestamp(timeResponse.getCurrentPeriodStartTimestamp())
							.build()
					)
					.changes(ScheduledEventsEntity.ChangesEntity.builder()
							.changeTimestamp(changesResponse.getChangeTimestamp())
							.build()
					)
					.hasGlobalHighlights(hasGlobalHighlightsResponse)
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


//		return scheduledEventMongoRepository.saveAll(scheduledEventsEntities);
		return null;
	}

	private static ScheduledEventsEntity.TeamEntity getTeamEntity(ScheduledEventsResponse.Team team) {
		ScheduledEventsCommonResponse.Sport sport = team.getSport();
		ScheduledEventsCommonResponse.Country country = team.getCountry();
		ScheduledEventsCommonResponse.TeamColors teamColors = team.getTeamColors();
		ScheduledEventsCommonResponse.FieldTranslations fieldTranslations = team.getFieldTranslations();

		return ScheduledEventsEntity.TeamEntity.builder()
				.name(sport.getName())
				.slug(team.getSlug())
				.shortName(team.getShortName())
				.sport(ScheduledEventsCommon.Sport.builder()
						.name(sport.getName())
						.slug(sport.getSlug())
						.id(sport.getId())
						.build())
				.userCount(team.getUserCount())
				.nameCode(team.getNameCode())
				.disabled(team.isDisabled())
				.national(team.isNational())
				.type(team.getType())
				.id(team.getId())
				.country(ScheduledEventsCommon.Country.builder()
						.alpha2(country.getAlpha2())
						.alpha3(country.getAlpha3())
						.name(country.getName())
						.build())
				.subTeams(null)
				.teamColors(ScheduledEventsCommon.TeamColors.builder()
						.primary(teamColors.getPrimary())
						.secondary(teamColors.getSecondary())
						.text(teamColors.getText())
						.build())
				.fieldTranslations(ScheduledEventsCommon.FieldTranslations.builder()
						.nameTranslation(ScheduledEventsCommon.Translations.builder()
								.ru(fieldTranslations.getNameTranslation().getRu())
								.ar(fieldTranslations.getNameTranslation().getAr())
								.build())
						.shortNameTranslation(ScheduledEventsCommon.Translations.builder()
								.ru(fieldTranslations.getShortNameTranslation().getRu())
								.ar(fieldTranslations.getShortNameTranslation().getAr())
								.build())
						.build())
				.build();
	}

	private LocalDateTime convertUnixTimestampToLocalDateTime(long unixTimestamp) {
		// Convert the Unix timestamp to an Instant
		Instant instant = Instant.ofEpochSecond(unixTimestamp);
		// Convert the Instant to a LocalDateTime using the system default time zone
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

}
