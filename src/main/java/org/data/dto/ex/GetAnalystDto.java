package org.data.dto.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.dto.common.ExBetMatchResponseDto;

import java.util.List;

public interface GetAnalystDto {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		private List<ExBetMatchResponseDto> matches;
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
		private ExBetMatchResponseDto match; // Thông tin trận đấu
		private TeamAnalysisDto homeTeamAnalysis; // Phân tích đội nhà
		private TeamAnalysisDto awayTeamAnalysis; // Phân tích đội khách
		/**
		 * Index for Over 1.5 goals in the full match, calculated as:
		 * 0.6 * (homeOver15Rate + awayOver15Rate) / 2 +
		 * 0.4 * (homeAvgGoalsScored + homeAvgGoalsConceded + awayAvgGoalsScored + awayAvgGoalsConceded).
		 * Normalized to [0, 1].
		 * Example: 0.75 indicates a high likelihood of over 1.5 goals.
		 */
		private Double over15Index; // Chỉ số Over 1.5
		/**
		 * Index for Over 0.5 goals in the full match, calculated similarly to over15Index.
		 * Example: 0.90 indicates a very high chance of at least one goal.
		 */
		private Double over05Index; // Chỉ số Over 0.5

		/**
		 * Index for Over 2.5 goals in the full match, calculated as:
		 * 0.6 * (homeOver25Rate + awayOver25Rate) / 2 +
		 * 0.4 * (homeAvgGoalsScored + homeAvgGoalsConceded + awayAvgGoalsScored + awayAvgGoalsConceded).
		 * Example: 0.65 suggests a moderate chance of over 2.5 goals.
		 */
		private Double over25Index; // Chỉ số Over 2.5 (mới)
		/**
		 * Index for Both Teams To Score (BTTS) in the full match, calculated as:
		 * 0.6 * (homeBttsRate + awayBttsRate) / 2 +
		 * 0.4 * (homeAvgGoalsScored + awayAvgGoalsScored).
		 * Example: 0.70 indicates a high chance of both teams scoring.
		 */
		private Double bttsIndex; // Chỉ số BTTS (mới)
		/**
		 * Index for Over 0.5 goals in the first half, calculated as:
		 * 0.6 * (homeFirstHalfOver05Rate + awayFirstHalfOver05Rate) / 2 +
		 * 0.4 * (homeFirstHalfAvgGoalsScored + awayFirstHalfAvgGoalsScored).
		 * Example: 0.80 suggests a high likelihood of at least one goal in the first half.
		 */
		private Double firstHalfOver05Index;
		/**
		 * Index for Over 1.5 goals in the first half, calculated as:
		 * 0.6 * (homeFirstHalfOver15Rate + awayFirstHalfOver15Rate) / 2 +
		 * 0.4 * (homeFirstHalfAvgGoalsScored + homeFirstHalfAvgGoalsConceded +
		 * awayFirstHalfAvgGoalsScored + awayFirstHalfAvgGoalsConceded).
		 * Example: 0.50 indicates a moderate chance of over 1.5 goals in the first half.
		 */
		private Double firstHalfOver15Index;

		/**
		 * Index for Both Teams To Score (BTTS) in the first half, calculated as:
		 * 0.6 * (homeFirstHalfBttsRate + awayFirstHalfBttsRate) / 2 +
		 * 0.4 * (homeFirstHalfAvgGoalsScored + awayFirstHalfAvgGoalsScored).
		 * Example: 0.40 suggests a low chance of both teams scoring in the first half.
		 */
		private Double firstHalfBttsIndex;

		/**
		 * Tournament priority from SofaScore, indicating the importance of the match.
		 * Example: 503 for Brasileirão Betano, 744 for UEFA Nations League.
		 */
		private Integer matchPriority; // Độ ưu tiên giải đấu (từ tournament.priority)
		/**
		 * List of head-to-head matches between the two teams (up to 5 recent matches).
		 * Example: Historical matches with results, total goals, and betting outcomes.
		 */
		private List<HeadToHeadDto> headToHead; // Lịch sử đối đầu (mới)
		/**
		 * Recommended bet based on indices (e.g., "Over 1.5", "BTTS", "No Bet").
		 * Determined by thresholds, e.g., over15Index > 0.7 → "Over 1.5".
		 * Example: "Over 1.5" if over15Index is 0.75.
		 */
		private String recommendedBet; // Gợi ý cược (VD: "Over 1.5", "BTTS") (mới)
		private Integer homeRedCards; // Số thẻ đỏ đội nhà
		private Integer awayRedCards; // Số thẻ đỏ đội khách
		private Double homeXg; // Expected Goals đội nhà
		private Double awayXg; // Expected Goals đội khách
	}

	/**
	 * DTO representing the analysis of a team's historical performance.
	 */
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class TeamAnalysisDto {
		private String teamName;
		private Integer teamId;
		private Double over15Rate;
		private Double over25Rate;
		private Double bttsRate;
		private Double averageGoalsScored;
		private Double averageGoalsConceded;
		private Double firstHalfOver05Rate;
		private Double firstHalfOver15Rate;
		private Double firstHalfBttsRate;
		private Double firstHalfAverageGoalsScored;
		private Double firstHalfAverageGoalsConceded;
		private Double recentFormScore;
		private Integer totalMatchesAnalyzed;
		private Integer wins; // Số trận thắng
		private Integer draws; // Số trận hòa
		private Integer losses; // Số trận thua
		private List<RecentMatchDto> recentMatches;

		// Các chỉ số mới
		private double homeOver15Rate;
		private double awayOver15Rate;
		private double homeOver25Rate;
		private double awayOver25Rate;
		private double homeBttsRate;
		private double awayBttsRate;
		private double homeFirstHalfOver05Rate;
		private double awayFirstHalfOver05Rate;
		private double homeFirstHalfOver15Rate;
		private double awayFirstHalfOver15Rate;
		private double homeFirstHalfBttsRate;
		private double awayFirstHalfBttsRate;
		private double homeAverageGoalsScored;
		private double awayAverageGoalsScored;
		private double homeAverageGoalsConceded;
		private double awayAverageGoalsConceded;
		private double homeFirstHalfAverageGoalsScored;
		private double awayFirstHalfAverageGoalsScored;
		private double homeFirstHalfAverageGoalsConceded;
		private double awayFirstHalfAverageGoalsConceded;
		private int homeWins;
		private int awayWins;
		private int homeDraws;
		private int awayDraws;
		private int homeLosses;
		private int awayLosses;
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
		private Boolean btts; // Cả hai đội ghi bàn?
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