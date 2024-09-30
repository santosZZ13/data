package org.data.sofa.mapper;

import org.data.dto.sf.GetEventScheduledDto;
import org.data.sofa.response.EventChildResponse;

public interface SofaEventMapper {
	GetEventScheduledDto.ScheduledEventDto scheduledEventDto(EventChildResponse eventResponse);
}
