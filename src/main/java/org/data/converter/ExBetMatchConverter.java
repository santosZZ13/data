package org.data.converter;

import lombok.extern.log4j.Log4j2;
import org.data.dto.common.ex.ExBetMatchCommonDto;
import org.data.dto.common.ex.ExBetMatchDto;
import org.data.persistent.entity.ExBetMatchEntity;
import org.data.util.utils.DateUtils;


@Log4j2
public class ExBetMatchConverter {
	public static ExBetMatchDto toDto(ExBetMatchEntity entity) {
		try {
			return ExBetMatchDto.builder()
					.id(entity.getMatchId())
					.tournamentName(entity.getTournamentName())
					.kickoffTime(DateUtils.toTimestamp(entity.getKickoffTime()))
					.homeId(entity.getHomeId())
					.homeName(entity.getHomeName())
					.awayId(entity.getAwayId())
					.awayName(entity.getAwayName())
					.isFavorite(entity.isFavorite())
					.status(entity.getStatus())
					.isMatched(entity.getIsMatched())
					.sofaData(
							entity.getSofaDataEntity() == null ? null :
									ExBetMatchCommonDto.SofaData
											.builder()
											.sofaMatchId(entity.getSofaDataEntity().getSofaMatchId())
											.sofaHomeId(entity.getSofaDataEntity().getSofaHomeId())
											.sofaHomeName(entity.getSofaDataEntity().getSofaHomeName())
											.sofaAwayId(entity.getSofaDataEntity().getSofaAwayId())
											.sofaAwayName(entity.getSofaDataEntity().getSofaAwayName())
											.homeScore(
													entity.getSofaDataEntity().getHomeScore() == null ? null :
															ExBetMatchCommonDto.ScoreData
																	.builder()
																	.current(entity.getSofaDataEntity().getHomeScore().getCurrent())
																	.display(entity.getSofaDataEntity().getHomeScore().getDisplay())
																	.period1(entity.getSofaDataEntity().getHomeScore().getPeriod1())
																	.period2(entity.getSofaDataEntity().getHomeScore().getPeriod2())
																	.normalTime(entity.getSofaDataEntity().getHomeScore().getNormalTime())
																	.extra1(entity.getSofaDataEntity().getHomeScore().getExtra1())
																	.extra2(entity.getSofaDataEntity().getHomeScore().getExtra2())
																	.overtime(entity.getSofaDataEntity().getHomeScore().getOvertime())
																	.penalties(entity.getSofaDataEntity().getHomeScore().getPenalties())
																	.scoreEmpty(entity.getSofaDataEntity().getHomeScore().getScoreEmpty())
																	.aggregated(entity.getSofaDataEntity().getHomeScore().getAggregated())
																	.build()
											)
											.awayScore(
													entity.getSofaDataEntity().getAwayScore() == null ? null :
															ExBetMatchCommonDto.ScoreData
																	.builder()
																	.current(entity.getSofaDataEntity().getAwayScore().getCurrent())
																	.display(entity.getSofaDataEntity().getAwayScore().getDisplay())
																	.period1(entity.getSofaDataEntity().getAwayScore().getPeriod1())
																	.period2(entity.getSofaDataEntity().getAwayScore().getPeriod2())
																	.normalTime(entity.getSofaDataEntity().getAwayScore().getNormalTime())
																	.extra1(entity.getSofaDataEntity().getAwayScore().getExtra1())
																	.extra2(entity.getSofaDataEntity().getAwayScore().getExtra2())
																	.overtime(entity.getSofaDataEntity().getAwayScore().getOvertime())
																	.penalties(entity.getSofaDataEntity().getAwayScore().getPenalties())
																	.scoreEmpty(entity.getSofaDataEntity().getAwayScore().getScoreEmpty())
																	.aggregated(entity.getSofaDataEntity().getAwayScore().getAggregated())
																	.build()
											)
											.build()

					)
					.round(ExBetMatchCommonDto.RoundDto.builder()
							.roundName(entity.getRound().getRoundName())
							.roundType(entity.getRound().getRoundType())
							.build()
					)
					.build();
		} catch (Exception e) {
			throw new RuntimeException("Has error in converting from Entity to Dto in ExBetMatchDto");
		}
	}

