package org.data.sofa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.data.common.model.GenericResponseWrapper;
import org.data.common.model.Pagination;
import org.data.conts.FetchStatus;

import java.time.LocalDateTime;


public interface GetHistoryFetchEventDto {
	@EqualsAndHashCode(callSuper = true)
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request extends Pagination {
		private String status;

	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		@JsonProperty("data")
		private HistoryFetchEventDto historyFetchEventDto;
	}

	@EqualsAndHashCode(callSuper = true)
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class HistoryFetchEventDto extends GenericResponseWrapper {
		private Integer teamId;
		private Long timeElapsed;
		private Integer total;
		private FetchStatus fetchStatus;
		private LocalDateTime createdDate;
		private LocalDateTime updatedDate;
	}
}
