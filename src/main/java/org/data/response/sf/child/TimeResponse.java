package org.data.response.sf.child;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeResponse {
	private Integer injuryTime1; // Thêm trường mới
	private Integer injuryTime2; // Thêm trường mới
	private Integer initial; // Thêm trường mới
	private Integer max; // Thêm trường mới
	private Integer extra; // Thêm trường mới
	private Long currentPeriodStartTimestamp; // Thêm trường mới
	private Integer periodLength; // Thêm trường mới
	private Integer overtimeLength; // Thêm trường mới
	private Integer totalPeriodCount; // Thêm trường mới
}
