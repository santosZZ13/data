package org.data.service.history;

import lombok.AllArgsConstructor;
import org.data.conts.FetchStatus;
import org.data.dto.history.HistoryFetchEventDto;
import org.data.persistent.entity.HistoryFetchEventEntity;
import org.data.persistent.repository.HistoryFetchEventMongoRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HistoryFetchEventServiceImpl implements HistoryFetchEventService {

	private final HistoryFetchEventMongoRepository historyFetchEventMongoRepository;


	@Override
	public @Nullable HistoryFetchEventDto getHistoryFetchEventByTeamId(@NotNull Integer idTeam) {
		Optional<HistoryFetchEventEntity> historyFetchEventByTeamId = historyFetchEventMongoRepository.findByTeamId(idTeam);
		if (historyFetchEventByTeamId.isPresent()) {
			HistoryFetchEventEntity historyFetchEventEntity = historyFetchEventByTeamId.get();
			return HistoryFetchEventDto.fromEntity(historyFetchEventEntity);
		}
		return null;
	}

	@Override
	public List<HistoryFetchEventDto> getAllHistoryFetchEvents() {
		List<HistoryFetchEventEntity> historyFetchEventEntities = historyFetchEventMongoRepository.findAll();
		return HistoryFetchEventDto.fromEntities(historyFetchEventEntities);
	}

	@Override
	public void saveHistoryFetchEvent(HistoryFetchEventDto historyFetchEventDto) {

	}

	@Override
	public void saveHistoryFetchEvents(List<HistoryFetchEventDto> historyFetchEventDto) {

	}

	@Override
	public void saveHistoryEventWithIds(List<Integer> ids) {
		List<HistoryFetchEventEntity> historyFetchEventEntities = new ArrayList<>();
		for (Integer id : ids) {
			HistoryFetchEventEntity historyFetchEventEntity = HistoryFetchEventEntity.builder()
					.teamId(id)
					.fetchStatus(FetchStatus.NOT_FETCHED)
					.build();
			historyFetchEventEntities.add(historyFetchEventEntity);
		}
		historyFetchEventMongoRepository.saveAll(historyFetchEventEntities);
	}

	@Override
	public List<Integer> getHistoryFetchEventWithStatus(FetchStatus status) {
		List<HistoryFetchEventEntity> historyFetchEventEntitiesWithStatusFetched = historyFetchEventMongoRepository.findHistoryFetchEventEntitiesWithStatus(status.name());
		if (!historyFetchEventEntitiesWithStatusFetched.isEmpty()) {
			List<Integer> ids = new ArrayList<>();
			for (HistoryFetchEventEntity historyFetchEventEntity : historyFetchEventEntitiesWithStatusFetched) {
				ids.add(historyFetchEventEntity.getTeamId());
			}
			return ids;
		}
		return List.of();
	}
}
