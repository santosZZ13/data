package org.data.service.sf;

import lombok.AllArgsConstructor;
import org.data.dto.sf.GetScheduledMatchByName;
import org.data.dto.sf.GetScheduledMatchesByName;
import org.data.dto.sf.GetSofaMatchesByDate;
import org.data.dto.sf.SaveScheduledMatchDto;
import org.data.repository.sofa.SofaScheduledMatchRepository;
import org.data.response.sf.parent.SofaMatchResponseDetailDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SofaScheduledMatchServiceImpl implements SofaScheduledMatchService {
	private final SofaScheduledMatchRepository sofaScheduledMatchRepository;


	@Override
	public SaveScheduledMatchDto.Response saveScheduledMatches(SaveScheduledMatchDto.Request request) {
//		sofaScheduledMatchRepository.saveSofaScheduledMatches(request.getMatches());
//		return SaveScheduledMatchDto.Response.builder()
//				.message("Scheduled matches saved successfully")
//				.build();
		return null;
	}

	@Override
	public GetScheduledMatchesByName.Response findMatchesByName(String name) {
		return GetScheduledMatchesByName.Response.builder()
				.matches(sofaScheduledMatchRepository.findSofaScheduledMatchesByName(name))
				.build();
	}

	@Override
	public GetScheduledMatchByName.Response findMatchByName(String name) {
		return GetScheduledMatchByName.Response.builder()
				.matches(sofaScheduledMatchRepository.findSofaScheduledMatchByName(name))
				.build();
	}

	@Override
	public GetSofaMatchesByDate.Response getMatchesByDate(String date) {
		List<SofaMatchResponseDetailDto> matchesByDate = sofaScheduledMatchRepository.getMatchesByDate(date);
		return GetSofaMatchesByDate.Response.builder()
				.matches(matchesByDate)
				.size(matchesByDate.size())
				.build();
	}
}
