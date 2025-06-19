package org.data.util.analyzer;

import org.data.dto.ex.GetAnalystDto;
import org.data.external.sofa.model.ScoreResponse;
import org.data.external.sofa.model.SofaMatchResponseDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MatchAnalyzer {
	/**
	 * Tính các chỉ số cho danh sách trận đấu.
	 */
	public static GetAnalystDto.TeamStats calculateStats(List<SofaMatchResponseDetail> history, Integer teamId) {
		int size = history.size();

		// Tính tỷ lệ trận đội ghi > 1.5 bàn
		double over15Rate = history.stream()
				.filter(match -> {
					boolean isHomeTeam = match.getHomeTeam().getId().equals(teamId);
					int teamGoals = isHomeTeam ?
							(getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0) :
							(getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0);
					return teamGoals > 1.5;
				})
				.count() / (double) size;

		// Tính tỷ lệ trận đội ghi > 2.5 bàn
		double over25Rate = history.stream()
				.filter(match -> {
					boolean isHomeTeam = match.getHomeTeam().getId().equals(teamId);
					int teamGoals = isHomeTeam ?
							(getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0) :
							(getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0);
					return teamGoals > 2.5;
				})
				.count() / (double) size;

		// Tính tỷ lệ trận đội ghi ít nhất 1 bàn
		double scoringRate = history.stream()
				.filter(match -> {
					boolean isHomeTeam = match.getHomeTeam().getId().equals(teamId);
					int teamGoals = isHomeTeam ?
							(getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0) :
							(getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0);
					return teamGoals > 0;
				})
				.count() / (double) size;

		// Tính tỷ lệ trận đội ghi ít nhất 1 bàn hiệp 1
		double firstHalfOver05Rate = history.stream()
				.filter(match -> {
					boolean isHomeTeam = match.getHomeTeam().getId().equals(teamId);
					int teamGoals = isHomeTeam ?
							(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0) :
							(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0);
					return teamGoals > 0.5;
				})
				.count() / (double) size;

		// Tính tỷ lệ trận đội ghi > 1.5 bàn hiệp 1
		double firstHalfOver15Rate = history.stream()
				.filter(match -> {
					boolean isHomeTeam = match.getHomeTeam().getId().equals(teamId);
					int teamGoals = isHomeTeam ?
							(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0) :
							(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0);
					return teamGoals > 1.5;
				})
				.count() / (double) size;

		// Tính tỷ lệ trận đội ghi ít nhất 1 bàn hiệp 1
		double firstHalfScoringRate = history.stream()
				.filter(match -> {
					boolean isHomeTeam = match.getHomeTeam().getId().equals(teamId);
					int teamGoals = isHomeTeam ?
							(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0) :
							(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0);
					return teamGoals > 0;
				})
				.count() / (double) size;

		// Trung bình bàn ghi
		double averageGoalsScored = history.stream()
				.mapToDouble(match -> {
					boolean isHomeTeam = match.getHomeTeam().getId().equals(teamId);
					return isHomeTeam ?
							(getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0) :
							(getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0);
				})
				.average()
				.orElse(0.0);

		// Trung bình bàn thủng
		double averageGoalsConceded = history.stream()
				.mapToDouble(match -> {
					boolean isHomeTeam = match.getHomeTeam().getId().equals(teamId);
					return isHomeTeam ?
							(getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0) :
							(getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0);
				})
				.average()
				.orElse(0.0);

		// Trung bình bàn ghi hiệp 1
		double firstHalfAverageGoalsScored = history.stream()
				.mapToDouble(match -> {
					boolean isHomeTeam = match.getHomeTeam().getId().equals(teamId);
					return isHomeTeam ?
							(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0) :
							(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0);
				})
				.average()
				.orElse(0.0);

		// Trung bình bàn thủng hiệp 1
		double firstHalfAverageGoalsConceded = history.stream()
				.mapToDouble(match -> {
					boolean isHomeTeam = match.getHomeTeam().getId().equals(teamId);
					return isHomeTeam ?
							(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0) :
							(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0);
				})
				.average()
				.orElse(0.0);

		// Tính thắng, hòa, thua
		int wins = (int) history.stream()
				.filter(match -> {
					boolean isHomeTeam = match.getHomeTeam().getId().equals(teamId);
					int homeScore = getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0;
					int awayScore = getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0;
					return isHomeTeam ? homeScore > awayScore : awayScore > homeScore;
				})
				.count();
		int draws = (int) history.stream()
				.filter(match -> {
					int homeScore = getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0;
					int awayScore = getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0;
					return homeScore == awayScore;
				})
				.count();
		int losses = size - wins - draws;

		return GetAnalystDto.TeamStats.builder()
				.over15Rate(over15Rate)
				.over25Rate(over25Rate)
				.scoringRate(scoringRate)
				.firstHalfOver05Rate(firstHalfOver05Rate)
				.firstHalfOver15Rate(firstHalfOver15Rate)
				.firstHalfScoringRate(firstHalfScoringRate)
				.averageGoalsScored(averageGoalsScored)
				.averageGoalsConceded(averageGoalsConceded)
				.firstHalfAverageGoalsScored(firstHalfAverageGoalsScored)
				.firstHalfAverageGoalsConceded(firstHalfAverageGoalsConceded)
				.wins(wins)
				.draws(draws)
				.losses(losses)
				.build();
	}

	/**
	 * Chuyển đổi danh sách trận gần đây
	 */
	public static List<GetAnalystDto.RecentMatchDto> convertRecentMatches(List<SofaMatchResponseDetail> matches, Integer teamId) {
		return matches.stream()
				.limit(5)
				.map(match -> {
					boolean isHome = match.getHomeTeam().getId().equals(teamId);
					int teamGoals = isHome ?
							(getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0) :
							(getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0);
					return GetAnalystDto.RecentMatchDto.builder()
							.matchId(match.getMatchId())
							.opponent(isHome ? match.getAwayTeam().getName() : match.getHomeTeam().getName())
							.result((getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0) + "-" +
									(getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0))
							.isHome(isHome)
							.totalGoals(getTotalGoals(match))
							.over15(teamGoals > 1.5)
							.over25(teamGoals > 2.5)
							.scoring(teamGoals > 0)
							.firstHalfOver05((isHome ?
									(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0) :
									(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0)) > 0.5)
							.firstHalfOver15((isHome ?
									(getHomeScore(match).getPeriod1() != null ? getHomeScore(match).getPeriod1() : 0) :
									(getAwayScore(match).getPeriod1() != null ? getAwayScore(match).getPeriod1() : 0)) > 1.5)
							.build();
				})
				.collect(Collectors.toList());
	}

	/**
	 * Helper method để lấy tổng số bàn thắng
	 */
	public static int getTotalGoals(SofaMatchResponseDetail match) {
		if (match == null) {
			return 0;
		}
		int homeScore = getHomeScore(match).getCurrent() != null ? getHomeScore(match).getCurrent() : 0;
		int awayScore = getAwayScore(match).getCurrent() != null ? getAwayScore(match).getCurrent() : 0;
		return homeScore + awayScore;
	}

	/**
	 * Helper method để lấy score đội nhà
	 */
	public static ScoreResponse getHomeScore(SofaMatchResponseDetail match) {
		return match.getHomeScore() != null ? match.getHomeScore() : new ScoreResponse();
	}

	/**
	 * Helper method để lấy score đội khách
	 */
	public static ScoreResponse getAwayScore(SofaMatchResponseDetail match) {
		return match.getAwayScore() != null ? match.getAwayScore() : new ScoreResponse();
	}

	/**
	 * Giả lập lấy lịch sử trận đấu của đội
	 */
	public static List<SofaMatchResponseDetail> fetchTeamHistory(Integer teamId) {
		return new ArrayList<>();
	}

	/**
	 * Giả lập lấy lịch sử đối đầu
	 */
	public static List<SofaMatchResponseDetail> fetchHeadToHead(Integer homeTeamId, Integer awayTeamId) {
		return new ArrayList<>();
	}
}

