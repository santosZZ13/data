package org.data.dto.ex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

public interface ExBetCommonDto {

	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ExBetMatchResponseDto {
		private String tntName;
		private Integer iid;
		private Boolean inPlay;
		private String homeName;
		private String awayName;
		private String slug;
		private LocalDateTime kickoffTime;
		@JsonProperty("fetched_date")
		private LocalDateTime fetchedDate;
	}

}
