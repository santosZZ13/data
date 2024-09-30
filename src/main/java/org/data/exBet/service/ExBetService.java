package org.data.exBet.service;

import org.data.common.model.BaseResponse;
import org.data.dto.ex.GetExBetEventByDate;
import org.data.dto.ex.GetExBetEventByDateWithDetails;
import org.data.dto.ex.ImportExBetFromFile;
import org.springframework.web.multipart.MultipartFile;

public interface ExBetService {
	BaseResponse getScheduledEventInPlay();

	BaseResponse getEventsByDate(String date);

	ImportExBetFromFile.Response getDataFile(MultipartFile file);

	GetExBetEventByDate.Response getExBetEventByDate(GetExBetEventByDate.Request request);

	GetExBetEventByDateWithDetails.Response getExBetEventByDateWithDetails(GetExBetEventByDateWithDetails.Request request);
}
