package org.data.response;

import lombok.*;

import java.util.Date;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MatchResponse {
	private String id;
	private Long matchId;
	private Date date;
	private TeamDetails teams;
	private Score score;

	@Data
	public static class TeamDetails {
		private Team home;
		private Team away;
	}

	@Data
	public static class Team {
		private Long teamId;
		private String name;
		private String shortName;
	}

	@Data
	public static class Score {
		private PeriodScore home;
		private PeriodScore away;
	}

	@Data
	public static class PeriodScore {
		private Integer period1;
		private Integer period2;
		private Integer normaltime;
	}
}
