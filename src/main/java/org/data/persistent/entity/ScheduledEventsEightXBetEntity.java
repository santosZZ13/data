package org.data.persistent.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "scheduled_events_eight_x_bet")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledEventsEightXBetEntity {

	@Id
	private String id;

	private Integer sId;
	private Integer tId;
	private Integer cId;
	private String name;
	private Boolean favorite;
	private Integer priority;
	private Integer count;
	private List<MatchEntity> matchEntities;


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MatchEntity {
		private int sId;
		private int cId;
		private int tId;
		private int iId;
		private int countdown;
		private String state;
		private String series;
		private String vd;
		private int streaming;
		private int chatMid;
		private int gifMid;
		private int graphMid;
		private Boolean inplay;
		private Boolean video;
		private Boolean nv;
		private String scoreId;
		private String tnName;
		private int tnPriority;
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
		private int id;
		private int cid;
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
	public static class MidsEntity {
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
