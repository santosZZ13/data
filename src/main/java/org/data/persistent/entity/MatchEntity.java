package org.data.persistent.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "matches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchEntity {
	@Id
	private String id;
	@Indexed(unique = true)
	private int matchId;
	@Indexed
	private Date date;
	private Long tournamentId;
	private Long seasonId;
	private Integer round;
	private Status status;
	private Integer winnerCode;
	private Teams teams;
	private Time time;
	private Date updatedAt;


	@Data
	public static class Status {
		private Integer code;
		private String type;
	}

	@Data
	public static class Teams {
		private TeamDetails home;
		private TeamDetails away;
	}

	@Data
	public static class TeamDetails {
		private int teamId;
		private Score score;
	}

	@Data
	public static class Score {
		private Integer period1;
		private Integer period2;
		private Integer normaltime;
	}

	@Data
	public static class Time {
		private Long startTimestamp;
		private Integer injuryTime1;
		private Integer injuryTime2;
	}
}
