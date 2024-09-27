package org.data.eightBet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class ExBetMidsResponse {
	private Integer fmid;
	private Integer bmid;
	private Integer amid;
	private Integer cmid;
	private Integer dmid;
	private Integer jmid;
}
