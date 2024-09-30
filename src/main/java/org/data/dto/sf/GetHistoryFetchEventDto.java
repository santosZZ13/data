package org.data.dto.sf;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.common.model.BaseResponse;
import org.data.common.model.GenericResponseWithPagination;
import org.data.common.model.PaginationSortDto;
import org.data.conts.FetchStatus;

import java.time.LocalDateTime;
import java.util.List;


public interface GetHistoryFetchEventDto {
	@EqualsAndHashCode(callSuper = true)
	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request extends PaginationSortDto {
		private String status;
		private String fromDate;
		private String toDate;
	}

	@EqualsAndHashCode(callSuper = true)
	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response extends BaseResponse {
		private GetHistoryFetchEventData data;
	}

	@EqualsAndHashCode(callSuper = true)
	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class GetHistoryFetchEventData extends GenericResponseWithPagination {
		private List<HistoryFetchEventDto> history;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class HistoryFetchEventDto {
		private Integer teamId;
		private Long timeElapsed;
		private Integer total;
		private FetchStatus fetchStatus;
		private LocalDateTime createdDate;
	}
}
