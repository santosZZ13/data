package org.data.dto.ex;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.dto.common.ex.ExBetMatchDto;

import java.util.List;

public interface GetAnalystDto {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		@NotNull(message = "Matches cannot be null")
		@NotEmpty(message = "Matches cannot be empty")
		private List<ExBetMatchDto> matches;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private List<MatchAnalysisDto> analyzedMatches;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class MatchAnalysisDto {
		private ExBetMatchDto match; // Thông tin trận đấu
		private TeamAnalysisDto homeTeamAnalysis; // Phân tích đội nhà
		private TeamAnalysisDto awayTeamAnalysis; // Phân tích đội khách
		private Double over15Index; // Chỉ số Over 1.5
		private Double over05Index; // Chỉ số Over 0.5
		private Double over25Index; // Chỉ số Over 2.5 (mới)
		private Double bttsIndex; // Chỉ số BTTS (mới)
		private Double firstHalfOver05Index;
		private Double firstHalfOver15Index;
		private Double firstHalfBttsIndex;
		private Integer matchPriority; // Độ ưu tiên giải đấu (từ tournament.priority)
		private List<HeadToHeadDto> headToHead; // Lịch sử đối đầu (mới)
		private String recommendedBet; // Gợi ý cược (VD: "Over 1.5", "BTTS") (mới)
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class TeamAnalysisDto {
		private String teamName;
		private Integer teamId;
		private Integer totalMatchesAnalyzed;
		private List<RecentMatchDto> recentMatches;
		private TeamStats stats;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class TeamStats {
		private double over15Rate;
		private double over25Rate;
		private double scoringRate; // Tỷ lệ trận đội ghi bàn
		private double firstHalfOver05Rate;
		private double firstHalfOver15Rate;
		private double firstHalfScoringRate; // Tỷ lệ trận đội ghi bàn hiệp 1
		private double averageGoalsScored;
		private double averageGoalsConceded;
		private double firstHalfAverageGoalsScored;
		private double firstHalfAverageGoalsConceded;
		private int wins;
		private int draws;
		private int losses;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class RecentMatchDto {
		private Integer matchId; // ID trận đấu
		private String opponent; // Tên đối thủ
		private String result; // Kết quả (VD: "2-1")
		private Boolean isHome; // Đội này đá sân nhà hay sân khách
		private Integer totalGoals; // Tổng bàn thắng
		private Boolean over15; // Trận có tài 1.5?
		private Boolean over25; // Trận có tài 2.5?
		private Boolean scoring; // Đội có ghi bàn?
		private Boolean firstHalfOver05; // Có bàn thắng trong hiệp 1?
		private Boolean firstHalfOver15; // Có trên 1.5 bàn trong hiệp 1?
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class HeadToHeadDto {
		private Integer matchId; // ID trận đấu
		private String result; // Kết quả (VD: "3-2")
		private Integer totalGoals; // Tổng bàn thắng
		private Boolean over15; // Trận có tài 1.5?
		private Boolean over25; // Trận có tài 2.5?
		private Boolean btts; // Cả hai đội ghi bàn?
		private Long timestamp; // Thời gian trận đấu
		private Boolean firstHalfOver05; // Có bàn thắng trong hiệp 1?
		private Boolean firstHalfOver15; // Có trên 1.5 bàn trong hiệp 1?
	}
}