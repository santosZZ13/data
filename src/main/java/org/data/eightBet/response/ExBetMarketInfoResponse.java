package org.data.eightBet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class ExBetMarketInfoResponse {
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
