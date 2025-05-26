package org.data.service.sf;

import lombok.AllArgsConstructor;
import org.data.dto.sf.SaveScheduledMatchDto;
import org.data.repository.sofa.SofaScheduledMatchRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SofaScheduledMatchServiceImpl implements SofaScheduledMatchService {
	private final SofaScheduledMatchRepository sofaScheduledMatchRepository;


	@Override
	public SaveScheduledMatchDto.Response saveScheduledMatches(SaveScheduledMatchDto.Request request) {
		sofaScheduledMatchRepository.saveSofaScheduledMatches(request.getMatches());
		return SaveScheduledMatchDto.Response.builder()
				.message("Scheduled matches saved successfully")
				.build();
	}
}
