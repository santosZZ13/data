package org.data.service;

import org.data.dto.ex.*;
import org.springframework.web.multipart.MultipartFile;

public interface ExService {
	ImportMatchesJsonFile.Response getDataFile(MultipartFile request);

	GetMatchesExByDateDto.Response getMatchesByDate(String[] date, boolean isFavorite);

	SaveMatchesDto.Response saveMatchesFavorite(SaveMatchesDto.Request request, boolean isFavorite);


	/**
	 * Saves match information for a specified date.
	 *
	 * @param date the date to associate with the match data
	 * @param request the request object containing match details to be saved
	 * @return a response object containing the list of saved matches
	 */
	SaveExBetMatchDto.Response saveMatches(SaveExBetMatchDto.Request request, String date);

	GetAnalystDto.Response getAnalyst(GetAnalystDto.Request request);
}
