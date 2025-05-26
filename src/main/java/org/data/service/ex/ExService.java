package org.data.service.ex;

import org.data.dto.ex.GetMatchesExByDateDto;
import org.data.dto.ex.ImportMatchesJsonFile;
import org.data.dto.ex.SaveMatchesDto;
import org.springframework.web.multipart.MultipartFile;

public interface ExService {
	ImportMatchesJsonFile.Response getDataFile(MultipartFile request);

	GetMatchesExByDateDto.Response getMatchesByDate(String[] date, boolean isFavorite);

	SaveMatchesDto.Response saveMatchesFavorite(SaveMatchesDto.Request request, boolean isFavorite);
}
