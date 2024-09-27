package org.data.sofa.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentResponse {
	private String name;
	private String slug;
	private CategoryResponse category;
	private UniqueTournamentResponse uniqueTournament;
	private Integer priority;
	private Boolean isGroup;
	private Boolean isLive;
	private Integer id;
}
