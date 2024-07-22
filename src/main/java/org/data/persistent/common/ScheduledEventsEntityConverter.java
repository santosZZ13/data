package org.data.persistent.common;

import org.data.sofa.dto.ScheduledEventsCommonResponse;
import org.data.util.TimeUtil;

import java.util.Objects;

public class ScheduledEventsEntityConverter {
	public static ScheduledEventsCommonEntity.TournamentEntity fromTournamentResponse(ScheduledEventsCommonResponse.TournamentResponse tournamentResponse) {

		ScheduledEventsCommonResponse.Category categoryResponse = tournamentResponse.getCategory();
		ScheduledEventsCommonResponse.SportResponse sportResponse = categoryResponse.getSportResponse();
		ScheduledEventsCommonResponse.Country countryResponse = categoryResponse.getCountry();
		String flagResponse = categoryResponse.getFlag();
		Integer idResponse = categoryResponse.getId();


		ScheduledEventsCommonEntity.Sport sport = fromSportResponse(sportResponse);
		ScheduledEventsCommonEntity.Country country = fromCountryResponse(countryResponse);


		ScheduledEventsCommonEntity.Category category = ScheduledEventsCommonEntity.Category
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
		ScheduledEventsCommonResponse.SportResponse sportResponseUniQueTournament = categoryUniQueTournamentResponse.getSportResponse();
		ScheduledEventsCommonResponse.Country countryUniQueTournament = categoryUniQueTournamentResponse.getCountry();
		Integer categoryUniQueTournamentId = categoryUniQueTournamentResponse.getId();
		String categoryUniQueTournamentFlag = categoryUniQueTournamentResponse.getFlag();

		ScheduledEventsCommonEntity.Sport sportCategoryUniQueTournament = fromSportResponse(sportResponseUniQueTournament);
		ScheduledEventsCommonEntity.Country countryCategoryUniQueTournament = fromCountryResponse(countryUniQueTournament);

		ScheduledEventsCommonEntity.Category categoryUniQueTournament = ScheduledEventsCommonEntity.Category
				.builder()
				.id(categoryUniQueTournamentId)
				.name(categoryUniQueTournamentResponse.getName())
				.slug(categoryUniQueTournamentResponse.getSlug())
				.sport(sportCategoryUniQueTournament)
				.country(countryCategoryUniQueTournament)
				.flag(categoryUniQueTournamentFlag)
				.build();

		ScheduledEventsCommonEntity.UniqueTournament uniqueTournament = ScheduledEventsCommonEntity.UniqueTournament.builder()
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


		return ScheduledEventsCommonEntity.TournamentEntity
				.builder()
				.name(tournamentResponse.getName())
				.slug(tournamentResponse.getSlug())
				.category(category)
				.uniqueTournament(uniqueTournament)
				.priority(tournamentResponse.getPriority())
				.id(tournamentResponse.getId())
				.build();
	}

	public static ScheduledEventsCommonEntity.SeasonEntity fromSesSeasonResponse(ScheduledEventsCommonResponse.SeasonResponse seasonResponse) {
		return Objects.isNull(seasonResponse) ? null : ScheduledEventsCommonEntity.SeasonEntity.builder()
				.name(seasonResponse.getName())
				.year(seasonResponse.getYear())
				.editor(seasonResponse.getEditor())
				.id(seasonResponse.getId())
				.build();
	}

	public static ScheduledEventsCommonEntity.StatusEntity fromStatusResponse(ScheduledEventsCommonResponse.Status statusResponse) {
		return Objects.isNull(statusResponse) ? null : ScheduledEventsCommonEntity.StatusEntity.builder()
				.code(statusResponse.getCode())
				.description(statusResponse.getDescription())
				.type(statusResponse.getType())
				.build();

	}

