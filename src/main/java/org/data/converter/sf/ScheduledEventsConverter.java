package org.data.converter.sf;

import org.data.dto.sf.SofaCommonResponse;
import org.data.persistent.common.ScheduledEventsCommonEntity;

import java.util.Objects;

public class ScheduledEventsConverter {

	public static ScheduledEventsCommonEntity.Sport fromSportScheduledEventsCommonResponse(SofaCommonResponse.SportResponse sportResponse) {
		return ScheduledEventsCommonEntity.Sport.builder()
				.id(Objects.isNull(sportResponse) ? null : sportResponse.getId())
				.name(Objects.isNull(sportResponse) ? null : sportResponse.getName())
				.slug(Objects.isNull(sportResponse) ? null : sportResponse.getSlug())
				.build();
	}

	public static ScheduledEventsCommonEntity.Country fromCountryScheduledEventsCommonResponse(SofaCommonResponse.Country countryResponse) {
		return ScheduledEventsCommonEntity.Country.builder()
				.alpha2(Objects.isNull(countryResponse) ? null : countryResponse.getAlpha2())
				.alpha3(Objects.isNull(countryResponse) ? null : countryResponse.getAlpha3())
				.name(Objects.isNull(countryResponse) ? null : countryResponse.getName())
				.build();
	}

	public static ScheduledEventsCommonEntity.TeamColors fromTeamColorsScheduledEventsCommonResponse(SofaCommonResponse.TeamColors teamColorsResponse) {
		return ScheduledEventsCommonEntity.TeamColors.builder()
				.primary(Objects.isNull(teamColorsResponse) ? null : teamColorsResponse.getPrimary())
				.secondary(Objects.isNull(teamColorsResponse) ? null : teamColorsResponse.getSecondary())
				.text(Objects.isNull(teamColorsResponse) ? null : teamColorsResponse.getText())
				.build();
	}
}
