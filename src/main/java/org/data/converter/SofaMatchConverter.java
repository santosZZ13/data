package org.data.converter;

import lombok.extern.log4j.Log4j2;
import org.data.dto.common.sofa.SofaMatchDto;
import org.data.persistent.entity.SofaScheduledMatchEntity;
import org.data.external.sofa.model.SofaMatchResponseDetail;
import org.data.util.NormalizeTeamName;
import org.data.util.utils.DateUtils;

import java.time.ZonedDateTime;

@Log4j2
public class SofaMatchConverter {
	public static SofaScheduledMatchEntity toEntity(SofaMatchResponseDetail dto) {
		if (dto == null) {
			return null;
		}

		ZonedDateTime startTimestamp = DateUtils.toUtcZonedDateTime(dto.getStartTimestamp());
		return SofaScheduledMatchEntity.builder()
				.matchId(dto.getMatchId())
				.startTimestamp(startTimestamp)
				.tournamentInfo(dto.getTournament() != null ? SofaScheduledMatchEntity.TournamentEntity.builder()
						.id(dto.getTournament().getId())
						.name(dto.getTournament().getName())
						.build() : null)
				.sessionInfo(dto.getSeason() != null ? SofaScheduledMatchEntity.SessionEntity.builder()
						.id(dto.getSeason() .getId())
						.name(dto.getSeason() .getName())
						.build() : null)
				.roundInfo(dto.getRoundInfo() != null ? SofaScheduledMatchEntity.RoundEntity.builder()
						.round(dto.getRoundInfo().getRound())
						.build() : null)
				.status(dto.getStatus() != null ? SofaScheduledMatchEntity.StatusEntity.builder()
						.description(dto.getStatus().getDescription())
						.type(dto.getStatus().getType())
						.build() : null)
				.homeTeam(dto.getHomeTeam() != null ? SofaScheduledMatchEntity.TeamEntity.builder()
						.id(dto.getHomeTeam().getId())
						.name(dto.getHomeTeam().getName())
						.normalizedName(NormalizeTeamName.normalize(dto.getHomeTeam() != null ? dto.getHomeTeam().getName() : null))
						.shortName(dto.getHomeTeam().getShortName() != null ? dto.getHomeTeam().getShortName() : null)
						.build() : null)
				.awayTeam(dto.getAwayTeam() != null ? SofaScheduledMatchEntity.TeamEntity.builder()
						.id(dto.getAwayTeam().getId())
						.name(dto.getAwayTeam().getName())
						.normalizedName(NormalizeTeamName.normalize(dto.getAwayTeam() != null ? dto.getAwayTeam().getName() : null))
						.shortName(dto.getAwayTeam().getShortName() != null ? dto.getAwayTeam().getShortName() : null)
						.build() : null)
				.homeScore(dto.getHomeScore() != null ? SofaScheduledMatchEntity.ScoreEntity.builder()
						.current(dto.getHomeScore().getCurrent())
						.display(dto.getHomeScore().getDisplay())
						.period1(dto.getHomeScore().getPeriod1())
						.period2(dto.getHomeScore().getPeriod2())
						.normalTime(dto.getHomeScore().getNormalTime())
						.extra1(dto.getHomeScore().getExtra1())
						.extra2(dto.getHomeScore().getExtra2())
						.overtime(dto.getHomeScore().getOvertime())
						.penalties(dto.getHomeScore().getPenalties())
						.scoreEmpty(dto.getHomeScore().getScoreEmpty())
						.build() : null)
				.awayScore(dto.getAwayScore() != null ? SofaScheduledMatchEntity.ScoreEntity.builder()
						.current(dto.getAwayScore().getCurrent())
						.display(dto.getAwayScore().getDisplay())
						.period1(dto.getAwayScore().getPeriod1())
						.period2(dto.getAwayScore().getPeriod2())
						.normalTime(dto.getAwayScore().getNormalTime())
						.extra1(dto.getAwayScore().getExtra1())
						.extra2(dto.getAwayScore().getExtra2())
						.overtime(dto.getAwayScore().getOvertime())
						.penalties(dto.getAwayScore().getPenalties())
						.scoreEmpty(dto.getAwayScore().getScoreEmpty())
						.build() : null)
				.build();
	}

	public static SofaMatchDto toDto(SofaScheduledMatchEntity entity) {
		if (entity == null) return null;

		return SofaMatchDto.builder()
				.matchId(entity.getMatchId())
				.startTimestamp(entity.getStartTimestamp())
				.tournamentInfo(entity.getTournamentInfo() != null ? SofaMatchDto.TournamentDto.builder()
						.id(entity.getTournamentInfo().getId())
						.name(entity.getTournamentInfo().getName())
						.build() : null)
				.sessionInfo(entity.getSessionInfo() != null ? SofaMatchDto.SessionDto.builder()
						.id(entity.getSessionInfo().getId())
						.name(entity.getSessionInfo().getName())
						.build() : null)
				.roundInfo(entity.getRoundInfo() != null ? SofaMatchDto.RoundDto.builder()
						.round(entity.getRoundInfo().getRound())
						.build() : null)
				.status(entity.getStatus() != null ? SofaMatchDto.StatusDto.builder()
						.description(entity.getStatus().getDescription())
						.type(entity.getStatus().getType())
						.build() : null)
				.homeTeam(entity.getHomeTeam() != null ? SofaMatchDto.TeamDto.builder()
						.id(entity.getHomeTeam().getId())
						.name(entity.getHomeTeam().getName())
						.normalizedName(entity.getHomeTeam().getNormalizedName())
						.shortName(entity.getHomeTeam().getShortName() != null ? entity.getHomeTeam().getShortName() : null)
						.build() : null)
				.awayTeam(entity.getAwayTeam() != null ? SofaMatchDto.TeamDto.builder()
						.id(entity.getAwayTeam().getId())
						.name(entity.getAwayTeam().getName())
						.normalizedName(entity.getAwayTeam().getNormalizedName())
						.shortName(entity.getAwayTeam().getShortName() != null ? entity.getAwayTeam().getShortName() : null)
						.build() : null)
				.homeScore(entity.getHomeScore() != null ? SofaMatchDto.ScoreDto.builder()
						.current(entity.getHomeScore().getCurrent())
						.display(entity.getHomeScore().getDisplay())
						.period1(entity.getHomeScore().getPeriod1())
						.period2(entity.getHomeScore().getPeriod2())
						.normalTime(entity.getHomeScore().getNormalTime())
						.extra1(entity.getHomeScore().getExtra1())
						.extra2(entity.getHomeScore().getExtra2())
						.overtime(entity.getHomeScore().getOvertime())
						.penalties(entity.getHomeScore().getPenalties())
						.scoreEmpty(entity.getHomeScore().getScoreEmpty())
						.build() : null)
				.awayScore(entity.getAwayScore() != null ? SofaMatchDto.ScoreDto.builder()
						.current(entity.getAwayScore().getCurrent())
						.display(entity.getAwayScore().getDisplay())
						.period1(entity.getAwayScore().getPeriod1())
						.period2(entity.getAwayScore().getPeriod2())
						.normalTime(entity.getAwayScore().getNormalTime())
						.extra1(entity.getAwayScore().getExtra1())
						.extra2(entity.getAwayScore().getExtra2())
						.overtime(entity.getAwayScore().getOvertime())
						.penalties(entity.getAwayScore().getPenalties())
						.scoreEmpty(entity.getAwayScore().getScoreEmpty())
						.build() : null)
				.build();
	}
}
