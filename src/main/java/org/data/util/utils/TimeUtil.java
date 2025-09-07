package org.data.util.utils;

import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
	 *
	 * @param date
	 * @return
	 */
	public static LocalDateTime convertStringToLocalDateTime(@NotNull String date) {
		if (Objects.isNull(date) || date.isEmpty()) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return LocalDateTime.parse(date, formatter);
	}

	public static LocalDateTime convertStringToLocalDateTimeFormal(@NotNull String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return LocalDateTime.parse(date, formatter);
	}

//	        "startTime": "2025-09-05T20:42:00+07:00",
//					"endTime": "2025-09-05T21:42:00+07:00",
	public static LocalDateTime convertStringToLocalDateTimeWithZone(@NotNull String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
		LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
		return localDateTime
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}

	public static long calculateTimeElapsed(Instant start, Instant finish) {
		return Duration.between(start, finish).toMillis();
	}

	public static LocalDateTime convertStringToLocalDateTimeFormalWithZone(@NotNull String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
		return localDateTime
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}

	public static String convertLocalDateTimeToString(LocalDateTime localDateTime) {
		if (Objects.isNull(localDateTime)) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return localDateTime.format(formatter);
	}


	public static String convertIntoXet(String date) {
		return date.replace("-", "");
	}

	public static boolean isEqual(LocalDateTime kickoffTime, LocalDateTime kickOffMatch) {
		return kickoffTime.getYear() == kickOffMatch.getYear() && kickoffTime.getMonthValue() == kickOffMatch.getMonthValue()
				&& kickoffTime.getDayOfMonth() == kickOffMatch.getDayOfMonth() && kickoffTime.getHour() == kickOffMatch.getHour()
				&& kickoffTime.getMinute() == kickOffMatch.getMinute();
	}
}
