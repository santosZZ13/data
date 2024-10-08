package org.data.persistent.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.persistent.common.ScheduledEventsCommonEntity;
import org.data.persistent.entity.base.ScheduledEventsBaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class HistoryEntity extends ScheduledEventsBaseEntity {
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
