package org.data.tournament.repository;

import lombok.AllArgsConstructor;
import org.data.tournament.converter.ScheduledEventsConverter;
import org.data.tournament.dto.ScheduledEventsCommonResponse;
import org.data.tournament.dto.ScheduledEventsResponse;
import org.data.tournament.persistent.entity.ScheduledEventsCommon;
import org.data.tournament.persistent.entity.ScheduledEventsEntity;
import org.data.tournament.persistent.repository.ScheduledEventMongoRepository;
import org.data.tournament.repository.impl.ScheduledEventsRepository;
import org.data.tournament.util.TimeUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.data.tournament.util.TimeUtil.convertUnixTimestampToLocalDateTime;

@Repository
@AllArgsConstructor
public class ScheduledEventsRepositoryImpl implements ScheduledEventsRepository {

	private final ScheduledEventMongoRepository scheduledEventMongoRepository;


	@Override
	public List<ScheduledEventsEntity> saveEvents(List<ScheduledEventsResponse.Event> events) {
		List<ScheduledEventsEntity> scheduledEventsEntities = new ArrayList<>();

		events.forEach(event -> {

			ScheduledEventsResponse.TournamentResponse tournamentResponse = event.getTournament();
			ScheduledEventsResponse.SeasonResponse seasonResponse = event.getSeason();
			ScheduledEventsResponse.RoundInfo roundInfoResponse = event.getRoundInfo();
			String customIdResponse = event.getCustomId();
			ScheduledEventsResponse.Status statusResponse = event.getStatus();
			Integer winnerCodeResponse = event.getWinnerCode();
			ScheduledEventsResponse.Team homeTeamResponse = event.getHomeTeam();
			ScheduledEventsResponse.Team awayTeamResponse = event.getAwayTeam();
			ScheduledEventsResponse.Score homeScoreResponse = event.getHomeScore();
			ScheduledEventsResponse.Score awayScoreResponse = event.getAwayScore();
			ScheduledEventsResponse.Time timeResponse = event.getTime();
			ScheduledEventsResponse.Changes changesResponse = event.getChanges();
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

			ScheduledEventsEntity.TournamentEntity tournamentEntity = populatedTournamentEntity(tournamentResponse);
			ScheduledEventsEntity.SeasonEntity seasonEntity = populatedSessionEntity(seasonResponse);
			ScheduledEventsEntity.RoundInfoEntity roundInfoEntity = populatedRoundInfoEntity(roundInfoResponse);
			ScheduledEventsEntity.StatusEntity statusEntity = populatedStatusEntity(statusResponse);
			ScheduledEventsEntity.TeamEntity homeTeamEntity = populatedTeamEntity(homeTeamResponse);
			ScheduledEventsEntity.TeamEntity awayTeamEntity = populatedTeamEntity(awayTeamResponse);
			ScheduledEventsEntity.ScoreEntity homeScoreEntity = Objects.isNull(homeScoreResponse) ? null : populatedScore(homeScoreResponse);
			ScheduledEventsEntity.ScoreEntity awayeScoreEntity = Objects.isNull(homeScoreResponse) ? null : populatedScore(awayScoreResponse);
			ScheduledEventsEntity.TimeEntity timeEntity = populatedTime(timeResponse);
			ScheduledEventsEntity.ChangesEntity changesEntity = populatedChanges(changesResponse);

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


		return List.of();
	}


	@Override
	public ScheduledEventsResponse.Event saveEvent() {
		return null;
	}

	private ScheduledEventsEntity.ChangesEntity populatedChanges(ScheduledEventsResponse.Changes changesResponse) {
		return ScheduledEventsEntity.ChangesEntity.builder()
				.changeTimestamp(TimeUtil.convertUnixTimestampToLocalDateTime(changesResponse.getChangeTimestamp()))
				.changes(changesResponse.getChanges())
				.build();
	}


	private ScheduledEventsEntity.TimeEntity populatedTime(ScheduledEventsResponse.Time timeResponse) {
		return ScheduledEventsEntity.TimeEntity.builder()
				.injuryTime1(timeResponse.getInjuryTime1())
				.injuryTime2(timeResponse.getInjuryTime2())
				.currentPeriodStartTimestamp(TimeUtil.convertUnixTimestampToLocalDateTime(timeResponse.getCurrentPeriodStartTimestamp()))
				.build();
	}


	private ScheduledEventsEntity.ScoreEntity populatedScore(ScheduledEventsResponse.Score scoreResponse) {
		return ScheduledEventsEntity.ScoreEntity.builder()
				.current(scoreResponse.getCurrent())
				.display(scoreResponse.getDisplay())
				.period1(scoreResponse.getPeriod1())
				.period2(scoreResponse.getPeriod2())
				.normaltime(scoreResponse.getNormaltime())
				.build();
	}

	private ScheduledEventsEntity.StatusEntity populatedStatusEntity(ScheduledEventsResponse.Status statusResponse) {
		return Objects.isNull(statusResponse) ? null : ScheduledEventsEntity.StatusEntity.builder()
				.code(statusResponse.getCode())
				.description(statusResponse.getDescription())
				.type(statusResponse.getType())
				.build();
	}

	private ScheduledEventsEntity.RoundInfoEntity populatedRoundInfoEntity(ScheduledEventsResponse.RoundInfo roundInfoResponse) {
		return Objects.isNull(roundInfoResponse) ? null : ScheduledEventsEntity.RoundInfoEntity.builder()
				.round(roundInfoResponse.getRound())
				.name(roundInfoResponse.getName())
				.cupRoundType(roundInfoResponse.getCupRoundType())
				.build();
	}

	private ScheduledEventsEntity.SeasonEntity populatedSessionEntity(ScheduledEventsResponse.SeasonResponse seasonResponse) {
		return Objects.isNull(seasonResponse) ? null : ScheduledEventsEntity.SeasonEntity.builder()
				.name(seasonResponse.getName())
				.year(seasonResponse.getYear())
				.editor(seasonResponse.getEditor())
				.id(seasonResponse.getId())
				.build();
	}

	private ScheduledEventsEntity.TournamentEntity populatedTournamentEntity(ScheduledEventsResponse.TournamentResponse tournamentResponse) {

		ScheduledEventsCommonResponse.Category categoryResponse = tournamentResponse.getCategory();
		ScheduledEventsCommonResponse.Sport sportResponse = categoryResponse.getSport();
		ScheduledEventsCommonResponse.Country countryResponse = categoryResponse.getCountry();
		String flagResponse = categoryResponse.getFlag();
		Integer idResponse = categoryResponse.getId();

		ScheduledEventsCommon.Sport sport = ScheduledEventsConverter.fromSportScheduledEventsCommonResponse(sportResponse);
		ScheduledEventsCommon.Country country = ScheduledEventsConverter.fromCountryScheduledEventsCommonResponse(countryResponse);

		ScheduledEventsCommon.Category category = ScheduledEventsCommon.Category
				.builder()
				.id(idResponse)
				.name(categoryResponse.getName())
				.slug(categoryResponse.getSlug())
				.sport(sport)
				.country(country)
				.flag(flagResponse)
				.build();


		ScheduledEventsCommonResponse.UniqueTournament uniqueTournamentResponse = tournamentResponse.getUniqueTournament();
		ScheduledEventsCommonResponse.Category categoryUniQueTournamentResponse = uniqueTournamentResponse.getCategory();
		ScheduledEventsCommonResponse.Sport sportUniQueTournament = categoryUniQueTournamentResponse.getSport();
		ScheduledEventsCommonResponse.Country countryUniQueTournament = categoryUniQueTournamentResponse.getCountry();
		Integer categoryUniQueTournamentId = categoryUniQueTournamentResponse.getId();
		String categoryUniQueTournamentFlag = categoryUniQueTournamentResponse.getFlag();

		ScheduledEventsCommon.Sport sportCategoryUniQueTournament = ScheduledEventsConverter.fromSportScheduledEventsCommonResponse(sportUniQueTournament);
		ScheduledEventsCommon.Country countryCategoryUniQueTournament = ScheduledEventsConverter.fromCountryScheduledEventsCommonResponse(countryUniQueTournament);

		ScheduledEventsCommon.Category categoryUniQueTournament = ScheduledEventsCommon.Category
				.builder()
				.id(categoryUniQueTournamentId)
				.name(categoryUniQueTournamentResponse.getName())
				.slug(categoryUniQueTournamentResponse.getSlug())
				.sport(sportCategoryUniQueTournament)
				.country(countryCategoryUniQueTournament)
				.flag(categoryUniQueTournamentFlag)
				.build();


		ScheduledEventsCommon.UniqueTournament uniqueTournament = ScheduledEventsCommon.UniqueTournament.builder()
				.name(uniqueTournamentResponse.getName())
				.slug(uniqueTournamentResponse.getSlug())
				.category(categoryUniQueTournament)
				.userCount(uniqueTournamentResponse.getUserCount())
				.crowdsourcingEnabled(uniqueTournamentResponse.isCrowdsourcingEnabled())
				.hasPerformanceGraphFeature(uniqueTournamentResponse.isHasPerformanceGraphFeature())
				.id(uniqueTournamentResponse.getId())
				.hasEventPlayerStatistics(uniqueTournamentResponse.isHasEventPlayerStatistics())
				.displayInverseHomeAwayTeams(uniqueTournamentResponse.isDisplayInverseHomeAwayTeams())
				.build();

		return ScheduledEventsEntity.TournamentEntity
				.builder()
				.name(tournamentResponse.getName())
				.slug(tournamentResponse.getSlug())
				.category(category)
				.uniqueTournament(uniqueTournament)
				.priority(tournamentResponse.getPriority())
				.id(tournamentResponse.getId())
				.build();
	}


	private static ScheduledEventsEntity.TeamEntity populatedTeamEntity(ScheduledEventsResponse.Team team) {
		ScheduledEventsCommonResponse.Sport sport = team.getSport();
		ScheduledEventsCommonResponse.Country country = team.getCountry();
		ScheduledEventsCommonResponse.TeamColors teamColors = team.getTeamColors();
		ScheduledEventsCommonResponse.FieldTranslations fieldTranslations = team.getFieldTranslations();


		ScheduledEventsEntity.TeamEntity teamEntity = ScheduledEventsEntity.TeamEntity.builder()
				.name(team.getName())
				.slug(team.getSlug())
				.shortName(team.getShortName())
				.sport(ScheduledEventsConverter.fromSportScheduledEventsCommonResponse(sport))
				.userCount(team.getUserCount())
				.nameCode(team.getNameCode())
				.disabled(team.isDisabled())
				.national(team.isNational())
				.type(team.getType())
				.id(team.getId())
				.country(ScheduledEventsConverter.fromCountryScheduledEventsCommonResponse(country))
				.subTeams(null)
				.teamColors(ScheduledEventsConverter.fromTeamColorsScheduledEventsCommonResponse(teamColors))
				.build();

		if (Objects.isNull(fieldTranslations)) {
			teamEntity.setFieldTranslations(null);
		} else {

			ScheduledEventsCommonResponse.Translations nameTranslation = fieldTranslations.getNameTranslation();
			ScheduledEventsCommonResponse.Translations shortNameTranslation = fieldTranslations.getShortNameTranslation();
			ScheduledEventsCommon.FieldTranslations.FieldTranslationsBuilder fieldTranslationsBuilder = ScheduledEventsCommon.FieldTranslations.builder();


			if (!Objects.isNull(nameTranslation)) {
				fieldTranslationsBuilder
						.nameTranslation(ScheduledEventsCommon.Translations.builder()
								.ru(nameTranslation.getRu())
								.ar(nameTranslation.getAr())
								.build()
						);
			} else {
				fieldTranslationsBuilder.nameTranslation(null);
			}

			if (!Objects.isNull(shortNameTranslation)) {
				fieldTranslationsBuilder.shortNameTranslation(ScheduledEventsCommon.Translations.builder()
						.ru(shortNameTranslation.getRu())
						.ar(shortNameTranslation.getAr())
						.build()
				);
			} else {
				fieldTranslationsBuilder.shortNameTranslation(null);
			}

			teamEntity.setFieldTranslations(fieldTranslationsBuilder.build());
		}
		return teamEntity;
	}


}
