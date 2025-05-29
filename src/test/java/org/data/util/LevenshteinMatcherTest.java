package org.data.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LevenshteinMatcherTest {
	@Test
	void calculateLevenshteinDistance_IdenticalStrings_ReturnsZero() {
		String team1 = NormalizeTeamName.normalize("FC Trollhattan"); // 8xbet
		String team2 = NormalizeTeamName.normalize("FC Trollhättan");
		assertEquals(0, LevenshteinMatcher.calculateLevenshteinDistance( team1, team2));
	}

	@Test
	void calculateLevenshteinDistance_DifferentStrings_ReturnsCorrectDistance() {
		assertEquals(1, LevenshteinMatcher.calculateLevenshteinDistance("urartu", "uratu")); // Xóa 1 ký tự 'r'
		assertEquals(1, LevenshteinMatcher.calculateLevenshteinDistance("cat", "bat")); // Thay 'c' bằng 'b'
		assertEquals(1, LevenshteinMatcher.calculateLevenshteinDistance("dog", "dig")); // Thay 'o' bằng 'i'
	}

	@Test
	void calculateLevenshteinDistance_NullInput_ReturnsNegativeOne() {
		assertEquals(-1, LevenshteinMatcher.calculateLevenshteinDistance(null, "urartu"));
		assertEquals(-1, LevenshteinMatcher.calculateLevenshteinDistance("urartu", null));
		assertEquals(-1, LevenshteinMatcher.calculateLevenshteinDistance(null, null));
	}

	@Test
	void calculateLevenshteinDistance_EmptyStrings_ReturnsCorrectDistance() {
		assertEquals(0, LevenshteinMatcher.calculateLevenshteinDistance("", ""));
		assertEquals(3, LevenshteinMatcher.calculateLevenshteinDistance("", "abc"));
		assertEquals(3, LevenshteinMatcher.calculateLevenshteinDistance("abc", ""));
	}


	@Test
	void matchTeams_PerfectMatchWithinThreshold_ReturnsMatch() {
		List<String> sourceTeams = new ArrayList<>();
		sourceTeams.add("FC Urartu");
		List<String> targetTeams = new ArrayList<>();
		targetTeams.add("Urartu");

		List<String> results = LevenshteinMatcher.matchTeams(sourceTeams, targetTeams, 3);
		assertEquals(1, results.size());
		assertTrue(results.contains("FC Urartu → Urartu (Distance: 0)"));
	}

	@Test
	void matchTeams_NoMatchExceedsThreshold_ReturnsNoMatch() {
		List<String> sourceTeams = new ArrayList<>();
		sourceTeams.add("AA");
		List<String> targetTeams = new ArrayList<>();
		targetTeams.add("CCCCC");

		List<String> results = LevenshteinMatcher.matchTeams(sourceTeams, targetTeams, 3);
		assertEquals(1, results.size());
		assertTrue(results.contains("AA → No match (Distance: 5)")); // "aa" vs "ccccc" = 5
	}

	@Test
	void matchTeams_MultipleTeamsWithBestMatch_ReturnsCorrectMatches() {
		List<String> sourceTeams = new ArrayList<>();
		sourceTeams.add("Atlético Colegiales");
		sourceTeams.add("Manchester United U21");
		List<String> targetTeams = new ArrayList<>();
		targetTeams.add("Atletico Colegiales");
		targetTeams.add("Manchester United");

		List<String> results = LevenshteinMatcher.matchTeams(sourceTeams, targetTeams, 3);
		assertEquals(2, results.size());
		assertTrue(results.contains("Atlético Colegiales → Atletico Colegiales (Distance: 0)"));
		assertTrue(results.contains("Manchester United U21 → Manchester United (Distance: 0)"));
	}
}