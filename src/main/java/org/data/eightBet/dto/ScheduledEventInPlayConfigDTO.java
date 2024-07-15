package org.data.eightBet.dto;

import java.util.Map;

public class ScheduledEventInPlayConfigDTO {
	public static final String EIGHT_X_BET = "/product/business/sport/tournament/info";
	public static final int S_ID = 1;
	public static final String SORT = "tournament";
	public static final boolean IN_PLAY = false;
	private static final String LANGUAGE = "en-us";


	public static Map<String, Object> queryParam() {
		return Map.of("sId", S_ID, "sort", SORT,
				"inplay", IN_PLAY, "language", LANGUAGE);
	}
}
