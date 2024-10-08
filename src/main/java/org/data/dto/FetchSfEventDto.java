package org.data.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.common.model.BaseResponse;
import org.data.dto.history.HistoryFetchCommonDto;

public interface FetchSfEventDto {

	String FETCH_SF_EVENT = "/api/v1/fetch/{teamId}";


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
		private HistoryFetchCommonDto.HistoryFetchEventDto historyFetchEvent;
	}
}
