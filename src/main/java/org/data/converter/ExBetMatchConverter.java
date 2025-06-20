package org.data.converter;

import lombok.extern.log4j.Log4j2;
import org.data.dto.common.ExBetMatchCommonDto;
import org.data.dto.common.ExBetMatchDto;
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
