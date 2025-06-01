package org.data.persistent.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.persistent.entity.base.BaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "team")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
//@CompoundIndexes({
//		@CompoundIndex(name = "matchId_idx", def = "{'matchId': 1}", unique = true)
//})
public class TeamEntity extends BaseEntity {
	private String id;
	private SofaTeamEntity sofaTeamInfo;
	private ExBetTeamEntity exBetTeamInfo;

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SofaTeamEntity {
		private Integer teamId;
		private String name;
		private String country;
		private String normalizedName;
		private String shortName;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ExBetTeamEntity {
		private Integer teamId;
		private String name;
		private String normalizedName;
	}
}
