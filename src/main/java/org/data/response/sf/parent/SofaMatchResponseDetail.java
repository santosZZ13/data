package org.data.response.sf.parent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.data.response.sf.child.StatusResponse;
import org.data.response.sf.child.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SofaMatchResponseDetail {
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
	private VarInProgressResponse varInProgress; // Thêm trường mới
	private Integer awayRedCards; // Thêm trường mới
	private StatusTimeResponse statusTime; // Thêm trường mới
	private String lastPeriod; // Thêm trường mới
}
