package org.data.tournament.persistent.entity;

import lombok.*;
import org.data.tournament.dto.TournamentResponse;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "pre_matches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String tnName;
	private String name;
	private String home;
	private String away;
	private String round;
	private LocalDateTime kickoffTime;
}
