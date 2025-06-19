package org.data.external.sofa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.data.util.utils.DateUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SofaMatchResponseDetail {
	@JsonProperty("id")
	private Integer matchId;
	@JsonProperty("startTimestamp")
	private Long startTimestamp;
	//	@JsonIgnore // Bỏ qua field này khi serialize nếu không cần
	private String startTimeUtc; // Lưu thời gian UTC dưới dạng String
	private TournamentResponse tournament;
	private SeasonResponse season;
	private RoundInfoResponse roundInfo;
	private StatusResponse status;
	private TeamResponse homeTeam;
	private TeamResponse awayTeam;
	private ScoreResponse homeScore;
	private ScoreResponse awayScore;
	private String slug;

	public String getStartTimeUtc() {
		if (startTimestamp != null) {
			ZonedDateTime utcDateTime = Instant.ofEpochSecond(startTimestamp)
					.atZone(ZoneId.of("UTC"));
			return utcDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
		return null;
	}

	public void setStartTimestamp(Long startTimestamp) {
		this.startTimestamp = startTimestamp;
		if (startTimestamp != null) {
			this.startTimeUtc = DateUtils.fromTimestampToUtc(startTimestamp);
		}
	}
}
