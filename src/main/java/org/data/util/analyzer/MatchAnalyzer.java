package org.data.util.analyzer;

import org.data.dto.ex.GetAnalystDto;
import org.data.response.sf.child.ScoreResponse;
import org.data.response.sf.parent.SofaMatchResponseDetailDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MatchAnalyzer {
	/**
	 * Phân tích lịch sử trận đấu của một đội.
	 */
	public static GetAnalystDto.TeamAnalysisDto analyzeTeam(Integer teamId, List<SofaMatchResponseDetailDto> history, boolean isHome) {
		int totalMatches = history.size();
		if (totalMatches == 0) {
			return GetAnalystDto.TeamAnalysisDto.builder()
					.teamId(teamId)
					.totalMatchesAnalyzed(0)
					.build();
		}

		// Lọc 10 trận gần nhất trên sân nhà hoặc sân khách
		List<SofaMatchResponseDetailDto> relevantMatches = history.stream()
				.filter(match -> isHome ? match.getHomeTeam().getId().equals(teamId) : match.getAwayTeam().getId().equals(teamId))
//				.limit(10)
				.collect(Collectors.toList());

		int relevantMatchesSize = relevantMatches.size();
		if (relevantMatchesSize == 0) {
			return GetAnalystDto.TeamAnalysisDto.builder()
					.teamId(teamId)
					.totalMatchesAnalyzed(0)
					.build();
		}

		// Tính các chỉ số
		double over15Count = relevantMatches.stream()
				.filter(match -> getTotalGoals(match) > 1.5)
				.count();

		double over25Count = relevantMatches.stream()
				.filter(match -> getTotalGoals(match) > 2.5)
				.count();

		double bttsCount = relevantMatches.stream()
				.filter(match -> getHomeScore(match).getCurrent() > 0 && getAwayScore(match).getCurrent() > 0)
				.count();

		double firstHalfOver05Count = relevantMatches.stream()
				.filter(match ->
						(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0) +
								(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0) > 0.5
				)
				.count();

		double firstHalfOver15Count = relevantMatches.stream()
				.filter(match ->
						(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0) +
								(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0) > 1.5
				)
				.count();

		double firstHalfBttsCount = relevantMatches.stream()
				.filter(match ->
						(getHomeScore(match).getPeriod1() != null && getHomeScore(match).getPeriod1() > 0) &&
								(getAwayScore(match).getPeriod1() != null && getAwayScore(match).getPeriod1() > 0)
				)
				.count();

		double avgGoalsScored = relevantMatches.stream()
				.mapToDouble(match -> isHome ?
						(getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0.0) :
						(getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0.0))
				.average()
				.orElse(0.0);

		double avgGoalsConceded = relevantMatches.stream()
				.mapToDouble(match -> isHome ?
						(getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0.0) :
						(getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0.0))
				.average()
				.orElse(0.0);

		double firstHalfAvgGoalsScored = relevantMatches.stream()
				.mapToDouble(match -> isHome ?
						(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0) :
						(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0))
				.average()
				.orElse(0.0);

		double firstHalfAvgGoalsConceded = relevantMatches.stream()
				.mapToDouble(match -> isHome ?
						(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0) :
						(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0))
				.average()
				.orElse(0.0);

		// Tính điểm phong độ (3 điểm thắng, 1 điểm hòa, 0 điểm thua)
		double recentFormScore = relevantMatches.stream()
				.mapToDouble(match -> {
					int homeScore = getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0;
					int awayScore = getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0;
					if (isHome) {
						return homeScore > awayScore ? 3 : homeScore == awayScore ? 1 : 0;
					} else {
						return awayScore > homeScore ? 3 : homeScore == awayScore ? 1 : 0;
					}
				})
				.average()
				.orElse(0.0);

		// Tính số trận thắng, hòa, thua
		int wins = (int) relevantMatches.stream()
				.filter(match -> {
					int homeScore = getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0;
					int awayScore = getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0;
					return isHome ? homeScore > awayScore : awayScore > homeScore;
				})
				.count();

		int draws = (int) relevantMatches.stream()
				.filter(match -> {
					int homeScore = getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0;
					int awayScore = getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0;
					return homeScore == awayScore;
				})
				.count();

		int losses = relevantMatchesSize - wins - draws;

		// Chuyển đổi recent matches
		List<GetAnalystDto.RecentMatchDto> recentMatches = convertRecentMatches(relevantMatches, teamId);

		return GetAnalystDto.TeamAnalysisDto.builder()
				.teamId(teamId)
				.teamName(relevantMatches.get(0).getHomeTeam().getId().equals(teamId) ?
						relevantMatches.get(0).getHomeTeam().getName() :
						relevantMatches.get(0).getAwayTeam().getName())
				.over15Rate(over15Count / relevantMatchesSize)
				.over25Rate(over25Count / relevantMatchesSize)
				.bttsRate(bttsCount / relevantMatchesSize)
				.averageGoalsScored(avgGoalsScored)
				.averageGoalsConceded(avgGoalsConceded)
				.firstHalfOver05Rate(firstHalfOver05Count / relevantMatchesSize)
				.firstHalfOver15Rate(firstHalfOver15Count / relevantMatchesSize)
				.firstHalfBttsRate(firstHalfBttsCount / relevantMatchesSize)
				.firstHalfAverageGoalsScored(firstHalfAvgGoalsScored)
				.firstHalfAverageGoalsConceded(firstHalfAvgGoalsConceded)
				.recentFormScore(recentFormScore)
				.totalMatchesAnalyzed(relevantMatchesSize)
				.recentMatches(recentMatches)
				.wins(wins)
				.draws(draws)
				.losses(losses)
				.build();
	}

	/**
	 * Tính chỉ số Over 1.5
	 */
	public static Double calculateOver15Index(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * (home.getOver15Rate() + away.getOver15Rate()) / 2 +
				0.4 * (home.getAverageGoalsScored() + home.getAverageGoalsConceded() +
						away.getAverageGoalsScored() + away.getAverageGoalsConceded());
	}

	/**
	 * Tính chỉ số Over 2.5
	 */
	public static Double calculateOver25Index(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * (home.getOver25Rate() + away.getOver25Rate()) / 2 +
				0.4 * (home.getAverageGoalsScored() + home.getAverageGoalsConceded() +
						away.getAverageGoalsScored() + away.getAverageGoalsConceded());
	}

	/**
	 * Tính chỉ số BTTS
	 */
	public static Double calculateBttsIndex(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * (home.getBttsRate() + away.getBttsRate()) / 2 +
				0.4 * (home.getAverageGoalsScored() + away.getAverageGoalsScored());
	}

	/**
	 * Tính chỉ số Over 0.5
	 */
	public static Double calculateOver05Index(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * ((home.getOver15Rate() + away.getOver15Rate()) / 2) +
				0.4 * (home.getAverageGoalsScored() + away.getAverageGoalsScored());
	}

	/**
	 * Tính chỉ số Over 0.5 hiệp 1
	 */
	public static Double calculateFirstHalfOver05Index(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * (home.getFirstHalfOver05Rate() + away.getFirstHalfOver05Rate()) / 2 +
				0.4 * (home.getFirstHalfAverageGoalsScored() + away.getFirstHalfAverageGoalsScored());
	}

	/**
	 * Tính chỉ số Over 1.5 hiệp 1
	 */
	public static Double calculateFirstHalfOver15Index(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * (home.getFirstHalfOver15Rate() + away.getFirstHalfOver15Rate()) / 2 +
				0.4 * (home.getFirstHalfAverageGoalsScored() + home.getFirstHalfAverageGoalsConceded() +
						away.getFirstHalfAverageGoalsScored() + away.getFirstHalfAverageGoalsConceded());
	}

	/**
	 * Tính chỉ số BTTS hiệp 1
	 */
	public static Double calculateFirstHalfBttsIndex(GetAnalystDto.TeamAnalysisDto home, GetAnalystDto.TeamAnalysisDto away) {
		return 0.6 * (home.getFirstHalfBttsRate() + away.getFirstHalfBttsRate()) / 2 +
				0.4 * (home.getFirstHalfAverageGoalsScored() + away.getFirstHalfAverageGoalsScored());
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

	public static List<GetAnalystDto.RecentMatchDto> convertRecentMatches(List<SofaMatchResponseDetailDto> matches, Integer teamId) {
		return matches.stream()
				.limit(5)
				.map(match -> {
					boolean isHome = match.getHomeTeam().getId().equals(teamId);
					return GetAnalystDto.RecentMatchDto.builder()
							.matchId(match.getMatchId())
							.opponent(isHome ? match.getAwayTeam().getName() : match.getHomeTeam().getName())
							.result((getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0) + "-" +
									(getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0))
							.isHome(isHome)
							.totalGoals(getTotalGoals(match))
							.over15(getTotalGoals(match) > 1.5)
							.over25(getTotalGoals(match) > 2.5)
							.btts(getHomeScore(match).getCurrent() != null && getHomeScore(match).getCurrent() > 0 &&
									getAwayScore(match).getCurrent() != null && getAwayScore(match).getCurrent() > 0)
							.build();
				})
				.collect(Collectors.toList());
	}

	/**
	 * Helper method để lấy tổng số bàn thắng
	 */
	public static int getTotalGoals(SofaMatchResponseDetailDto match) {
		int homeScore = getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0;
		int awayScore = getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0;
		return homeScore + awayScore;
	}

	/**
	 * Helper method để lấy score đội nhà
	 */
	public static ScoreResponse getHomeScore(SofaMatchResponseDetailDto match) {
		return match.getHomeScore() != null ? match.getHomeScore() : new ScoreResponse();
	}

	/**
	 * Helper method để lấy score đội khách
	 */
	public static ScoreResponse getAwayScore(SofaMatchResponseDetailDto match) {
		return match.getAwayScore() != null ? match.getAwayScore() : new ScoreResponse();
	}

	/**
	 * Giả lập lấy lịch sử trận đấu của đội (cần thay bằng API thực tế)
	 */
	public static List<SofaMatchResponseDetailDto> fetchTeamHistory(Integer teamId) {
		// TODO: Gọi API https://www.sofascore.com/api/v1/team/{teamId}/events/last/0
		return new ArrayList<>();
	}

	/**
	 * Giả lập lấy lịch sử đối đầu (cần thay bằng API thực tế hoặc lọc từ lịch sử đội)
	 */
	public static List<SofaMatchResponseDetailDto> fetchHeadToHead(Integer homeTeamId, Integer awayTeamId) {
		// TODO: Lọc các trận giữa hai đội từ lịch sử
		return new ArrayList<>();
	}
}
