package org.data.response.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class ExBetGiftResponse {
	private String source;
	private String type;
	private String info;
}
