package org.data.dto.sf;

import org.data.persistent.common.ScheduledEventsCommonEntity;
import org.data.util.TimeUtil;

import java.util.Objects;

public class ScheduledEventsResponseConverter {
	public static SofaCommonResponse.TournamentResponse fromTournamentEntity(ScheduledEventsCommonEntity.TournamentEntity tournamentEntity) {

		ScheduledEventsCommonEntity.Category entityCategory = tournamentEntity.getCategory();
		ScheduledEventsCommonEntity.Sport sportEntity = entityCategory.getSport();
		ScheduledEventsCommonEntity.Country countryEntity = entityCategory.getCountry();
		String flagResponse = entityCategory.getFlag();
		Integer idResponse = entityCategory.getId();


		SofaCommonResponse.SportResponse sport = fromSportEntity(sportEntity);
		SofaCommonResponse.Country country = fromCountryEntity(countryEntity);


		SofaCommonResponse.Category category = SofaCommonResponse.Category
				.builder()
				.id(idResponse)
				.name(entityCategory.getName())
				.slug(entityCategory.getSlug())
				.sport(sport)
				.country(country)
				.flag(flagResponse)
				.build();


		ScheduledEventsCommonEntity.UniqueTournament uniqueTournamentResponse = tournamentEntity.getUniqueTournament();
		ScheduledEventsCommonEntity.Category categoryUniQueTournamentResponse = uniqueTournamentResponse.getCategory();
		ScheduledEventsCommonEntity.Sport sportResponseUniQueTournament = categoryUniQueTournamentResponse.getSport();
		ScheduledEventsCommonEntity.Country countryUniQueTournament = categoryUniQueTournamentResponse.getCountry();
		Integer categoryUniQueTournamentId = categoryUniQueTournamentResponse.getId();
		String categoryUniQueTournamentFlag = categoryUniQueTournamentResponse.getFlag();

		SofaCommonResponse.SportResponse sportCategoryUniQueTournament = fromSportEntity(sportResponseUniQueTournament);
		SofaCommonResponse.Country countryCategoryUniQueTournament = fromCountryEntity(countryUniQueTournament);

		SofaCommonResponse.Category categoryUniQueTournament = SofaCommonResponse.Category
				.builder()
				.id(categoryUniQueTournamentId)
				.name(categoryUniQueTournamentResponse.getName())
				.slug(categoryUniQueTournamentResponse.getSlug())
				.sport(sportCategoryUniQueTournament)
				.country(countryCategoryUniQueTournament)
				.flag(categoryUniQueTournamentFlag)
				.build();

		SofaCommonResponse.UniqueTournament uniqueTournament = SofaCommonResponse.UniqueTournament.builder()
				.name(uniqueTournamentResponse.getName())
				.slug(uniqueTournamentResponse.getSlug())
				.category(categoryUniQueTournament)
				.userCount(uniqueTournamentResponse.getUserCount())
				.crowdsourcingEnabled(uniqueTournamentResponse.getCrowdsourcingEnabled())
				.hasPerformanceGraphFeature(uniqueTournamentResponse.getHasPerformanceGraphFeature())
				.id(uniqueTournamentResponse.getId())
				.hasEventPlayerStatistics(uniqueTournamentResponse.getHasEventPlayerStatistics())
				.displayInverseHomeAwayTeams(uniqueTournamentResponse.getDisplayInverseHomeAwayTeams())
				.build();


		return SofaCommonResponse.TournamentResponse
				.builder()
				.name(tournamentEntity.getName())
				.slug(tournamentEntity.getSlug())
				.category(category)
				.uniqueTournament(uniqueTournament)
				.priority(tournamentEntity.getPriority())
				.id(tournamentEntity.getId())
				.build();
	}

	public static SofaCommonResponse.SeasonResponse fromSesSeasonEntity(ScheduledEventsCommonEntity.SeasonEntity seasonEntity) {
		return Objects.isNull(seasonEntity) ? null : SofaCommonResponse.SeasonResponse.builder()
				.name(seasonEntity.getName())
				.year(seasonEntity.getYear())
				.editor(seasonEntity.getEditor())
				.id(seasonEntity.getId())
				.build();
	}

	public static SofaCommonResponse.Status fromStatusEntity(ScheduledEventsCommonEntity.StatusEntity statusEntity) {
		return Objects.isNull(statusEntity) ? null : SofaCommonResponse.Status.builder()
				.code(statusEntity.getCode())
				.description(statusEntity.getDescription())
				.type(statusEntity.getType())
				.build();

	}

