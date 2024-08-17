package org.data.sofa.mapper;

import org.data.sofa.dto.SofaEventsDto;
import org.data.sofa.dto.SofaEventsResponse;

public interface SofaEventMapper {
	SofaEventsDto.EventDto toEventDto(SofaEventsResponse.EventResponse eventResponse);
}
