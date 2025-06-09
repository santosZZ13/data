package org.data.converter;

import lombok.extern.log4j.Log4j2;
import org.data.dto.common.ExBetMatchResponseDto;
import org.data.persistent.entity.ExBetMatchEntity;
import org.data.util.utils.DateUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Log4j2
public class ExBetMatchConverter {
	public static ExBetMatchResponseDto toDto(ExBetMatchEntity entity) {
		try {
			return ExBetMatchResponseDto.builder()
					.id(entity.getMatchId())
					.tournamentName(entity.getTournamentName())
					.kickoffTime(DateUtils.toTimestampMillis(entity.getKickoffTime()))
					.homeId(entity.getHomeId())
					.homeName(entity.getHomeName())
					.awayId(entity.getAwayId())
					.awayName(entity.getAwayName())
					.isFavorite(entity.isFavorite())
					.status(entity.getStatus())
					.isMatched(entity.getIsMatched())
					.sofaData(
							entity.getSofaDataEntity() == null ? null :
									ExBetMatchResponseDto.SofaData
											.builder()
											.sofaMatchId(entity.getSofaDataEntity().getSofaMatchId())
											.sofaHomeId(entity.getSofaDataEntity().getSofaHomeId())
											.sofaHomeName(entity.getSofaDataEntity().getSofaHomeName())
											.sofaAwayId(entity.getSofaDataEntity().getSofaAwayId())
											.sofaAwayName(entity.getSofaDataEntity().getSofaAwayName())
											.build()

					)
					.round(ExBetMatchResponseDto.RoundDto.builder()
							.roundName(entity.getRound().getRoundName())
							.roundType(entity.getRound().getRoundType())
							.build()
					)
					.build();
		} catch (Exception e) {
			throw new RuntimeException("Has error in converting from Entity to Dto in ExBetMatchDto");
		}
	}

	public static ExBetMatchEntity toEntity(ExBetMatchResponseDto dto) {
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
					.status(dto.getStatus() == null ? "scheduled" : dto.getStatus())
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
