package org.data.service.ex;

import org.data.dto.ex.*;
import org.springframework.web.multipart.MultipartFile;

public interface ExService {
	ImportMatchesJsonFile.Response getDataFile(MultipartFile request);

	GetMatchesExByDateDto.Response getMatchesByDate(String[] date, boolean isFavorite);

	SaveMatchesDto.Response saveMatchesFavorite(SaveMatchesDto.Request request, boolean isFavorite);

	MatchWithSofaDto.Response getMatchesWithSofa(MatchWithSofaDto.Request request);

	SaveMatchExDto.Response saveMatches(SaveMatchExDto.Request request, String date);
}
