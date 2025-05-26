package org.data.dto.sf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.dto.common.SofaMatchDto;

public interface GetScheduledMatchByName {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		private String name;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private SofaMatchDto matchDto;
	}
}
