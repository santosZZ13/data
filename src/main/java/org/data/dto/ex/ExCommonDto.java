package org.data.dto.ex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.data.dto.sf.SfEventsCommonDto;

import java.time.LocalDateTime;

public interface ExCommonDto {

	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ExMatchResponseDto {
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
	class ExMatchDetailsResponseDto extends ExMatchResponseDto {
		private SfEventsCommonDto.SfEventDto sofaDetail;

		public static ExMatchDetailsResponseDto of(ExMatchResponseDto exMatchResponseDto, SfEventsCommonDto.SfEventDto sofaDetail) {
			return ExMatchDetailsResponseDto
					.builder()
					.tntName(exMatchResponseDto.getTntName())
					.iid(exMatchResponseDto.getIid())
					.inPlay(exMatchResponseDto.getInPlay())
					.homeName(exMatchResponseDto.getHomeName())
					.awayName(exMatchResponseDto.getAwayName())
					.slug(exMatchResponseDto.getSlug())
					.kickoffTime(exMatchResponseDto.getKickoffTime())
					.fetchedDate(exMatchResponseDto.getFetchedDate())
					.sofaDetail(sofaDetail)
					.build();
		}
	}
}
