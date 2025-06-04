package org.data.response.sf.child;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoundInfoResponse {
	private Integer round;
	private String name;
	private Integer cupRoundType; // Thêm trường mới
}
