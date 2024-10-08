package org.data.dto.ex;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.common.model.BaseResponse;
import org.data.common.model.PaginationSortDto;

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
		private List<ExCommonDto.ExMatchDetailsResponseDto> matches;

		public static ExBetSfDetail of(List<ExCommonDto.ExMatchDetailsResponseDto> matches) {
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
		private List<ExCommonDto.ExMatchResponseDto> matches;

		public static ExBetDetail of(List<ExCommonDto.ExMatchResponseDto> matches) {
			return ExBetDetail
					.builder()
					.total(matches.size())
					.matches(matches)
					.build();
		}
	}


}
