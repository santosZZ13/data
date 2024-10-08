package org.data.service.ex;

import org.data.dto.ex.FetchExBetWithSfEventByDate;
import org.data.dto.ex.GetExBetEventByDate;
import org.data.dto.ex.GetExBetEventByDateWithDetails;
import org.data.dto.ex.ImportExBetFromFile;
import org.springframework.web.multipart.MultipartFile;

public interface ExService {
	ImportExBetFromFile.Response getDataFile(MultipartFile file);

	GetExBetEventByDate.Response getExBetEventByDate(GetExBetEventByDate.Request request);

	GetExBetEventByDateWithDetails.Response getExBetEventByDateWithDetails(GetExBetEventByDateWithDetails.Request request);

	FetchExBetWithSfEventByDate.Response fetchExBetWithSfEventByDate(FetchExBetWithSfEventByDate.Request request);
}
