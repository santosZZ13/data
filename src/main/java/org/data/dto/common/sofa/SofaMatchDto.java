package org.data.dto.common.sofa;

import lombok.*;

import java.time.ZonedDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SofaMatchDto {
	private Integer matchId;
	private ZonedDateTime startTimestamp;
	private TournamentDto tournamentInfo;
	private SessionDto sessionInfo;
	private RoundDto roundInfo;
	private StatusDto status;
	private TeamDto homeTeam;
	private TeamDto awayTeam;
	private ScoreDto homeScore;
	private ScoreDto awayScore;

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TournamentDto {
		private Integer id;
		private String name;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SessionDto {
		private Integer id;
		private String name;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoundDto {
		private Integer round;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class StatusDto {
		private String description;
		private String type; // finished, notstarted, postponed, canceled, inprogress
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TeamDto {
		private Integer id;
		private String name;
		private String country;
		private String normalizedName;
		private String shortName;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ScoreDto {
		private Integer current;
		private Integer display;
		private Integer period1;
		private Integer period2;
		private Integer normalTime;
		private Integer extra1;
		private Integer extra2;
		private Integer overtime;
		private Integer penalties;
		private Boolean scoreEmpty;
	}
}
