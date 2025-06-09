package org.data.util.utils;

import lombok.extern.log4j.Log4j2;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Utility class for handling date and time conversions, formatting, and validations.
 * Supports Instant, ZonedDateTime, LocalDateTime, LocalDate, timestamps, and ISO 8601 strings.
 */
@Log4j2
public class DateUtils {
	// Common date-time formats
	private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String TIME_FORMAT = "HH:mm:ss";

	// Formatters
	private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern(ISO_FORMAT).withZone(ZoneOffset.UTC);
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneOffset.UTC);
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

	// Default time zone
	private static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");


	/**
	 * Converts an Instant to an ISO 8601 string in UTC.
	 *
	 * @param instant The Instant to convert.
	 * @return ISO 8601 string (e.g., "2025-06-08T10:52:00Z") or null if input is null.
	 */
	public static String toUtcISOString(Instant instant) {
		if (instant == null) {
			return null;
		}
		return ISO_FORMATTER.format(instant);
	}


	/**
	 * Parses an ISO 8601 string to an Instant.
	 *
	 * @param utcDate ISO 8601 string (e.g., "2025-06-08T10:52:00Z").
	 * @return Instant or null if input is invalid.
	 */
	public static Instant fromUtcISOString(String utcDate) {
		if (utcDate == null || utcDate.isEmpty()) {
			return null;
		}
		try {
			return Instant.parse(utcDate);
		} catch (DateTimeParseException e) {
			log.warn("Invalid ISO 8601 format: {}", utcDate);
			return null;
		}
	}

	/**
	 * Gets the current time in UTC as an ISO 8601 string.
	 *
	 * @return Current UTC time (e.g., "2025-06-08T10:52:00Z").
	 */
	public static String nowUtc() {
		return toUtcISOString(Instant.now());
	}

	/**
	 * Gets the current time in a specified time zone as an ISO 8601 string.
	 *
	 * @param zoneId Time zone ID (e.g., "Asia/Ho_Chi_Minh").
	 * @return Current time in the specified zone or null if zoneId is invalid.
	 */
	public static String nowInZone(String zoneId) {
		try {
			ZoneId zone = zoneId == null ? DEFAULT_ZONE : ZoneId.of(zoneId);
			return ISO_FORMATTER.withZone(zone).format(Instant.now());
		} catch (Exception e) {
			log.warn("Invalid zoneId: {}", zoneId);
			return null;
		}
	}

	/**
	 * Converts a timestamp (epoch seconds) to an ISO 8601 string in UTC.
	 *
	 * @param timestamp Epoch seconds since 1970-01-01.
	 * @return ISO 8601 string or null if timestamp is invalid.
	 */
	public static String fromTimestampToUtc(long timestamp) {
		try {
			return toUtcISOString(Instant.ofEpochSecond(timestamp));
		} catch (Exception e) {
			log.warn("Invalid timestamp: {}", timestamp);
			return null;
		}
	}

	/**
	 * Converts a timestamp (epoch seconds) to LocalDateTime in UTC.
	 *
	 * @param timestamp Epoch seconds.
	 * @return LocalDateTime in UTC or null if timestamp is null.
	 */
	public static LocalDateTime toUtcLocalDateTime(Long timestamp) {
		if (timestamp == null) {
			return null;
		}
		return Instant.ofEpochSecond(timestamp).atZone(DEFAULT_ZONE).toLocalDateTime();
	}

	/**
	 * Converts a timestamp (epoch seconds) to ZonedDateTime in UTC.
	 *
	 * @param timestamp Epoch seconds.
	 * @return ZonedDateTime in UTC or null if timestamp is null.
	 */
	public static ZonedDateTime toUtcZonedDateTime(Long timestamp) {
		if (timestamp == null) {
			return null;
		}
		return Instant.ofEpochSecond(timestamp).atZone(DEFAULT_ZONE);
	}

	/**
	 * Converts a ZonedDateTime to a timestamp (epoch seconds).
	 *
	 * @param zonedDateTime The ZonedDateTime to convert.
	 * @return Epoch seconds or null if input is null.
	 */
	public static Long toTimestamp(ZonedDateTime zonedDateTime) {
		if (zonedDateTime == null) {
			return null;
		}
		return zonedDateTime.toInstant().getEpochSecond();
	}

	/**
	 * Converts a ZonedDateTime to a timestamp (epoch milliseconds).
	 *
	 * @param zonedDateTime The ZonedDateTime to convert.
	 * @return Epoch milliseconds or null if input is null.
	 */
	public static Long toTimestampMillis(ZonedDateTime zonedDateTime) {
		if (zonedDateTime == null) {
			return null;
		}
		return zonedDateTime.toInstant().toEpochMilli();
	}

	/**
	 * Converts a LocalDateTime to ZonedDateTime with a specified time zone.
	 *
	 * @param localDateTime The LocalDateTime to convert.
	 * @param zoneId        Time zone ID (e.g., "UTC", "Asia/Ho_Chi_Minh").
	 * @return ZonedDateTime or null if inputs are invalid.
	 */
	public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime, String zoneId) {
		if (localDateTime == null || zoneId == null) {
			return null;
		}
		try {
			return localDateTime.atZone(ZoneId.of(zoneId));
		} catch (Exception e) {
			log.warn("Invalid zoneId: {}", zoneId);
			return null;
		}
	}

	/**
	 * Converts a ZonedDateTime to LocalDateTime.
	 *
	 * @param zonedDateTime The ZonedDateTime to convert.
	 * @return LocalDateTime or null if input is null.
	 */
	public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
		if (zonedDateTime == null) {
			return null;
		}
		return zonedDateTime.toLocalDateTime();
	}

	/**
	 * Converts a LocalDate to ZonedDateTime at start of day in a specified time zone.
	 *
	 * @param localDate The LocalDate to convert.
	 * @param zoneId    Time zone ID.
	 * @return ZonedDateTime at 00:00:00 or null if inputs are invalid.
	 */
	public static ZonedDateTime toZonedDateTime(LocalDate localDate, String zoneId) {
		if (localDate == null || zoneId == null) {
			return null;
		}
		try {
			return localDate.atStartOfDay(ZoneId.of(zoneId));
		} catch (Exception e) {
			log.warn("Invalid zoneId: {}", zoneId);
			return null;
		}
	}

	/**
	 * Converts a ZonedDateTime to Instant.
	 *
	 * @param zonedDateTime The ZonedDateTime to convert.
	 * @return Instant or null if input is null.
	 */
	public static Instant toInstant(ZonedDateTime zonedDateTime) {
		if (zonedDateTime == null) {
			return null;
		}
		return zonedDateTime.toInstant();
	}

	/**
	 * Parses a custom-formatted date-time string to ZonedDateTime.
	 *
	 * @param dateTimeString The date-time string (e.g., "2025-06-08 10:52:00").
	 * @param pattern        The format pattern (e.g., "yyyy-MM-dd HH:mm:ss").
	 * @param zoneId         Time zone ID.
	 * @return ZonedDateTime or null if parsing fails.
	 */
	public static ZonedDateTime parseToZonedDateTime(String dateTimeString, String pattern, String zoneId) {
		if (dateTimeString == null || pattern == null || zoneId == null) {
			return null;
		}
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
			LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);
			return localDateTime.atZone(ZoneId.of(zoneId));
		} catch (Exception e) {
			log.warn("Failed to parse date-time: {}, pattern: {}, zoneId: {}", dateTimeString, pattern, zoneId);
			return null;
		}
	}

	/**
	 * Formats a ZonedDateTime to a custom pattern.
	 *
	 * @param zonedDateTime The ZonedDateTime to format.
	 * @param pattern       The format pattern (e.g., "yyyy-MM-dd HH:mm:ss").
	 * @return Formatted string or null if inputs are invalid.
	 */
	public static String formatZonedDateTime(ZonedDateTime zonedDateTime, String pattern) {
		if (zonedDateTime == null || pattern == null) {
			return null;
		}
		try {
			return zonedDateTime.format(DateTimeFormatter.ofPattern(pattern));
		} catch (Exception e) {
			log.warn("Invalid format pattern: {}", pattern);
			return null;
		}
	}

	/**
	 * Converts a ZonedDateTime to another time zone.
	 *
	 * @param zonedDateTime The ZonedDateTime to convert.
	 * @param targetZoneId  Target time zone ID.
	 * @return ZonedDateTime in the target zone or null if inputs are invalid.
	 */
	public static ZonedDateTime convertToZone(ZonedDateTime zonedDateTime, String targetZoneId) {
		if (zonedDateTime == null || targetZoneId == null) {
			return null;
		}
		try {
			return zonedDateTime.withZoneSameInstant(ZoneId.of(targetZoneId));
		} catch (Exception e) {
			log.warn("Invalid targetZoneId: {}", targetZoneId);
			return null;
		}
	}

	/**
	 * Formats an Instant to a date string (yyyy-MM-dd) in UTC or a specified time zone.
	 *
	 * @param instant The Instant to format.
	 * @param zoneId  Time zone ID (optional, defaults to UTC).
	 * @return Date string or null if input is invalid.
	 */
	public static String formatDate(Instant instant, String zoneId) {
		if (instant == null) {
			return null;
		}
		ZoneId zone = zoneId == null ? DEFAULT_ZONE : ZoneId.of(zoneId);
		return DATE_FORMATTER.withZone(zone).format(instant);
	}

	/**
	 * Gets the start of the day for a ZonedDateTime.
	 *
	 * @param zonedDateTime The ZonedDateTime.
	 * @return ZonedDateTime at 00:00:00 or null if input is null.
	 */
	public static ZonedDateTime startOfDay(ZonedDateTime zonedDateTime) {
		if (zonedDateTime == null) {
			return null;
		}
		return zonedDateTime.truncatedTo(ChronoUnit.DAYS);
	}

	/**
	 * Gets the end of the day for a ZonedDateTime.
	 *
	 * @param zonedDateTime The ZonedDateTime.
	 * @return ZonedDateTime at 23:59:59.999 or null if input is null.
	 */
	public static ZonedDateTime endOfDay(ZonedDateTime zonedDateTime) {
		if (zonedDateTime == null) {
			return null;
		}
		return zonedDateTime.truncatedTo(ChronoUnit.DAYS).plusDays(1).minusNanos(1);
	}

	/**
	 * Calculates the duration between two ZonedDateTimes in seconds.
	 *
	 * @param start The start ZonedDateTime.
	 * @param end   The end ZonedDateTime.
	 * @return Duration in seconds or null if inputs are invalid.
	 */
	public static Long durationInSeconds(ZonedDateTime start, ZonedDateTime end) {
		if (start == null || end == null) {
			return null;
		}
		return Duration.between(start, end).getSeconds();
	}

	/**
	 * Checks if a ZonedDateTime is before another.
	 *
	 * @param first  The first ZonedDateTime.
	 * @param second The second ZonedDateTime.
	 * @return True if first is before second, false otherwise or if inputs are null.
	 */
	public static boolean isBefore(ZonedDateTime first, ZonedDateTime second) {
		if (first == null || second == null) {
			return false;
		}
		return first.isBefore(second);
	}

	/**
	 * Checks if a ZonedDateTime is after another.
	 *
	 * @param first  The first ZonedDateTime.
	 * @param second The second ZonedDateTime.
	 * @return True if first is after second, false otherwise or if inputs are null.
	 */
	public static boolean isAfter(ZonedDateTime first, ZonedDateTime second) {
		if (first == null || second == null) {
			return false;
		}
		return first.isAfter(second);
	}

	/**
	 * Validates an ISO 8601 string.
	 *
	 * @param utcDate ISO 8601 string.
	 * @return True if valid, false otherwise.
	 */
	public static boolean isValid(String utcDate) {
		return fromUtcISOString(utcDate) != null;
	}

	/**
	 * Extracts date components from a ZonedDateTime.
	 *
	 * @param zonedDateTime The ZonedDateTime.
	 * @return Array of [year, month, day] or null if input is null.
	 */
	public static int[] extractDateComponents(ZonedDateTime zonedDateTime) {
		if (zonedDateTime == null) {
			return null;
		}
		return new int[]{
				zonedDateTime.getYear(),
				zonedDateTime.getMonthValue(),
				zonedDateTime.getDayOfMonth()
		};
	}

	/**
	 * Extracts time components from a ZonedDateTime.
	 *
	 * @param zonedDateTime The ZonedDateTime.
	 * @return Array of [hour, minute, second] or null if input is null.
	 */
	public static int[] extractTimeComponents(ZonedDateTime zonedDateTime) {
		if (zonedDateTime == null) {
			return null;
		}
		return new int[]{
				zonedDateTime.getHour(),
				zonedDateTime.getMinute(),
				zonedDateTime.getSecond()
		};
	}

}