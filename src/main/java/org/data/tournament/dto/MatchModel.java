package org.data.tournament.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchModel {
	private String tnName;
	private String name;
	private String home;
	private String away;
	private TournamentResponse.Round round;
	private LocalDateTime kickoffTime;
}
