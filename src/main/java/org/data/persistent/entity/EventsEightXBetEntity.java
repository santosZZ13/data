package org.data.persistent.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "scheduled_events_eight_x_bet")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventsEightXBetEntity {
	@Id
	private String id;
	private Integer sId;
	private Integer cId;
	private Integer tId;
	private Integer iId;
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
	private TeamEntity homeTeam;
	private TeamEntity awayTeam;
	private TournamentEntity tournament;
	private RoundEntity round;
	private MarketInfoEntity marketInfo;
	private MidsEntity mids;
	private List<GiftEntity> gifts;
	private List<VideoEntity> videos;
	private List<AnchorEntity> anchors;
	private String name;
//	@DateTimeFormat()
	private LocalDateTime kickoffTime;

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TournamentEntity {
		private Integer sId;
		private Integer cId;
		private Integer tId;
		private String name;
		private Boolean favorite;
		private Integer priority;
		private Integer count;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class VideoEntity {
		private String source;
		private String type;
		private String info;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MatchEntity {

		private TeamEntity home;
		private TeamEntity away;
		private RoundEntity round;
		private MarketInfoEntity marketInfo;
		private MidsEntity mids;
		private List<GiftEntity> gifts;
		private List<Object> videos;
		private List<AnchorEntity> anchors;
		private String name;
		private long kickoffTime;

	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class GiftEntity {
		private String source;
		private String type;
		private String info;
	}


	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TeamEntity {
		private Integer id;
		private Integer cid;
		private String name;
		private JerseyEntity jerseyEntity;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class JerseyEntity {
		private String base;
		private String sleeve;
		private String style;
		private String styleColor;
		private String shirtType;
		private String sleeveDetails;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoundEntity {
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
	public static class MarketInfoEntity {
		private Boolean cr;
		private Boolean ot;
		private Boolean pk;
		private Boolean otcr;
		private Boolean ad;
		private Boolean redCard;
		private Boolean otRedCard;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MidsEntity {
		private Integer fmid;
		private Integer bmid;
		private Integer amid;
		private Integer cmid;
		private Integer dmid;
		private Integer jmid;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class AnchorEntity {
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
