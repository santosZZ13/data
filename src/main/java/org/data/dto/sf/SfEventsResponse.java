package org.data.dto.sf;

import lombok.*;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class SfEventsResponse {

	private List<EventResponse> events;
	private Boolean hasNextPage;

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	public static class EventResponse {
		private SofaCommonResponse.TournamentResponse tournament;
		private SofaCommonResponse.SeasonResponse season;
		private SofaCommonResponse.RoundInfo roundInfo;
		private String customId;
		private SofaCommonResponse.Status status;
		private Integer winnerCode;
		private SofaCommonResponse.TeamResponse homeTeam;
		private SofaCommonResponse.TeamResponse awayTeam;
		private SofaCommonResponse.Score homeScore;
		private SofaCommonResponse.Score awayScore;
		private SofaCommonResponse.Time time;
		private SofaCommonResponse.Changes changes;
		private Boolean hasGlobalHighlights;
		private Boolean hasEventPlayerStatistics;
		private Boolean hasEventPlayerHeatMap;
		private Integer detailId;
		private Boolean crowdsourcingDataDisplayEnabled;
		private Integer id;
		private Boolean crowdsourcingEnabled;
		private Long startTimestamp;
		private String slug;
		private Boolean finalResultOnly;
		private Boolean feedLocked;
		private Boolean isEditor;

		public String getTeamNameFromId(Integer teamId, List<SofaCommonResponse.TeamResponse> teams) {
			for (SofaCommonResponse.TeamResponse team : teams) {
				if (team.getId().equals(teamId)) {
					return team.getName();
				}
			}
			return null;
		}
	}
}