	public static ScheduledEventsCommonEntity.TeamEntity fromTeamResponse(ScheduledEventsCommonResponse.TeamResponse teamResponse) {
		ScheduledEventsCommonResponse.SportResponse sportResponse = teamResponse.getSportResponse();
		ScheduledEventsCommonResponse.Country country = teamResponse.getCountry();
		ScheduledEventsCommonResponse.TeamColors teamColors = teamResponse.getTeamColors();
		ScheduledEventsCommonResponse.FieldTranslations fieldTranslations = teamResponse.getFieldTranslations();


		ScheduledEventsCommonEntity.TeamEntity teamEntity = ScheduledEventsCommonEntity.TeamEntity.builder()
				.name(teamResponse.getName())
				.slug(teamResponse.getSlug())
				.shortName(teamResponse.getShortName())
				.sport(fromSportResponse(sportResponse))
				.userCount(teamResponse.getUserCount())
				.nameCode(teamResponse.getNameCode())
				.disabled(teamResponse.isDisabled())
				.national(teamResponse.isNational())
				.type(teamResponse.getType())
				.id(teamResponse.getId())
				.country(fromCountryResponse(country))
				.subTeams(null)
				.teamColors(fromTeamColorsResponse(teamColors))
				.build();

		if (Objects.isNull(fieldTranslations)) {
			teamEntity.setFieldTranslations(null);
		} else {

			ScheduledEventsCommonResponse.Translations nameTranslation = fieldTranslations.getNameTranslation();
			ScheduledEventsCommonResponse.Translations shortNameTranslation = fieldTranslations.getShortNameTranslation();
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
		return teamEntity;
	}

	public static ScheduledEventsCommonEntity.RoundInfoEntity fromRoundInfoResponse(ScheduledEventsCommonResponse.RoundInfo roundInfoResponse) {
		return Objects.isNull(roundInfoResponse) ? null : ScheduledEventsCommonEntity.RoundInfoEntity.builder()
				.round(roundInfoResponse.getRound())
				.name(roundInfoResponse.getName())
				.cupRoundType(roundInfoResponse.getCupRoundType())
				.build();
	}

	public static ScheduledEventsCommonEntity.Sport fromSportResponse(ScheduledEventsCommonResponse.SportResponse sportResponse) {
		return ScheduledEventsCommonEntity.Sport.builder()
				.id(Objects.isNull(sportResponse) ? null : sportResponse.getId())
				.name(Objects.isNull(sportResponse) ? null : sportResponse.getName())
				.slug(Objects.isNull(sportResponse) ? null : sportResponse.getSlug())
				.build();
	}


	public static ScheduledEventsCommonEntity.Country fromCountryResponse(ScheduledEventsCommonResponse.Country countryResponse) {
		return ScheduledEventsCommonEntity.Country.builder()
				.alpha2(Objects.isNull(countryResponse) ? null : countryResponse.getAlpha2())
				.alpha3(Objects.isNull(countryResponse) ? null : countryResponse.getAlpha3())
				.name(Objects.isNull(countryResponse) ? null : countryResponse.getName())
				.build();
	}


	public static ScheduledEventsCommonEntity.TeamColors fromTeamColorsResponse(ScheduledEventsCommonResponse.TeamColors teamColorsResponse) {
		return ScheduledEventsCommonEntity.TeamColors.builder()
				.primary(Objects.isNull(teamColorsResponse) ? null : teamColorsResponse.getPrimary())
				.secondary(Objects.isNull(teamColorsResponse) ? null : teamColorsResponse.getSecondary())
				.text(Objects.isNull(teamColorsResponse) ? null : teamColorsResponse.getText())
				.build();
	}

	public static ScheduledEventsCommonEntity.ScoreEntity fromScoreResponse(ScheduledEventsCommonResponse.Score scoreResponse) {
		return ScheduledEventsCommonEntity.ScoreEntity.builder()
				.current(scoreResponse.getCurrent())
				.display(scoreResponse.getDisplay())
				.period1(scoreResponse.getPeriod1())
				.period2(scoreResponse.getPeriod2())
				.normaltime(scoreResponse.getNormaltime())
				.build();
	}

	public static ScheduledEventsCommonEntity.TimeEntity fromTimeResponse(ScheduledEventsCommonResponse.Time timeResponse) {
		return ScheduledEventsCommonEntity.TimeEntity.builder()
				.injuryTime1(timeResponse.getInjuryTime1())
				.injuryTime2(timeResponse.getInjuryTime2())
				.currentPeriodStartTimestamp(TimeUtil.convertUnixTimestampToLocalDateTime(timeResponse.getCurrentPeriodStartTimestamp()))
				.build();
	}

	public static ScheduledEventsCommonEntity.ChangesEntity fromChangesResponse(ScheduledEventsCommonResponse.Changes changesResponse) {
		return ScheduledEventsCommonEntity.ChangesEntity.builder()
				.changeTimestamp(TimeUtil.convertUnixTimestampToLocalDateTime(changesResponse.getChangeTimestamp()))
				.changes(changesResponse.getChanges())
				.build();
	}
}
