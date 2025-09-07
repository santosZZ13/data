package org.data.persistent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "lottery")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotteryEntity {

	private long roundId; // Sử dụng roundId làm _id
	private int lotteryID;
	private long roundTime;
	private long closeTime;

	private String specialPrize;
	private String firstPrize;
	private List<String> secondPrize;
	private List<String> thirdPrize;
	private List<String> fourthPrize;
	private List<String> fifthPrize;
	private List<String> sixthPrize;
	private List<String> seventhPrize;
	private LocalDateTime created;
	private LocalDateTime updated;
}
