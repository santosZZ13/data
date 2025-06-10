package org.data.persistent.repository;

import org.data.persistent.entity.ExBetMatchEntity;

import java.util.List;
import java.util.Map;

public interface ExBetCustomRepository {
	List<ExBetMatchEntity> findExBetMatchEntitiesByIds(List<Integer> ids);

	Map<Integer, ExBetMatchEntity> getEntitiesMap(List<Integer> ids);

	void saveAll(List<ExBetMatchEntity> entities);

}
