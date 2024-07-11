package org.data.tournament.converter;

import org.data.tournament.dto.ScheduledEventsCommonResponse;
import org.data.tournament.persistent.entity.ScheduledEventsCommon;

import java.util.Objects;

public class ScheduledEventsConverter {

	public static ScheduledEventsCommon.Sport fromSportScheduledEventsCommonResponse(ScheduledEventsCommonResponse.Sport sportResponse) {
		return ScheduledEventsCommon.Sport.builder()
				.id(Objects.isNull(sportResponse) ? null : sportResponse.getId())
				.name(Objects.isNull(sportResponse) ? null : sportResponse.getName())
				.slug(Objects.isNull(sportResponse) ? null : sportResponse.getSlug())
				.build();
	}

	public static ScheduledEventsCommon.Country fromCountryScheduledEventsCommonResponse(ScheduledEventsCommonResponse.Country countryResponse) {
		return ScheduledEventsCommon.Country.builder()
				.alpha2(Objects.isNull(countryResponse) ? null : countryResponse.getAlpha2())
				.alpha3(Objects.isNull(countryResponse) ? null : countryResponse.getAlpha3())
				.name(Objects.isNull(countryResponse) ? null : countryResponse.getName())
				.build();
	}

	public static ScheduledEventsCommon.TeamColors fromTeamColorsScheduledEventsCommonResponse(ScheduledEventsCommonResponse.TeamColors teamColorsResponse) {
		return ScheduledEventsCommon.TeamColors.builder()
				.primary(Objects.isNull(teamColorsResponse) ? null : teamColorsResponse.getPrimary())
				.secondary(Objects.isNull(teamColorsResponse) ? null : teamColorsResponse.getSecondary())
				.text(Objects.isNull(teamColorsResponse) ? null : teamColorsResponse.getText())
				.build();
	}
}
