package org.data.tournament.persistent.repository;

import org.data.tournament.persistent.entity.MatchEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long> {
	@Override
	<S extends MatchEntity> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);
}
