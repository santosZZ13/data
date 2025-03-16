package org.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamStats {
	private String id;
	private int teamId;
	private DateRange dateRange;
	private Metrics metrics;
	private Date updatedAt;


	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class DateRange {
		private Date start;
		private Date end;
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Metrics {
		private int matchesPlayed;
		private int wins;
		private int losses;
		private int draws;
		private Goals goalsScored;
		private Goals goalsConceded;
		private Over15 over15Period1;
		private Over05 over05Period1;
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Goals {
		private int period1;
		private int period2;
		private int avgPeriod1;
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Over15 {
		private int count;
		private double percentage;
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Over05 {
		private int count;
		private double percentage;
	}
}
