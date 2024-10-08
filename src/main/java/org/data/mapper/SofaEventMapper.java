package org.data.mapper;

import org.data.dto.sf.GetEventScheduledDto;
import org.data.response.sf.EventChildResponse;

public interface SofaEventMapper {
	GetEventScheduledDto.ScheduledEventDto scheduledEventDto(EventChildResponse eventResponse);
}
