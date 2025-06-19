package org.data.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import org.data.external.sofa.model.SofaMatchResponseDetail;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class SofaCacheId {
	private final Cache<Integer, List<SofaMatchResponseDetail>> teamHistoryCache;

	public SofaCacheId() {
		this.teamHistoryCache = Caffeine.newBuilder()
				.expireAfterWrite(1, TimeUnit.HOURS) // Cache hết hạn sau 1 giờ
				.maximumSize(1000) // Giới hạn 1000 đội
				.build();
	}

	public List<SofaMatchResponseDetail> getTeamHistory(Integer teamId) {
		return teamHistoryCache.getIfPresent(teamId);
	}

	public void putTeamHistory(Integer teamId, List<SofaMatchResponseDetail> history) {
		teamHistoryCache.put(teamId, history);
	}
}
