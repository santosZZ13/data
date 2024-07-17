package org.data.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public class TimeUtil {
	public static LocalDateTime convertUnixTimestampToLocalDateTime(long unixTimestamp) {
		Instant instant = Instant.ofEpochSecond(unixTimestamp);
		return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static long convertLocalDateTimeToUnixTimestamp(LocalDateTime localDateTime) {
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		return instant.getEpochSecond();
	}

	/**
	 * date format: yyyy-MM-dd
	 * @param date
	 * @return
	 */
	public static LocalDateTime convertStringToLocalDateTime(String date) {
		if (Objects.equals(date, "today")) {
			return LocalDateTime.now();
		} else {
			return LocalDateTime.parse(date + "T00:00:00");
		}
	}
}
