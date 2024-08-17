package org.data.sofa.repository;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.conts.FetchStatus;
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
@Log4j2
public class HistoryFetchEventRepositoryImpl implements HistoryFetchEventRepository {

	private final HistoryFetchEventMongoRepository historyFetchEventMongoRepository;
	private final HistoryFetchEventMapper historyFetchEventMapper;


	@Override
	public GetHistoryFetchEventDto.GetHistoryFetchEventData findAllByStatusAndStartTimestampBetween(String status, LocalDateTime fromDate, LocalDateTime toDate, PageRequest pageRequest) {

		Page<HistoryFetchEventEntity> historyFetchEventEntitiesResult = historyFetchEventMongoRepository.findByFetchStatusAndCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(status, fromDate, toDate, pageRequest);
		List<HistoryFetchEventEntity> content = historyFetchEventEntitiesResult.getContent();
		List<GetHistoryFetchEventDto.HistoryFetchEventDto> historyFetchEventDto = new ArrayList<>();

		content.forEach(historyFetchEventEntity -> {
			historyFetchEventDto.add(historyFetchEventMapper.toHistoryFetchEventDto(historyFetchEventEntity));
		});

		return GetHistoryFetchEventDto.GetHistoryFetchEventData.builder()
				.history(historyFetchEventDto)
				.pageNumber(historyFetchEventEntitiesResult.getNumber())
				.pageSize(historyFetchEventEntitiesResult.getSize())
				.totals((int) historyFetchEventEntitiesResult.getTotalElements())
				.build();
	}

	@Override
	public List<Integer> getAllIds() {
		List<HistoryFetchEventEntity> entities = historyFetchEventMongoRepository.findAll();
		return entities
				.stream()
				.map(HistoryFetchEventEntity::getTeamId)
				.toList();
	}

	@Override
	public boolean isExistByTeamId(Integer eventId) {
		return historyFetchEventMongoRepository.findByTeamId(eventId).isPresent();
	}

	@Override
	public void saveHistoryEventWithIds(List<Integer> ids) {
		List<Integer> idsExist = getAllIds();
		List<Integer> idsToFetch = ids.stream()
				.filter(id -> !idsExist.contains(id))
				.toList();
		List<HistoryFetchEventEntity> historyFetchEventEntities = new ArrayList<>();

		for (Integer id : idsToFetch) {
			HistoryFetchEventEntity historyFetchEventEntity = HistoryFetchEventEntity.builder()
					.teamId(id)
					.fetchStatus(FetchStatus.NOT_FETCHED)
					.createdDate(LocalDateTime.now())
					.build();
			historyFetchEventEntities.add(historyFetchEventEntity);
		}
		historyFetchEventMongoRepository.saveAll(historyFetchEventEntities);
	}
}
