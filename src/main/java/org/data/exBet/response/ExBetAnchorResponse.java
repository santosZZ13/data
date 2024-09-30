package org.data.exBet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@Nullable
public class ExBetAnchorResponse {
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
	private String name;
	private long kickoffTime;
}
