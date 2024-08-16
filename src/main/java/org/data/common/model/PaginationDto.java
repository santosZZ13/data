package org.data.common.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Builder
@NoArgsConstructor
public class PaginationDto {

	@Builder.Default
	private Integer pageNumber = 1;

	@Builder.Default
	private Integer pageSize = 12;

	private Integer totalResults;


	public PaginationDto(Integer pageNumber, Integer pageSize, Integer totalResults) {
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.totalResults = totalResults;
	}
}
