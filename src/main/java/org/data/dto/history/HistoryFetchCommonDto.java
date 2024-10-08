package org.data.dto.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.conts.FetchStatus;
import org.data.persistent.entity.HistoryFetchEventEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public interface HistoryFetchCommonDto {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class HistoryFetchEventDto {
		private String id;
		private Integer teamId;
		private String name;
		private Long timeElapsed;
		private Integer total;
		private FetchStatus fetchStatus;
		private LocalDateTime createdDate;
		private LocalDateTime updatedDate;

		public static List<HistoryFetchEventDto> fromEntities(List<HistoryFetchEventEntity> historyFetchEventEntities) {
			List<HistoryFetchEventDto> historyFetchEventDto = new ArrayList<>();
			for (HistoryFetchEventEntity historyFetchEventEntity : historyFetchEventEntities) {
				historyFetchEventDto.add(fromEntity(historyFetchEventEntity));
			}
			return historyFetchEventDto;
		}

		public static HistoryFetchEventDto fromEntity(HistoryFetchEventEntity historyFetchEventEntity) {
			return historyFetchEventEntity != null ? HistoryFetchEventDto.builder()
					.id(historyFetchEventEntity.getId())
					.teamId(historyFetchEventEntity.getTeamId())
					.name(historyFetchEventEntity.getTeam())
					.timeElapsed(historyFetchEventEntity.getTimeElapsed())
					.total(historyFetchEventEntity.getTotal())
					.fetchStatus(historyFetchEventEntity.getFetchStatus())
					.createdDate(historyFetchEventEntity.getCreatedDate())
					.updatedDate(historyFetchEventEntity.getUpdatedDate())
					.build() : null;
		}
	}

}
