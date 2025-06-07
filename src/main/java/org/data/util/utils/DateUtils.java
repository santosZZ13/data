package org.data.util.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 *
 */
public class DateUtils {
	// Định dạng mặc định cho ISO 8601 (UTC)
	private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern(ISO_FORMAT).withZone(ZoneOffset.UTC);

	// Định dạng mặc định cho ngày
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneOffset.UTC);

	/**
	 * Chuyển thời gian sang UTC (ISO 8601) để lưu trữ hoặc trả về FE
	 *
	 * @param instant - Thời gian (Instant)
	 * @return Chuỗi thời gian ở UTC (ISO 8601)
	 */
	public static String toUtcISOString(Instant instant) {
		if (instant == null) {
			return null;
		}
		return ISO_FORMATTER.format(instant);
	}

	/**
	 * Chuyển chuỗi ISO 8601 (UTC) thành Instant
	 *
	 * @param utcDate - Chuỗi thời gian ở UTC (ISO 8601)
	 * @return Instant
	 */
	public static Instant fromUtcISOString(String utcDate) {
		if (utcDate == null || utcDate.isEmpty()) {
			return null;
		}
		try {
			return Instant.parse(utcDate);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Lấy thời gian hiện tại ở UTC
	 *
	 * @return Chuỗi thời gian hiện tại ở UTC (ISO 8601)
	 */
	public static String nowUtc() {
		return toUtcISOString(Instant.now());
	}

	/**
	 * Chuyển timestamp (số giây) sang UTC ISO string
	 *
	 * @param timestamp - Timestamp (số giây từ 1970-01-01)
	 * @return Chuỗi thời gian ở UTC (ISO 8601)
	 */
	public static String fromTimestampToUtc(long timestamp) {
		return toUtcISOString(Instant.ofEpochSecond(timestamp));
	}

	/**
	 * Định dạng ngày (không bao gồm thời gian)
	 *
	 * @param instant - Thời gian (Instant)
	 * @return Chuỗi ngày ở định dạng "yyyy-MM-dd"
	 */
	public static String formatDate(Instant instant) {
		if (instant == null) {
			return null;
		}
		return DATE_FORMATTER.format(instant);
	}

	/**
	 * Định dạng ngày (không bao gồm thời gian) với múi giờ tùy chỉnh
	 *
	 * @param instant - Thời gian (Instant)
	 * @param zoneId  - Múi giờ (ví dụ: "GMT+07:00")
	 * @return Chuỗi ngày ở định dạng "yyyy-MM-dd"
	 */
	public static String formatDate(Instant instant, String zoneId) {
		if (instant == null || zoneId == null) {
			return null;
		}
		return DateTimeFormatter.ofPattern(DATE_FORMAT)
				.withZone(ZoneId.of(zoneId))
				.format(instant);
	}

	/**
	 * Chuyển timestamp (số giây) sang LocalDateTime ở UTC
	 *
	 * @param startTimestamp - Timestamp (số giây từ 1970-01-01)
	 * @return LocalDateTime ở UTC
	 */
	public static LocalDateTime toUtcLocalDateTime(Long startTimestamp) {
		if (Objects.isNull(startTimestamp)) {
			return null;
		}
		return Instant.ofEpochSecond(startTimestamp)
				.atZone(ZoneId.of("UTC"))
				.toLocalDateTime();
	}


	public static ZonedDateTime toUtcZonedDateTime(Long startTimestamp) {
		if (Objects.isNull(startTimestamp)) {
			return null;
		}
		return Instant.ofEpochSecond(startTimestamp)
				.atZone(ZoneId.of("UTC"));
	}

	/**
	 * Kiểm tra thời gian có hợp lệ không
	 *
	 * @param utcDate - Chuỗi thời gian ở UTC (ISO 8601)
	 * @return True nếu hợp lệ, false nếu không
	 */
	public static boolean isValid(String utcDate) {
		try {
			Instant.parse(utcDate);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}