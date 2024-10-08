package org.data.dto.ex;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.common.model.BaseResponse;
import org.data.common.model.PaginationSortDto;

import java.util.List;

public interface GetExBetEventByDate {

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
		private ExBetResponseDto data;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ExBetResponseDto {
		private int total;
		private String date;
		private List<ExCommonDto.ExMatchResponseDto> matches;
	}


}
