package org.data.dto.ex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.data.dto.sf.SfEventsCommonDto;

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

	@EqualsAndHashCode(callSuper = true)
	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ExBetMatchDetailsResponseDto extends ExBetMatchResponseDto {
		private SfEventsCommonDto.SfEventDto sofaDetail;

		public static ExBetMatchDetailsResponseDto of(ExBetMatchResponseDto exBetMatchResponseDto, SfEventsCommonDto.SfEventDto sofaDetail) {
			return ExBetMatchDetailsResponseDto
					.builder()
					.tntName(exBetMatchResponseDto.getTntName())
					.iid(exBetMatchResponseDto.getIid())
					.inPlay(exBetMatchResponseDto.getInPlay())
					.homeName(exBetMatchResponseDto.getHomeName())
					.awayName(exBetMatchResponseDto.getAwayName())
					.slug(exBetMatchResponseDto.getSlug())
					.kickoffTime(exBetMatchResponseDto.getKickoffTime())
					.fetchedDate(exBetMatchResponseDto.getFetchedDate())
					.sofaDetail(sofaDetail)
					.build();
		}
	}
}
