package org.data.response.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class ExBetMatchResponse {
	private Integer sid;
	private Integer cid;
	private Integer tid;
	private Integer iid;
	private Integer countdown;
	private String state;
	private String series;
	private String vd;
	private Integer streaming;
	private Integer chatMid;
	private Integer gifMid;
	private Integer graphMid;
	private Boolean inplay;
	private Boolean video;
	private Boolean nv;
	private String scoreId;
	private String tnName;
	private Integer tnPriority;
	private ExBetTeamResponse home;
	private ExBetTeamResponse away;
	private ExRoundResponse round;
	private ExBetMarketInfoResponse marketInfo;
	private ExBetMidsResponse mids;
	private List<ExBetGiftResponse> gifs;
	private List<ExBetVideoResponse> videos;
	private List<ExBetAnchorResponse> anchors;
	private String name;
	private long kickoffTime;
}
