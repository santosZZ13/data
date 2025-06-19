package org.data.external.sofa.model;

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
	private List<SofaMatchResponseDetail> events;
	private Boolean hasNextPage;
}