	public static SofaCommonResponse.TeamResponse fromTeamEntity(ScheduledEventsCommonEntity.TeamEntity teamEntity) {
		ScheduledEventsCommonEntity.Sport sportResponse = teamEntity.getSport();
		ScheduledEventsCommonEntity.Country country = teamEntity.getCountry();
		ScheduledEventsCommonEntity.TeamColors teamColors = teamEntity.getTeamColors();
		ScheduledEventsCommonEntity.FieldTranslations fieldTranslations = teamEntity.getFieldTranslations();


		SofaCommonResponse.TeamResponse teamResponse = SofaCommonResponse.TeamResponse.builder()
				.name(teamEntity.getName())
				.slug(teamEntity.getSlug())
				.shortName(teamEntity.getShortName())
				.sportResponse(fromSportEntity(sportResponse))
				.userCount(teamEntity.getUserCount())
				.nameCode(teamEntity.getNameCode())
				.disabled(teamEntity.getDisabled())
				.national(teamEntity.getNational())
				.type(teamEntity.getType())
				.id(teamEntity.getId())
				.country(fromCountryEntity(country))
				.subTeamResponses(null)
				.teamColors(fromTeamColorsEntity(teamColors))
				.build();

		if (Objects.isNull(fieldTranslations)) {
			teamEntity.setFieldTranslations(null);
		} else {

			ScheduledEventsCommonEntity.Translations nameTranslation = fieldTranslations.getNameTranslation();
			ScheduledEventsCommonEntity.Translations shortNameTranslation = fieldTranslations.getShortNameTranslation();
			ScheduledEventsCommonEntity.FieldTranslations.FieldTranslationsBuilder fieldTranslationsBuilder = ScheduledEventsCommonEntity.FieldTranslations.builder();


			if (!Objects.isNull(nameTranslation)) {
				fieldTranslationsBuilder
						.nameTranslation(ScheduledEventsCommonEntity.Translations.builder()
								.ru(nameTranslation.getRu())
								.ar(nameTranslation.getAr())
								.build()
						);
			} else {
				fieldTranslationsBuilder.nameTranslation(null);
			}

			if (!Objects.isNull(shortNameTranslation)) {
				fieldTranslationsBuilder.shortNameTranslation(ScheduledEventsCommonEntity.Translations.builder()
						.ru(shortNameTranslation.getRu())
						.ar(shortNameTranslation.getAr())
						.build()
				);
			} else {
				fieldTranslationsBuilder.shortNameTranslation(null);
			}

			teamEntity.setFieldTranslations(fieldTranslationsBuilder.build());
		}
		return teamResponse;
	}

	public static SofaCommonResponse.RoundInfo fromRoundInfoResponse(ScheduledEventsCommonEntity.RoundInfoEntity roundInfoEntity) {
		return Objects.isNull(roundInfoEntity) ? null : SofaCommonResponse.RoundInfo.builder()
				.round(roundInfoEntity.getRound())
				.name(roundInfoEntity.getName())
				.cupRoundType(roundInfoEntity.getCupRoundType())
				.build();
	}

	public static SofaCommonResponse.SportResponse fromSportEntity(ScheduledEventsCommonEntity.Sport sport) {
		return SofaCommonResponse.SportResponse.builder()
				.id(Objects.isNull(sport) ? null : sport.getId())
				.name(Objects.isNull(sport) ? null : sport.getName())
				.slug(Objects.isNull(sport) ? null : sport.getSlug())
				.build();
	}


	public static SofaCommonResponse.Country fromCountryEntity(ScheduledEventsCommonEntity.Country country) {
		return SofaCommonResponse.Country.builder()
				.alpha2(Objects.isNull(country) ? null : country.getAlpha2())
				.alpha3(Objects.isNull(country) ? null : country.getAlpha3())
				.name(Objects.isNull(country) ? null : country.getName())
				.build();
	}


	public static SofaCommonResponse.TeamColors fromTeamColorsEntity(ScheduledEventsCommonEntity.TeamColors teamColorsEntity) {
		return SofaCommonResponse.TeamColors.builder()
				.primary(Objects.isNull(teamColorsEntity) ? null : teamColorsEntity.getPrimary())
				.secondary(Objects.isNull(teamColorsEntity) ? null : teamColorsEntity.getSecondary())
				.text(Objects.isNull(teamColorsEntity) ? null : teamColorsEntity.getText())
				.build();
	}

	public static SofaCommonResponse.Score fromScoreResponse(ScheduledEventsCommonEntity.ScoreEntity scoreEntity) {
		return SofaCommonResponse.Score.builder()
				.current(scoreEntity.getCurrent())
				.display(scoreEntity.getDisplay())
				.period1(scoreEntity.getPeriod1())
				.period2(scoreEntity.getPeriod2())
				.normaltime(scoreEntity.getNormaltime())
				.build();
	}

	public static SofaCommonResponse.Time fromTimeResponse(ScheduledEventsCommonEntity.TimeEntity teamEntity) {
		return SofaCommonResponse.Time.builder()
				.injuryTime1(teamEntity.getInjuryTime1())
				.injuryTime2(teamEntity.getInjuryTime2())
				.currentPeriodStartTimestamp(TimeUtil.convertLocalDateTimeToUnixTimestamp(teamEntity.getCurrentPeriodStartTimestamp()))
				.build();
	}

	public static SofaCommonResponse.Changes fromChangesResponse(ScheduledEventsCommonEntity.ChangesEntity changesEntity) {
		return SofaCommonResponse.Changes.builder()
				.changeTimestamp(TimeUtil.convertLocalDateTimeToUnixTimestamp(changesEntity.getChangeTimestamp()))
				.changes(changesEntity.getChanges())
				.build();
	}
}
