package org.data.response.sf.parent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SofaMatchResponse {
	private List<SofaMatchResponseDetailDto> events;
	private Boolean hasNextPage;
}
