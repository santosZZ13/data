package org.data.util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamUtils {

	private static final Map<String, String> TRANSLITERATION_MAP = new HashMap<>();

	static {
		TRANSLITERATION_MAP.put("kobenhavn", "copenhagen");
	}

	public static boolean areTeamNamesEqual(String nameFirst, String nameSecond) {
		// Stage 1:
		String appliedTransliterationFirst = applyTransliteration(normalizeTeamName(nameFirst));
		String appliedTransliterationSecond = applyTransliteration(normalizeTeamName(nameSecond));
		if (appliedTransliterationFirst.equals(appliedTransliterationSecond)) {
			return true;
		}

//		// Stage 2: "eif" and "Ekenas IF"
//		List<String> splitTeamFirst = Arrays.stream(appliedTransliterationFirst.split(" ")).toList();
//		List<String> splitTeamSecond = Arrays.stream(appliedTransliterationSecond.split(" ")).toList();
//
//		if (splitTeamFirst.size() > splitTeamSecond.size()) {
//			String first = splitTeamFirst.get(0);
//			String second = splitTeamFirst.get(1);
//			String fullName;
//			if (first.length() > second.length()) {
//				fullName = first.charAt(0) + second;
//			} else {
//				fullName = second.charAt(0) + first;
//			}
//
//			if (fullName.equals(splitTeamSecond.get(0))) {
//				return true;
//			}
//		} else if (splitTeamFirst.size() < splitTeamSecond.size()) {
//
//			String first = splitTeamSecond.get(0);
//			String second = splitTeamSecond.get(1);
//			String fullName;
//			if (first.length() > second.length()) {
//				fullName = first.charAt(0) + second;
//			} else {
//				fullName = second.charAt(0) + first;
//			}
//
//			if (fullName.equals(splitTeamFirst.get(0))) {
//				return true;
//			}
//		}
//
//		// Stage 3: Feyenoord Rotterdam vs feyenoord
//		List<String> appliedFirst = Arrays.stream(appliedTransliterationFirst.split(" ")).toList();
//		List<String> appliedSecond = Arrays.stream(appliedTransliterationSecond.split(" ")).toList();
//		for (String ele : appliedFirst) {
//			if (appliedSecond.contains(ele)) {
//				return true;
//			}
//		}
		return false;
	}

	private static String applyTransliteration(String name) {
		String[] words = name.split(" ");
		StringBuilder transliteratedName = new StringBuilder();

		for (String word : words) {
			if (TRANSLITERATION_MAP.containsKey(word)) {
				transliteratedName.append(TRANSLITERATION_MAP.get(word)).append(" ");
			} else {
				transliteratedName.append(word).append(" ");
			}
		}

		return transliteratedName.toString().trim();
	}


	private static String normalizeTeamName(String name) {
		// Convert to lower case
		name = name.toLowerCase();
		// Remove special characters and accents
		name = Normalizer.normalize(name, Normalizer.Form.NFD);
		name = name.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
		name = name.replaceAll("ø", "o");
		name = name.replaceAll("å", "a");
		name = name.replaceAll("-", " ");
		// Remove common suffixes
		name = name.replaceAll("\\b(fc|bk|ksv|eh|ff|il|tf|us|de|sk|fk)\\b", "");
		// Remove extra spaces
		name = name.replaceAll("\\s+", " ").trim();
		return name;
	}
}
