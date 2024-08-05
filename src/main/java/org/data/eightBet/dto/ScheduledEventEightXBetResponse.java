package org.data.eightBet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduledEventEightXBetResponse {
	private String msg;
	private int code;
	private Data data;

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Data {
		private List<TournamentResponse> tournaments;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TournamentResponse {
		private Integer sid;
		private Integer tid;
		private Integer cid;
		private String name;
		private Boolean favorite;
		private Integer priority;
		private Integer count;
		private List<MatchResponse> matches;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MatchResponse {
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
		private TeamResponse home;
		private TeamResponse away;
		private RoundResponse round;
		private MarketInfoResponse marketInfo;
		private MidsResponse mids;
		private List<GiftResponse> gifs;
		private List<VideoResponse> videos;
		private List<AnchorResponse> anchors;
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
	public static class VideoResponse {
		private String source;
		private String type;
		private String info;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class GiftResponse {
		private String source;
		private String type;
		private String info;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TeamResponse {
		private Integer id;
		private Integer cid;
		private String name;
		@Nullable
		private JerseyResponse jersey;
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
	@Nullable
	public static class RoundResponse {
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
	public static class MarketInfoResponse {
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
	public static class MidsResponse {
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
	public static class AnchorResponse {
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
