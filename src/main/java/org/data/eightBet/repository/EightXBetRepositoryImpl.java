package org.data.eightBet.repository;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.eightBet.dto.EventInPlayDTO;
import org.data.persistent.entity.EventsEightXBetEntity;
import org.data.persistent.projection.EventsEightXBetProjection;
import org.data.persistent.repository.ScheduledEventsEightXBetMongoRepository;
import org.data.util.TimeUtil;
import org.data.util.TournamentEightXBetConverter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.data.eightBet.dto.ScheduledEventInPlayEightXBetResponse.*;

@Repository
@AllArgsConstructor
@Log4j2
public class EightXBetRepositoryImpl implements EightXBetRepository {

	private final ScheduledEventsEightXBetMongoRepository scheduledEventsEightXBetMongoRepository;

	@Override
	public List<EventInPlayDTO> getAllInPlayMatches() {
		return List.of();
	}

	@Override
	public List<EventsEightXBetEntity> saveTournamentResponse(List<TournamentResponse> tournamentResponses) {

		List<EventsEightXBetEntity> scheduledEventsEightXBetEntities = new ArrayList<>();
		List<Integer> iIds = scheduledEventsEightXBetMongoRepository.findAllByIId()
				.stream()
				.map(EventsEightXBetProjection::getIId)
				.toList();

		for (TournamentResponse tournamentResponse : tournamentResponses) {
			List<MatchResponse> matchesResponses = tournamentResponse.getMatches();
			for (MatchResponse matchesResponse : matchesResponses) {
				if (!iIds.contains(matchesResponse.getIid())) {
					log.info("Detected new match with iid: {}", matchesResponse);
					EventsEightXBetEntity eventsEightXBetEntity = populateScheduledEventsEightXBetEntity(tournamentResponse, matchesResponse);
					scheduledEventsEightXBetEntities.add(eventsEightXBetEntity);
				}

			}
		}

		scheduledEventsEightXBetMongoRepository.saveAll(scheduledEventsEightXBetEntities);
		return scheduledEventsEightXBetEntities;
	}

	private EventsEightXBetEntity populateScheduledEventsEightXBetEntity(TournamentResponse tournamentResponse, MatchResponse matchesResponse) {
		return EventsEightXBetEntity
				.builder()
				.sId(matchesResponse.getSid())
				.cId(matchesResponse.getCid())
				.tId(matchesResponse.getTid())
				.iId(matchesResponse.getIid())
				.countdown(matchesResponse.getCountdown())
				.state(matchesResponse.getState())
				.series(matchesResponse.getSeries())
				.vd(matchesResponse.getVd())
				.streaming(matchesResponse.getStreaming())
				.chatMid(matchesResponse.getChatMid())
				.gifMid(matchesResponse.getGifMid())
				.graphMid(matchesResponse.getGraphMid())
				.inplay(matchesResponse.getInplay())
				.video(matchesResponse.getVideo())
				.nv(matchesResponse.getNv())
				.scoreId(matchesResponse.getScoreId())
				.tnName(matchesResponse.getTnName())
				.tnPriority(matchesResponse.getTnPriority())
				.homeTeam(TournamentEightXBetConverter.convertToTeamEntity(matchesResponse.getHome()))
				.awayTeam(TournamentEightXBetConverter.convertToTeamEntity(matchesResponse.getAway()))
				.round(TournamentEightXBetConverter.convertToRoundEntity(matchesResponse.getRound()))
				.marketInfo(TournamentEightXBetConverter.convertToMarketInfoEntity(matchesResponse.getMarketInfo()))
				.mids(TournamentEightXBetConverter.convertToMidsEntity(matchesResponse.getMids()))
				.gifts(TournamentEightXBetConverter.convertToGiftEntity(matchesResponse.getGifs()))
				.videos(TournamentEightXBetConverter.convertToVideoEntity(matchesResponse.getVideos()))
				.anchors(TournamentEightXBetConverter.convertToAnchorEntity(matchesResponse.getAnchors()))
				.name(matchesResponse.getName())
				.kickoffTime(TimeUtil.convertUnixTimestampToLocalDateTime(matchesResponse.getKickoffTime()))
				.tournament(EventsEightXBetEntity
						.TournamentEntity
						.builder()
						.sId(tournamentResponse.getSid())
						.cId(tournamentResponse.getCid())
						.tId(tournamentResponse.getTid())
						.name(tournamentResponse.getName())
						.favorite(tournamentResponse.getFavorite())
						.priority(tournamentResponse.getPriority())
						.count(tournamentResponse.getCount())
						.build()
				)
				.build();
	}

}
