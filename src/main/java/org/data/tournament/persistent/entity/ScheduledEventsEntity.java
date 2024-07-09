package org.data.tournament.persistent.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.tournament.persistent.entity.base.ScheduledEventsBaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "scheduled_events")
public class ScheduledEventsEntity extends ScheduledEventsBaseEntity {
	@Id
	private String id;
	private TournamentEntity tournament;
	private SeasonEntity season;
	private RoundInfoEntity roundInfo;
	private String customId;
	private StatusEntity status;
	private TeamEntity homeTeam;
	private TeamEntity awayTeam;
	private ScoreEntity homeScore;
	private ScoreEntity awayScore;
	private TimeEntity time;
	private ChangesEntity changes;


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TournamentEntity {
		private String name;
		private String slug;
		private ScheduledEventsCommon.Category category;
		private ScheduledEventsCommon.UniqueTournament uniqueTournament;
		private int priority;
		private int id;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SeasonEntity {
		private String name;
		private String year;
		private boolean editor;
		private int id;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoundInfoEntity {
		private int round;
		private String name;
		private int cupRoundType;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class StatusEntity {
		private int code;
		private String description;
		private String type;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TeamEntity {
		private String name;
		private String slug;
		private String shortName;
		private ScheduledEventsCommon.Sport sport;
		private int userCount;
		private String nameCode;
		private boolean disabled;
		private boolean national;
		private int type;
		private int id;
		private ScheduledEventsCommon.Country country;
		private List<TeamEntity> subTeams;
		private ScheduledEventsCommon.TeamColors teamColors;
		private ScheduledEventsCommon.FieldTranslations fieldTranslations;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ScoreEntity {
		private Integer current;
		private Integer display;
		private Integer period1;
		private Integer period2;
		private Integer normaltime;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TimeEntity {
		private String currentPeriodStartTimestamp;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ChangesEntity {
		private long changeTimestamp;
	}

}
