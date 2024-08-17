package org.data.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponseWithPagination  {
	private int pageNumber;
	private int pageSize;
	private int totals;
}
