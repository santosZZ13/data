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
	private String roundTime;
	private String closeTime;
	private int specialPrize;
	private int firstPrize;
	private List<Integer> secondPrize;
	private List<Integer> thirdPrize;
	private List<Integer> fourthPrize;
	private List<Integer> fifthPrize;
	private List<Integer> sixthPrize;
	private List<Integer> seventhPrize;
	private LocalDateTime created;
	private LocalDateTime updated;
}
