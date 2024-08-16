package org.data.sofa.repository;

import lombok.AllArgsConstructor;
import org.data.persistent.entity.HistoryFetchEventEntity;
import org.data.persistent.repository.HistoryFetchEventMongoRepository;
import org.data.sofa.dto.GetHistoryFetchEventDto;
import org.data.sofa.mapper.HistoryFetchEventMapper;
import org.data.sofa.repository.impl.HistoryFetchEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HistoryFetchEventRepositoryImpl implements HistoryFetchEventRepository {

	private final HistoryFetchEventMongoRepository historyFetchEventMongoRepository;
	private final HistoryFetchEventMapper historyFetchEventMapper;

	@Override
	public List<GetHistoryFetchEventDto.Response> getHistoryFetchEventByStatus(GetHistoryFetchEventDto.Request request) {
		return List.of();
	}


	@Override
	public List<GetHistoryFetchEventDto.HistoryFetchEventDto> findAllByStatusAndStartTimestampBetween(String status, LocalDateTime fromDate, LocalDateTime toDate, PageRequest pageRequest) {

		Page<HistoryFetchEventEntity> historyFetchEventEntitiesResult = historyFetchEventMongoRepository.findByFetchStatusAndCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(status, fromDate, toDate, pageRequest);
		List<HistoryFetchEventEntity> content = historyFetchEventEntitiesResult.getContent();
		List<GetHistoryFetchEventDto.HistoryFetchEventDto> historyFetchEventDto = new ArrayList<>();

		content.forEach(historyFetchEventEntity -> {
			historyFetchEventDto.add(historyFetchEventMapper.toHistoryFetchEventDto(historyFetchEventEntity));
		});


		return historyFetchEventDto;
	}

	@Override
	public Optional<HistoryFetchEventEntity> findByTeamId(Integer id) {
		return Optional.empty();
	}
}
