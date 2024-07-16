package org.data.util;

import org.data.persistent.entity.ScheduledEventsEightXBetEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.data.eightBet.dto.ScheduledEventInPlayEightXBetResponse.*;
import static org.data.persistent.entity.ScheduledEventsEightXBetEntity.*;

public class TournamentEightXBetConverter {

	public static ScheduledEventsEightXBetEntity convertToScheduledEventsEightXBetEntity(TournamentResponse tournamentResponse) {

		List<MatchEntity> matchEntities = tournamentResponse
				.getMatches()
				.stream()
				.map(TournamentEightXBetConverter::convertToMatchEntity)
				.collect(Collectors.toList());

		return ScheduledEventsEightXBetEntity.builder()
				.sId(tournamentResponse.getSid())
				.tId(tournamentResponse.getTid())
				.cId(tournamentResponse.getCid())
				.name(tournamentResponse.getName())
				.favorite(tournamentResponse.getFavorite())
				.priority(tournamentResponse.getPriority())
				.count(tournamentResponse.getCount())
				.matchEntities(matchEntities)
				.build();
	}

	private static MatchEntity convertToMatchEntity(MatchResponse matchResponse) {

		List<GiftEntity> giftEntities = new ArrayList<>();
		matchResponse.getGifs().forEach(giftResponse -> {
			GiftEntity giftEntity = GiftEntity.builder()
					.source(giftResponse.getSource())
					.type(giftResponse.getType())
					.info(giftResponse.getInfo())
					.build();
			giftEntities.add(giftEntity);
		});


		List<AnchorEntity> anchorEntities = matchResponse
				.getAnchors()
				.stream()
				.map(TournamentEightXBetConverter::convertToAnchorEntity)
				.toList();


		return MatchEntity.builder()
				.sId(matchResponse.getSid())
				.cId(matchResponse.getCid())
				.tId(matchResponse.getTid())
				.iId(matchResponse.getIid())
				.countdown(matchResponse.getCountdown())
				.state(matchResponse.getState())
				.series(matchResponse.getSeries())
				.vd(matchResponse.getVd())
				.streaming(matchResponse.getStreaming())
				.chatMid(matchResponse.getChatMid())
				.gifMid(matchResponse.getGifMid())
				.graphMid(matchResponse.getGraphMid())
				.inplay(matchResponse.getInplay())
				.video(matchResponse.getVideo())
				.nv(matchResponse.getNv())
				.scoreId(matchResponse.getScoreId())
				.tnName(matchResponse.getTnName())
				.tnPriority(matchResponse.getTnPriority())
				.home(convertToTeamEntity(matchResponse.getHome()))
				.away(convertToTeamEntity(matchResponse.getAway()))
				.round(convertToRoundEntity(matchResponse.getRound()))
				.marketInfo(convertToMarketInfoEntity(matchResponse.getMarketInfo()))
				.mids(convertToMidsEntity(matchResponse.getMids()))
				.gifts(giftEntities)
				.videos(matchResponse.getVideos())
				.anchors(anchorEntities)
				.name(matchResponse.getName())
				.kickoffTime(matchResponse.getKickoffTime())
				.build();
	}

	private static TeamEntity convertToTeamEntity(TeamResponse teamResponse) {
		return TeamEntity.builder()
				.id(teamResponse.getId())
				.cid(teamResponse.getCid())
				.name(teamResponse.getName())
				.jerseyEntity(Objects.isNull(teamResponse.getJersey()) ? null : convertToJerseyEntity(teamResponse.getJersey()))
				.build();
	}

	private static JerseyEntity convertToJerseyEntity(JerseyResponse jerseyResponse) {
		return JerseyEntity.builder()
				.base(jerseyResponse.getBase())
				.sleeve(jerseyResponse.getSleeve())
				.style(jerseyResponse.getStyle())
				.styleColor(jerseyResponse.getStyleColor())
				.shirtType(jerseyResponse.getShirtType())
				.sleeveDetails(jerseyResponse.getSleeveDetails())
				.build();
	}

	private static RoundEntity convertToRoundEntity(RoundResponse roundResponse) {
		return RoundEntity.builder()
				.roundType(roundResponse.getRoundType())
				.roundName(roundResponse.getRoundName())
				.roundGroup(roundResponse.getRoundGroup())
				.roundGroupName(roundResponse.getRoundGroupName())
				.roundNumber(roundResponse.getRoundNumber())
				.build();
	}

	private static MarketInfoEntity convertToMarketInfoEntity(MarketInfoResponse marketInfoResponse) {
		return MarketInfoEntity.builder()
				.cr(marketInfoResponse.getCr())
				.ot(marketInfoResponse.getOt())
				.pk(marketInfoResponse.getPk())
				.otcr(marketInfoResponse.getOtcr())
				.ad(marketInfoResponse.getAd())
				.redCard(marketInfoResponse.getRedCard())
				.otRedCard(marketInfoResponse.getOtRedCard())
				.build();
	}

	private static MidsEntity convertToMidsEntity(MidsResponse midsResponse) {
		return MidsEntity.builder()
				.fmid(midsResponse.getFmid())
				.bmid(midsResponse.getBmid())
				.amid(midsResponse.getAmid())
				.cmid(midsResponse.getCmid())
				.dmid(midsResponse.getDmid())
				.jmid(midsResponse.getJmid())
				.build();
	}

	private static AnchorEntity convertToAnchorEntity(AnchorResponse anchorResponse) {
		return AnchorEntity.builder()
				.houseId(anchorResponse.getHouseId())
				.liveStatus(anchorResponse.getLiveStatus())
				.visitHistory(anchorResponse.getVisitHistory())
				.playStreamAddress(anchorResponse.getPlayStreamAddress())
				.playStreamAddress2(anchorResponse.getPlayStreamAddress2())
				.userImage(anchorResponse.getUserImage())
				.houseName(anchorResponse.getHouseName())
				.houseImage(anchorResponse.getHouseImage())
				.nickName(anchorResponse.getNickName())
				.anchorTypeName(anchorResponse.getAnchorTypeName())
				.fansCount(anchorResponse.getFansCount())
				.anchorTitle(anchorResponse.getAnchorTitle())
				.houseIntroduction(anchorResponse.getHouseIntroduction())
				.languageType(anchorResponse.getLanguageType())
				.vendors(anchorResponse.getVendors())
				.build();
	}
}
