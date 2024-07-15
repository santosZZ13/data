package org.data.tournament.converter;

import org.data.tournament.dto.ScheduledEventsCommonResponse;
import org.data.tournament.persistent.entity.ScheduledEventsCommonEntity;

import java.util.Objects;

public class ScheduledEventsConverter {

	public static ScheduledEventsCommonEntity.Sport fromSportScheduledEventsCommonResponse(ScheduledEventsCommonResponse.SportResponse sportResponse) {
		return ScheduledEventsCommonEntity.Sport.builder()
				.id(Objects.isNull(sportResponse) ? null : sportResponse.getId())
				.name(Objects.isNull(sportResponse) ? null : sportResponse.getName())
				.slug(Objects.isNull(sportResponse) ? null : sportResponse.getSlug())
				.build();
	}

	public static ScheduledEventsCommonEntity.Country fromCountryScheduledEventsCommonResponse(ScheduledEventsCommonResponse.Country countryResponse) {
		return ScheduledEventsCommonEntity.Country.builder()
				.alpha2(Objects.isNull(countryResponse) ? null : countryResponse.getAlpha2())
				.alpha3(Objects.isNull(countryResponse) ? null : countryResponse.getAlpha3())
				.name(Objects.isNull(countryResponse) ? null : countryResponse.getName())
				.build();
	}

	public static ScheduledEventsCommonEntity.TeamColors fromTeamColorsScheduledEventsCommonResponse(ScheduledEventsCommonResponse.TeamColors teamColorsResponse) {
		return ScheduledEventsCommonEntity.TeamColors.builder()
				.primary(Objects.isNull(teamColorsResponse) ? null : teamColorsResponse.getPrimary())
				.secondary(Objects.isNull(teamColorsResponse) ? null : teamColorsResponse.getSecondary())
				.text(Objects.isNull(teamColorsResponse) ? null : teamColorsResponse.getText())
				.build();
	}
}
