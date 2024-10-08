package org.data.dto.ex;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.common.model.BaseResponse;
import org.data.common.model.PaginationSortDto;
import org.data.dto.history.HistoryFetchCommonDto;

import java.util.List;

public interface FetchExBetWithSfEventByDate {

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
		private ResponseData data;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ResponseData {
		private int totalMatches;
		private List<FetchHistoryExWithSf> metaData;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class FetchHistoryExWithSf {
		private HistoryFetchCommonDto.HistoryFetchEventDto historyFetchData;
		private ExCommonDto.ExMatchDetailsResponseDto exData;
	}
}
