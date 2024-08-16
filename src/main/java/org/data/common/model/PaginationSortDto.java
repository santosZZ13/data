package org.data.common.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class PaginationSortDto {
	@Builder.Default
	private Integer pageNumber = 1;

	@Builder.Default
	private Integer pageSize = 12;

	private Integer totalResults;

	private String sortField;

	@Builder.Default
	private SortDto.Direction sortDirection = SortDto.Direction.DESC;

	public enum Direction {
		ASC, DESC
	}
}
