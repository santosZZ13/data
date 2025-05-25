package org.data.service.ex;

import org.data.dto.GetMatchesExByDateDto;
import org.data.dto.ImportMatchesJsonFile;
import org.data.dto.SaveMatchesDto;
import org.springframework.web.multipart.MultipartFile;

public interface ExService {
	ImportMatchesJsonFile.Response getDataFile(MultipartFile request);

	GetMatchesExByDateDto.Response getMatchesByDate(String[] date);

	SaveMatchesDto.Response saveMatchesFavorite(SaveMatchesDto.Request request, boolean isFavorite);
}
