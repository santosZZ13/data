package org.data.response.sf;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TimeResponse {
	private String injuryTime1;
	private String injuryTime2;
	private Long currentPeriodStartTimestamp;
}
