package org.data.dto.sf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.response.sf.parent.SofaMatchResponseDetailDto;

import java.util.List;

public interface GetSofaMatchesByDate {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		private String date;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private int size;
		private List<SofaMatchResponseDetailDto> matches;
	}
}
