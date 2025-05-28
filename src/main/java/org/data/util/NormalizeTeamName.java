package org.data.util;

public class NormalizeTeamName {
	public static String normalize(String teamName) {
		if (teamName == null || teamName.trim().isEmpty()) {
			return "";
		}

		String normalized = teamName.toLowerCase().trim();
		normalized = normalized.replaceAll("\\b(fc|sc|afc|cf|u21|u19|reserves|ii|iii)\\b\\s*", "")
				.replaceAll("\\s+", " ")
				.trim();

		normalized = normalized.replaceAll("[^a-z0-9\\s]", "");
		normalized = normalized.trim().replaceAll("\\s+", " ");
		return normalized.isEmpty() ? teamName.toLowerCase().trim() : normalized;
	}
}
