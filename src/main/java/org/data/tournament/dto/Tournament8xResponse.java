package org.data.tournament.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tournament8xResponse {
	private String msg;
	private int code;
	private Data data;

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Data {
		private List<Tournament> tournaments;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Tournament {
		private int sid;
		private int tid;
		private int cid;
		private String name;
		private boolean favorite;
		private int priority;
		private int count;
		private List<Match> matches;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Match {
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
		private Team home;
		private Team away;
		private Round round;
		private MarketInfo marketInfo;
		private Mids mids;
		private List<Object> videos;
		private List<Anchor> anchors;
		private String name;
		private long kickoffTime;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Team {
		private int id;
		private int cid;
		private String name;
		private Jersey jersey;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Jersey {
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
	public static class Round {
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
	public static class MarketInfo {
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
	public static class Mids {
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
	public static class Anchor {
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
