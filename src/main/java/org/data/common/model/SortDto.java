package org.data.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public  class SortDto {
	private String sortField;

	@Builder.Default
	private Direction sortDirection = Direction.DESC;

	public enum Direction {
		ASC, DESC
	}
}
