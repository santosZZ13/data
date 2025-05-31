package org.data.util;

import java.text.Normalizer;

public class NormalizeTeamName {
	public static String normalize(String teamName) {
		if (teamName == null || teamName.trim().isEmpty()) {
			return "";
		}

		String normalized = teamName.toLowerCase().trim();

		normalized = Normalizer.normalize(normalized, Normalizer.Form.NFKD)
				.replaceAll("\\p{M}", ""); // Loại bỏ dấu (giữ lại ký tự cơ bản)

		normalized = normalized.replaceAll("\\b(club|fc|sc|afc|cf|u21|u19|reserves|ii|iii|if)\\b\\s*", "")
				.replaceAll("\\s+", " ")
				.trim();

		normalized = normalized.replaceAll("[^a-z0-9\\s]", "");
		normalized = normalized.trim().replaceAll("\\s+", " ");
		return normalized.isEmpty() ? teamName.toLowerCase().trim() : normalized;
	}
}
