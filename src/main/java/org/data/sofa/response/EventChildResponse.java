package org.data.sofa.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.data.dto.sf.RoundInfoResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventChildResponse {
	private TournamentResponse tournament;
	private SeasonResponse season;
	private RoundInfoResponse roundInfo;
	private String customId;
	private StatusResponse status;
	private Integer winnerCode;
	private TeamResponse homeTeam;
	private TeamResponse awayTeam;
	private ScoreResponse homeScore;
	private ScoreResponse awayScore;
	private TimeResponse time;
	private ChangesResponse changes;
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
}
