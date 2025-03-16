package org.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {
	private Integer teamId;
	private String name;
	private String shortName;
	private Country country;
	private FetchStatus fetchStatus;
	private Date updatedAt;

	@Data
	public static class Country {
		private String alpha2;
		private String name;
	}

	@Data
	public static class FetchStatus {
		private Boolean isFullyFetched = false;
		private Date lastFetchedDate;
		private Long lastMatchTimestamp;
		private Long futureMatchTimestamp;
	}
}
