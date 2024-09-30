package org.data.dto.ex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.common.model.BaseResponse;
import org.data.common.model.PaginationSortDto;
import org.data.dto.sf.SfEventsCommonDto;

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

		public static Response of(ExBetDetailsResponseDto data) {
			return Response
					.builder()
					.data(data)
					.build();
		}
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ExBetDetailsResponseDto {
		private int total;
		private String date;
		private ExBetDetail exBetDetail;
		private ExBetSfDetail exBetSfDetail;

		public static ExBetDetailsResponseDto of(String date, ExBetDetail exBetDetail, ExBetSfDetail exBetSfDetail) {
			return ExBetDetailsResponseDto
					.builder()
					.total(exBetDetail.getTotal() + exBetSfDetail.getTotal())
					.date(date)
					.exBetDetail(exBetDetail)
					.exBetSfDetail(exBetSfDetail)
					.build();
		}
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ExBetSfDetail {
		private int total;
		private List<ExBetCommonDto.ExBetMatchDetailsResponseDto> matches;

		public static ExBetSfDetail of(List<ExBetCommonDto.ExBetMatchDetailsResponseDto> matches) {
			return ExBetSfDetail
					.builder()
					.total(matches.size())
					.matches(matches)
					.build();
		}

	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ExBetDetail {
		private int total;
		private List<ExBetCommonDto.ExBetMatchResponseDto> matches;

		public static ExBetDetail of(List<ExBetCommonDto.ExBetMatchResponseDto> matches) {
			return ExBetDetail
					.builder()
					.total(matches.size())
					.matches(matches)
					.build();
		}
	}


}
