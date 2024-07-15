package org.data.tournament.persistent.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.tournament.persistent.entity.base.ScheduledEventsBaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
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
	private ScheduledEventsCommonEntity.TournamentEntity tournament;
	private ScheduledEventsCommonEntity.SeasonEntity season;
	private ScheduledEventsCommonEntity.RoundInfoEntity roundInfo;
	private String customId;
	private ScheduledEventsCommonEntity.StatusEntity status;
	private Integer winnerCode;
	private ScheduledEventsCommonEntity.TeamEntity homeTeam;
	private ScheduledEventsCommonEntity.TeamEntity awayTeam;
	private ScheduledEventsCommonEntity.ScoreEntity homeScore;
	private ScheduledEventsCommonEntity.ScoreEntity awayScore;
	private ScheduledEventsCommonEntity.TimeEntity time;
	private ScheduledEventsCommonEntity.ChangesEntity changes;

}
