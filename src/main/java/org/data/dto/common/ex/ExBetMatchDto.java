package org.data.dto.common.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExBetMatchDto {
	private int id;
	private String tournamentName;
	private long kickoffTime;
	private int homeId;
	private String homeName;
	private int awayId;
	private String awayName;
	private String status;
	private ExBetMatchCommonDto.RoundDto round;
	private boolean isFavorite;
	private String time;
	private Boolean isMatched;
	private ExBetMatchCommonDto.SofaData sofaData;
}
