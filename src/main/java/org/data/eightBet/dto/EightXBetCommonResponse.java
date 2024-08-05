package org.data.eightBet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface EightXBetCommonResponse {
	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	class EightXBetMatchResponse {
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
		private EightXBetTeamResponse home;
		private EightXBetTeamResponse away;
		private EightXBetRoundResponse round;
		private EightXBetMarketInfoResponse marketInfo;
		private EightXBetMidsResponse mids;
		private List<EightXBetGiftResponse> gifs;
		private List<EightXBetVideoResponse> videos;
		private List<EightXBetAnchorResponse> anchors;
		private String name;
		private long kickoffTime;

		@Override
		public String toString() {
			return "MatchResponse{" +
					"iid=" + iid +
					", home=" + home.getName() +
					", away=" + away.getName() +
					", kickoffTime=" + kickoffTime +
					", tntName='" + tnName + '\'' +
					'}';
		}
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	class EightXBetVideoResponse {
		private String source;
		private String type;
		private String info;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	class EightXBetGiftResponse {
		private String source;
		private String type;
		private String info;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	class EightXBetTeamResponse {
		private Integer id;
		private Integer cid;
		private String name;
		@Nullable
		private EightXBetCommonResponse.EightXBetJerseyResponse jersey;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	class EightXBetJerseyResponse {
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
	@Nullable
	class EightXBetRoundResponse {
		@Nullable
		private String roundType;
		@Nullable
		private String roundName;
		@Nullable
		private String roundGroup;
		@Nullable
		private String roundGroupName;
		@Nullable
		private String roundNumber;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Nullable
	class EightXBetMarketInfoResponse {
		@Nullable
		private Boolean cr;
		@Nullable
		private Boolean ot;
		@Nullable
		private Boolean pk;
		@Nullable
		private Boolean otcr;
		@Nullable
		private Boolean ad;
		@Nullable
		private Boolean redCard;
		@Nullable
		private Boolean otRedCard;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	class EightXBetMidsResponse {
		@Nullable
		private Integer fmid;
		@Nullable
		private Integer bmid;
		@Nullable
		private Integer amid;
		@Nullable
		private Integer cmid;
		@Nullable
		private Integer dmid;
		@Nullable
		private Integer jmid;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Nullable
	class EightXBetAnchorResponse {
		private String houseId;
		private Integer liveStatus;
		private Integer visitHistory;
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
