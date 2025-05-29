package org.data.util;

import java.util.ArrayList;
import java.util.List;

public class LevenshteinMatcher {
	public static int calculateLevenshteinDistance(String s1, String s2) {
		if (s1 == null || s2 == null) {
			return -1;
		}
		int[][] dp = new int[s1.length() + 1][s2.length() + 1];

		for (int i = 0; i <= s1.length(); i++) {
			dp[i][0] = i;
		}
		for (int j = 0; j <= s2.length(); j++) {
			dp[0][j] = j;
		}

		for (int i = 1; i <= s1.length(); i++) {
			for (int j = 1; j <= s2.length(); j++) {
				if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
					dp[i][j] = dp[i - 1][j - 1];
				} else {
					dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
				}
			}
		}
		return dp[s1.length()][s2.length()];
	}

	public static List<String> matchTeams(List<String> sourceTeams, List<String> targetTeams, int threshold) {
		List<String> matches = new ArrayList<>();
		for (String sourceTeam : sourceTeams) {
			String normalizedSource = NormalizeTeamName.normalize(sourceTeam);
			String bestMatch = null;
			int minDistance = Integer.MAX_VALUE;

			for (String targetTeam : targetTeams) {
				String normalizedTarget = NormalizeTeamName.normalize(targetTeam);
				int distance = calculateLevenshteinDistance(normalizedSource, normalizedTarget);
				if (distance >= 0 && distance < minDistance) {
					minDistance = distance;
					bestMatch = targetTeam;
				}
			}

			if (minDistance <= threshold) {
				matches.add(sourceTeam + " → " + bestMatch + " (Distance: " + minDistance + ")");
			} else {
				matches.add(sourceTeam + " → No match (Distance: " + minDistance + ")");
			}
		}
		return matches;
	}

	public static void main(String[] args) {
		// Example: Load teams from real data (you'd need to parse JSON here)
		List<String> eightXbetTeams = new ArrayList<>();
		eightXbetTeams.add("AA");
		eightXbetTeams.add("Atlético Colegiales");
		eightXbetTeams.add("Manchester United U21");

		List<String> sofaScoreTeams = new ArrayList<>();
		sofaScoreTeams.add("CCCCC"); // From sofa_scheduled_matches.json
		sofaScoreTeams.add("Atletico Colegiales"); // From sofa_scheduled_matches.json
		sofaScoreTeams.add("Manchester United"); // From sofa_scheduled_matches.json

		// Test with a different threshold
		int threshold = 3;
		List<String> results = matchTeams(eightXbetTeams, sofaScoreTeams, threshold);

		// Print results
		for (String result : results) {
			System.out.println(result);
		}
	}
}
