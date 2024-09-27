package org.data.sofa.mapper;

import org.data.sofa.dto.GetEventScheduledDto;
import org.data.sofa.dto.SofaEventsDto;
import org.data.sofa.response.EventChildResponse;

public interface SofaEventMapper {
	GetEventScheduledDto.ScheduledEventDto scheduledEventDto(EventChildResponse eventResponse);
}
