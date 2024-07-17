package org.data.eightBet.service;

import org.data.common.model.GenericResponseWrapper;

public interface EightXBetService {
	GenericResponseWrapper getScheduledEventInPlay();

	GenericResponseWrapper getEventsByDate(String date);
}
