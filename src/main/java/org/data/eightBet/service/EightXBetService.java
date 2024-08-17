package org.data.eightBet.service;

import org.data.common.model.BaseResponse;

public interface EightXBetService {
	BaseResponse getScheduledEventInPlay();

	BaseResponse getEventsByDate(String date);


}
