package org.data.util.analyzer;

import org.data.dto.ex.GetAnalystDto;

public class TeamAnalyzer {
	/**
	 * Tính chỉ số Over 0.5
	 */
	public static Double calculateOver05Index(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * (home.getStats().getScoringRate() + away.getStats().getScoringRate()) / 2 +
				0.4 * (home.getStats().getAverageGoalsScored() + away.getStats().getAverageGoalsScored());
	}

	/**
	 * Tính chỉ số Over 1.5
	 */
	public static Double calculateOver15Index(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * (home.getStats().getOver15Rate() + away.getStats().getOver15Rate()) / 2 +
				0.4 * (home.getStats().getAverageGoalsScored() + away.getStats().getAverageGoalsScored());
	}

	/**
	 * Tính chỉ số Over 2.5
	 */
	public static Double calculateOver25Index(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * (home.getStats().getOver25Rate() + away.getStats().getOver25Rate()) / 2 +
				0.4 * (home.getStats().getAverageGoalsScored() + away.getStats().getAverageGoalsScored());
	}

	/**
	 * Tính chỉ số BTTS
	 */
	public static Double calculateBttsIndex(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * (home.getStats().getScoringRate() * away.getStats().getScoringRate()) +
				0.4 * (home.getStats().getAverageGoalsScored() + away.getStats().getAverageGoalsScored());
	}

	/**
	 * Tính chỉ số Over 0.5 hiệp 1
	 */
	public static Double calculateFirstHalfOver05Index(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * (home.getStats().getFirstHalfScoringRate() + away.getStats().getFirstHalfScoringRate()) / 2 +
				0.4 * (home.getStats().getFirstHalfAverageGoalsScored() + away.getStats().getFirstHalfAverageGoalsScored());
	}

	/**
	 * Tính chỉ số Over 1.5 hiệp 1
	 */
	public static Double calculateFirstHalfOver15Index(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * (home.getStats().getFirstHalfOver15Rate() + away.getStats().getFirstHalfOver15Rate()) / 2 +
				0.4 * (home.getStats().getFirstHalfAverageGoalsScored() + away.getStats().getFirstHalfAverageGoalsScored());
	}

	/**
	 * Tính chỉ số BTTS hiệp 1
	 */
	public static Double calculateFirstHalfBttsIndex(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * (home.getStats().getFirstHalfScoringRate() * away.getStats().getFirstHalfScoringRate()) +
				0.4 * (home.getStats().getFirstHalfAverageGoalsScored() + away.getStats().getFirstHalfAverageGoalsScored());
	}

	/**
	 * Xác định gợi ý cược
	 */
	public static String determineRecommendedBet(Double over15Index, Double over25Index, Double bttsIndex, Double firstHalfOver05Index) {
		if (over15Index > 0.7) return "Over 1.5";
		if (over25Index > 0.65) return "Over 2.5";
		if (bttsIndex > 0.7) return "BTTS";
		if (firstHalfOver05Index > 0.8) return "First Half Over 0.5";
		return "No Bet";
	}
}