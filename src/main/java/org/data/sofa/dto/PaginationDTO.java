package org.data.sofa.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class PaginationDTO {

	@Builder.Default
	private Integer pageNumber = 1;

	@Builder.Default
	private Integer pageSize = 12;

	private Integer totalResults;

}
