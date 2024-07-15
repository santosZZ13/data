package org.data.eightBet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.tournament.dto.Tournament8xResponse;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledEventInPlayResponse {
	private String msg;
	private int code;
	private Data data;

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Data {
		private List<TournamentResponse> tournamentResponses;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TournamentResponse {
		private int sid;
		private int tid;
		private int cid;
		private String name;
		private boolean favorite;
		private int priority;
		private int count;
		private List<MatchResponse> matchResponses;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MatchResponse {
		private int sid;
		private int cid;
		private int tid;
		private int iid;
		private int countdown;
		private String state;
		private String series;
		private String vd;
		private int streaming;
		private int chatMid;
		private int gifMid;
		private int graphMid;
		private boolean inplay;
		private boolean video;
		private boolean nv;
		private String scoreId;
		private String tnName;
		private int tnPriority;
		private TeamResponse home;
		private TeamResponse away;
		private RoundResponse roundResponse;
		private MarketInfoResponse marketInfoResponse;
		private MidsResponse mids;
		private List<Object> videos;
		private List<Tournament8xResponse.Anchor> anchors;
		private String name;
		private long kickoffTime;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TeamResponse {
		private int id;
		private int cid;
		private String name;
		private Tournament8xResponse.Jersey jersey;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class JerseyResponse {
		private String base;
		private String sleeve;
		private String style;
		private String styleColor;
		private String shirtType;
		private String sleeveDetails;
	}


	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoundResponse {
		private String roundType;
		private String roundName;
		private String roundGroup;
		private String roundGroupName;
		private String roundNumber;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MarketInfoResponse {
		private boolean cr;
		private boolean ot;
		private boolean pk;
		private boolean otcr;
		private boolean ad;
		private boolean redCard;
		private boolean otRedCard;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MidsResponse {
		private int fmid;
		private int bmid;
		private int amid;
		private int cmid;
		private int dmid;
		private int jmid;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class AnchorResponse {
		private String houseId;
		private int liveStatus;
		private int visitHistory;
		private String playStreamAddress;
		private String playStreamAddress2;
		private String userImage;
		private String houseName;
		private String houseImage;
		private String nickName;
		private String anchorTypeName;
		private int fansCount;
		private String anchorTitle;
		private String houseIntroduction;
		private String languageType;
		private List<String> vendors;
	}
}
