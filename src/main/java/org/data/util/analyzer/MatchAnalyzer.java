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

		// Lọc các trận sân nhà và sân khách
		List<SofaMatchResponseDetailDto> homeMatches = history.stream()
				.filter(match -> match.getHomeTeam().getId().equals(teamId))
				.toList();
		List<SofaMatchResponseDetailDto> awayMatches = history.stream()
				.filter(match -> match.getAwayTeam().getId().equals(teamId))
				.toList();

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

		// Tính các chỉ số mới cho sân nhà
		int homeMatchesSize = homeMatches.size();
		double homeOver15Rate = homeMatchesSize > 0 ? homeMatches.stream()
				.filter(match -> getTotalGoals(match) > 1.5)
				.count() / (double) homeMatchesSize : 0.0;
		double homeOver25Rate = homeMatchesSize > 0 ? homeMatches.stream()
				.filter(match -> getTotalGoals(match) > 2.5)
				.count() / (double) homeMatchesSize : 0.0;
		double homeBttsRate = homeMatchesSize > 0 ? homeMatches.stream()
				.filter(match -> getHomeScore(match).getCurrent() > 0 && getAwayScore(match).getCurrent() > 0)
				.count() / (double) homeMatchesSize : 0.0;
		double homeFirstHalfOver05Rate = homeMatchesSize > 0 ? homeMatches.stream()
				.filter(match ->
						(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0) +
								(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0) > 0.5
				)
				.count() / (double) homeMatchesSize : 0.0;
		double homeFirstHalfOver15Rate = homeMatchesSize > 0 ? homeMatches.stream()
				.filter(match ->
						(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0) +
								(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0) > 1.5
				)
				.count() / (double) homeMatchesSize : 0.0;
		double homeFirstHalfBttsRate = homeMatchesSize > 0 ? homeMatches.stream()
				.filter(match ->
						(getHomeScore(match).getPeriod1() != null && getHomeScore(match).getPeriod1() > 0) &&
								(getAwayScore(match).getPeriod1() != null && getAwayScore(match).getPeriod1() > 0)
				)
				.count() / (double) homeMatchesSize : 0.0;
		double homeAverageGoalsScored = homeMatchesSize > 0 ? homeMatches.stream()
				.mapToDouble(match -> getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0.0)
				.average()
				.orElse(0.0) : 0.0;
		double homeAverageGoalsConceded = homeMatchesSize > 0 ? homeMatches.stream()
				.mapToDouble(match -> getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0.0)
				.average()
				.orElse(0.0) : 0.0;
		double homeFirstHalfAverageGoalsScored = homeMatchesSize > 0 ? homeMatches.stream()
				.mapToDouble(match -> getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0)
				.average()
				.orElse(0.0) : 0.0;
		double homeFirstHalfAverageGoalsConceded = homeMatchesSize > 0 ? homeMatches.stream()
				.mapToDouble(match -> getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0)
				.average()
				.orElse(0.0) : 0.0;
		int homeWins = homeMatchesSize > 0 ? (int) homeMatches.stream()
				.filter(match -> {
					int homeScore = getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0;
					int awayScore = getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0;
					return homeScore > awayScore;
				})
				.count() : 0;
		int homeDraws = homeMatchesSize > 0 ? (int) homeMatches.stream()
				.filter(match -> {
					int homeScore = getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0;
					int awayScore = getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0;
					return homeScore == awayScore;
				})
				.count() : 0;
		int homeLosses = homeMatchesSize > 0 ? homeMatchesSize - homeWins - homeDraws : 0;

		// Tính các chỉ số mới cho sân khách
		int awayMatchesSize = awayMatches.size();
		double awayOver15Rate = awayMatchesSize > 0 ? awayMatches.stream()
				.filter(match -> getTotalGoals(match) > 1.5)
				.count() / (double) awayMatchesSize : 0.0;
		double awayOver25Rate = awayMatchesSize > 0 ? awayMatches.stream()
				.filter(match -> getTotalGoals(match) > 2.5)
				.count() / (double) awayMatchesSize : 0.0;
		double awayBttsRate = awayMatchesSize > 0 ? awayMatches.stream()
				.filter(match -> getHomeScore(match).getCurrent() > 0 && getAwayScore(match).getCurrent() > 0)
				.count() / (double) awayMatchesSize : 0.0;
		double awayFirstHalfOver05Rate = awayMatchesSize > 0 ? awayMatches.stream()
				.filter(match ->
						(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0) +
								(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0) > 0.5
				)
				.count() / (double) awayMatchesSize : 0.0;
		double awayFirstHalfOver15Rate = awayMatchesSize > 0 ? awayMatches.stream()
				.filter(match ->
						(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0) +
								(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0) > 1.5
				)
				.count() / (double) awayMatchesSize : 0.0;
		double awayFirstHalfBttsRate = awayMatchesSize > 0 ? awayMatches.stream()
				.filter(match ->
						(getHomeScore(match).getPeriod1() != null && getHomeScore(match).getPeriod1() > 0) &&
								(getAwayScore(match).getPeriod1() != null && getAwayScore(match).getPeriod1() > 0)
				)
				.count() / (double) awayMatchesSize : 0.0;
		double awayAverageGoalsScored = awayMatchesSize > 0 ? awayMatches.stream()
				.mapToDouble(match -> getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0.0)
				.average()
				.orElse(0.0) : 0.0;
		double awayAverageGoalsConceded = awayMatchesSize > 0 ? awayMatches.stream()
				.mapToDouble(match -> getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0.0)
				.average()
				.orElse(0.0) : 0.0;
		double awayFirstHalfAverageGoalsScored = awayMatchesSize > 0 ? awayMatches.stream()
				.mapToDouble(match -> getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0)
				.average()
				.orElse(0.0) : 0.0;
		double awayFirstHalfAverageGoalsConceded = awayMatchesSize > 0 ? awayMatches.stream()
				.mapToDouble(match -> getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0)
				.average()
				.orElse(0.0) : 0.0;
		int awayWins = awayMatchesSize > 0 ? (int) awayMatches.stream()
				.filter(match -> {
					int homeScore = getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0;
					int awayScore = getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0;
					return awayScore > homeScore;
				})
				.count() : 0;
		int awayDraws = awayMatchesSize > 0 ? (int) awayMatches.stream()
				.filter(match -> {
					int homeScore = getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0;
					int awayScore = getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0;
					return homeScore == awayScore;
				})
				.count() : 0;
		int awayLosses = awayMatchesSize > 0 ? awayMatchesSize - awayWins - awayDraws : 0;

		// Chuyển đổi recent matches
		List<GetAnalystDto.RecentMatchDto> recentMatches = convertRecentMatches(history, teamId);


		return GetAnalystDto.TeamAnalysisDto.builder()
				.teamId(teamId)
				.teamName(relevantMatches.get(0).getHomeTeam().getId().equals(teamId) ?
						relevantMatches.get(0).getHomeTeam().getName() :
						relevantMatches.get(0).getAwayTeam().getName())
				.over15Rate(over15Count / relevantMatchesSize)
				.over25Rate(over25Count / relevantMatchesSize)
				.bttsRate(bttsCount / relevantMatchesSize)
				.firstHalfOver05Rate(firstHalfOver05Count / relevantMatchesSize)
				.firstHalfOver15Rate(firstHalfOver15Count / relevantMatchesSize)
				.firstHalfBttsRate(firstHalfBttsCount / relevantMatchesSize)
				.averageGoalsScored(avgGoalsScored)
				.averageGoalsConceded(avgGoalsConceded)
				.firstHalfAverageGoalsScored(firstHalfAvgGoalsScored)
				.firstHalfAverageGoalsConceded(firstHalfAvgGoalsConceded)
				.recentFormScore(recentFormScore)
				.wins(wins)
				.draws(draws)
				.losses(losses)
				// Chỉ số mới
				.homeOver15Rate(homeOver15Rate)
				.awayOver15Rate(awayOver15Rate)
				.homeOver25Rate(homeOver25Rate)
				.awayOver25Rate(awayOver25Rate)
				.homeBttsRate(homeBttsRate)
				.awayBttsRate(awayBttsRate)
				.homeFirstHalfOver05Rate(homeFirstHalfOver05Rate)
				.awayFirstHalfOver05Rate(awayFirstHalfOver05Rate)
				.homeFirstHalfOver15Rate(homeFirstHalfOver15Rate)
				.awayFirstHalfOver15Rate(awayFirstHalfOver15Rate)
				.homeFirstHalfBttsRate(homeFirstHalfBttsRate)
				.awayFirstHalfBttsRate(awayFirstHalfBttsRate)
				.homeAverageGoalsScored(homeAverageGoalsScored)
				.awayAverageGoalsScored(awayAverageGoalsScored)
				.homeAverageGoalsConceded(homeAverageGoalsConceded)
				.awayAverageGoalsConceded(awayAverageGoalsConceded)
				.homeFirstHalfAverageGoalsScored(homeFirstHalfAverageGoalsScored)
				.awayFirstHalfAverageGoalsScored(awayFirstHalfAverageGoalsScored)
				.homeFirstHalfAverageGoalsConceded(homeFirstHalfAverageGoalsConceded)
				.awayFirstHalfAverageGoalsConceded(awayFirstHalfAverageGoalsConceded)
				.homeWins(homeWins)
				.awayWins(awayWins)
				.homeDraws(homeDraws)
				.awayDraws(awayDraws)
				.homeLosses(homeLosses)
				.awayLosses(awayLosses)
				.totalMatchesAnalyzed(relevantMatchesSize)
				.recentMatches(recentMatches)
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