	public static ExBetMatchEntity toEntity(ExBetMatchDto dto) {
		try {
			return ExBetMatchEntity.builder()
					.matchId(dto.getId())
					.tournamentName(dto.getTournamentName())
					.kickoffTime(DateUtils.toUtcZonedDateTime(dto.getKickoffTime()))
					.homeId(dto.getHomeId())
					.homeName(dto.getHomeName())
					.awayId(dto.getAwayId())
					.awayName(dto.getAwayName())
					.isFavorite(dto.isFavorite())
					.status(dto.getStatus() == null ? "notstarted" : dto.getStatus())
					.isMatched(dto.getIsMatched())
					.sofaDataEntity(
							dto.getSofaData() == null ? null :
									ExBetMatchEntity.SofaDataEntity
											.builder()
											.sofaMatchId(dto.getSofaData().getSofaMatchId())
											.sofaHomeId(dto.getSofaData().getSofaHomeId())
											.sofaHomeName(dto.getSofaData().getSofaHomeName())
											.sofaAwayId(dto.getSofaData().getSofaAwayId())
											.sofaAwayName(dto.getSofaData().getSofaAwayName())
											.homeScore(
													dto.getSofaData().getHomeScore() == null ? null :
															ExBetMatchEntity.ScoreEntity
																	.builder()
																	.current(dto.getSofaData().getHomeScore().getCurrent())
																	.display(dto.getSofaData().getHomeScore().getDisplay())
																	.period1(dto.getSofaData().getHomeScore().getPeriod1())
																	.period2(dto.getSofaData().getHomeScore().getPeriod2())
																	.normalTime(dto.getSofaData().getHomeScore().getNormalTime())
																	.extra1(dto.getSofaData().getHomeScore().getExtra1())
																	.extra2(dto.getSofaData().getHomeScore().getExtra2())
																	.overtime(dto.getSofaData().getHomeScore().getOvertime())
																	.penalties(dto.getSofaData().getHomeScore().getPenalties())
																	.scoreEmpty(dto.getSofaData().getHomeScore().getScoreEmpty())
																	.aggregated(dto.getSofaData().getHomeScore().getAggregated())
																	.build()
											)
											.awayScore(
													dto.getSofaData().getAwayScore() == null ? null :
															ExBetMatchEntity.ScoreEntity
																	.builder()
																	.current(dto.getSofaData().getAwayScore().getCurrent())
																	.display(dto.getSofaData().getAwayScore().getDisplay())
																	.period1(dto.getSofaData().getAwayScore().getPeriod1())
																	.period2(dto.getSofaData().getAwayScore().getPeriod2())
																	.normalTime(dto.getSofaData().getAwayScore().getNormalTime())
																	.extra1(dto.getSofaData().getAwayScore().getExtra1())
																	.extra2(dto.getSofaData().getAwayScore().getExtra2())
																	.overtime(dto.getSofaData().getAwayScore().getOvertime())
																	.penalties(dto.getSofaData().getAwayScore().getPenalties())
																	.scoreEmpty(dto.getSofaData().getAwayScore().getScoreEmpty())
																	.aggregated(dto.getSofaData().getAwayScore().getAggregated())
																	.build()
											)
											.build()

					)
					.round(ExBetMatchEntity.RoundEntity.builder()
							.roundName(dto.getRound().getRoundName())
							.roundType(dto.getRound().getRoundType())
							.build()
					)
					.build();
		} catch (Exception e) {
			throw new RuntimeException("Has error in converting from Entity to Dto in ExBetMatchDto");
		}
	}
}
