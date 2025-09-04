package org.data.dto.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.dto.common.ex.ExBetMatchDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImportMatchesJsonFile {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		private MultipartFile file;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private int totalMatches;
		private int totalMatchesSaved;
		private List<ExBetMatchDto> matches;
	}
}
