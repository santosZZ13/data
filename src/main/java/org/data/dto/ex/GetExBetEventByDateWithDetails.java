package org.data.dto.ex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.common.model.BaseResponse;
import org.data.common.model.PaginationSortDto;
import org.data.dto.sf.SfEventsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface GetExBetEventByDateWithDetails {

	@EqualsAndHashCode(callSuper = true)
	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request extends PaginationSortDto {
		private String date;
	}

	@EqualsAndHashCode(callSuper = true)
	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response extends BaseResponse {
		private ExBetDetailsResponseDto data;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ExBetDetailsResponseDto {
		private int total;
		private String date;
		private List<ExBetMatchDetailsResponseDto> matches;
	}

	@EqualsAndHashCode(callSuper = true)
	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ExBetMatchDetailsResponseDto extends ExBetCommonDto.ExBetMatchResponseDto {
		private SfEventsDto.EventDto sofaDetail;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class SfScheduledEventDto {
		private String tntName;
		private String seasonName;
		private Integer round;
		private String status;
		private TeamDetailsDto homeDetails;
		private TeamDetailsDto awayDetails;
		//		private ScoreDetailsDto homeScoreDetails;
//		private GetEventScheduledDto.ScoreDetailsDto awayScoreDetails;
		private Integer id;
		private LocalDateTime kickOffMatch;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class TeamDetailsDto {
		@JsonProperty("id")
		private Integer idTeam;
		private String name;
		private String country;
	}
}
