package org.data.dto.ex;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.common.model.BaseResponse;
import org.data.common.model.PaginationSortDto;

import java.time.LocalDateTime;
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
		private List<SfStatusFetchEvent> date;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class SfStatusFetchEvent {
		private int id;
		private String name;
		private String country;
		private LocalDateTime fetchedDate;
		private int totalMatches;
		private int totalMatchesFetched;
		private int totalMatchesNotFetched;
		private int timeConsumed;
	}
}
