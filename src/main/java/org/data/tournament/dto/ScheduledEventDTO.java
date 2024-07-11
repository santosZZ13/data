package org.data.tournament.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public interface ScheduledEventDTO {

	@EqualsAndHashCode(callSuper = true)
	@Data
	@SuperBuilder
	@NoArgsConstructor
	@AllArgsConstructor
	public class Request extends PaginationDTO {
		private String date;
	}

	public class Response {

	}
}
