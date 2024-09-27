package org.data.eightBet.service;

import org.data.common.model.BaseResponse;
import org.data.eightBet.dto.GetExBetEventByDate;
import org.data.eightBet.dto.ImportExBetFromFile;
import org.springframework.web.multipart.MultipartFile;

public interface ExBetService {
	BaseResponse getScheduledEventInPlay();

	BaseResponse getEventsByDate(String date);

	ImportExBetFromFile.Response getDataFile(MultipartFile file);

	GetExBetEventByDate.Response getExBetEventByDate(GetExBetEventByDate.Request request);
}
