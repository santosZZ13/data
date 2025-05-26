package org.data.service.sf;

import org.data.dto.sf.SaveScheduledMatchDto;

public interface SofaScheduledMatchService {
	/**
	 * Saves scheduled matches.
	 *
	 * @param request the request containing matches to be saved
	 * @return a response indicating the result of the save operation
	 */
	SaveScheduledMatchDto.Response saveScheduledMatches(SaveScheduledMatchDto.Request request);
}
