package org.data.tournament.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtil {
	public static LocalDateTime convertUnixTimestampToLocalDateTime(long unixTimestamp) {
		// Convert the Unix timestamp to an Instant
		Instant instant = Instant.ofEpochSecond(unixTimestamp);
		// Convert the Instant to a LocalDateTime using the system default time zone
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	public static long convertLocalDateTimeToUnixTimestamp(LocalDateTime localDateTime) {
		// Convert the LocalDateTime to an Instant
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		// Get the Unix timestamp from the Instant
		return instant.getEpochSecond();
	}
}
