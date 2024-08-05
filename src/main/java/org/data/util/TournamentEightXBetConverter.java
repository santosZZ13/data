package org.data.util;


import org.data.eightBet.dto.EightXBetCommonResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.data.eightBet.dto.EightXBetEventsResponse.*;
import static org.data.persistent.entity.EventsEightXBetEntity.*;

public class TournamentEightXBetConverter {
	public static TeamEntity convertToTeamEntity(EightXBetCommonResponse.EightXBetTeamResponse eightXBetTeamResponse) {
		return TeamEntity.builder()
				.id(eightXBetTeamResponse.getId())
				.cid(eightXBetTeamResponse.getCid())
				.name(eightXBetTeamResponse.getName())
				.jerseyEntity(Objects.isNull(eightXBetTeamResponse.getJersey()) ? null : convertToJerseyEntity(eightXBetTeamResponse.getJersey()))
				.build();
	}

	private static JerseyEntity convertToJerseyEntity(EightXBetCommonResponse.EightXBetJerseyResponse jerseyResponse) {
		return Objects.isNull(jerseyResponse) ? null : JerseyEntity.builder()
				.base(jerseyResponse.getBase())
				.sleeve(jerseyResponse.getSleeve())
				.style(jerseyResponse.getStyle())
				.styleColor(jerseyResponse.getStyleColor())
				.shirtType(jerseyResponse.getShirtType())
				.sleeveDetails(jerseyResponse.getSleeveDetails())
				.build();
	}

	public static RoundEntity convertToRoundEntity(EightXBetCommonResponse.EightXBetRoundResponse roundResponse) {
		return Objects.isNull(roundResponse) ? null : RoundEntity.builder()
				.roundType(roundResponse.getRoundType())
				.roundName(roundResponse.getRoundName())
				.roundGroup(roundResponse.getRoundGroup())
				.roundGroupName(roundResponse.getRoundGroupName())
				.roundNumber(roundResponse.getRoundNumber())
				.build();
	}

	public static MarketInfoEntity convertToMarketInfoEntity(EightXBetCommonResponse.EightXBetMarketInfoResponse marketInfoResponse) {
		return Objects.isNull(marketInfoResponse) ? null : MarketInfoEntity.builder()
				.cr(marketInfoResponse.getCr())
				.ot(marketInfoResponse.getOt())
				.pk(marketInfoResponse.getPk())
				.otcr(marketInfoResponse.getOtcr())
				.ad(marketInfoResponse.getAd())
				.redCard(marketInfoResponse.getRedCard())
				.otRedCard(marketInfoResponse.getOtRedCard())
				.build();
	}

	public static MidsEntity convertToMidsEntity(EightXBetCommonResponse.EightXBetMidsResponse midsResponse) {
		return Objects.isNull(midsResponse) ? null : MidsEntity.builder()
				.fmid(midsResponse.getFmid())
				.bmid(midsResponse.getBmid())
				.amid(midsResponse.getAmid())
				.cmid(midsResponse.getCmid())
				.dmid(midsResponse.getDmid())
				.jmid(midsResponse.getJmid())
				.build();
	}

	public static List<AnchorEntity> convertToAnchorEntity(List<EightXBetCommonResponse.EightXBetAnchorResponse> anchorResponses) {
		return Objects.isNull(anchorResponses) ? null : anchorResponses.stream()
				.map(anchorResponse -> AnchorEntity.builder()
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
						.build())
				.collect(Collectors.toList());


	}

	public static List<GiftEntity> convertToGiftEntity(List<EightXBetCommonResponse.EightXBetGiftResponse> gifs) {

		return Objects.isNull(gifs) ? null : gifs.stream()
				.map(giftResponse -> GiftEntity.builder()
						.source(giftResponse.getSource())
						.type(giftResponse.getType())
						.info(giftResponse.getInfo())
						.build())
				.collect(Collectors.toList());
	}

	public static List<VideoEntity> convertToVideoEntity(List<EightXBetCommonResponse.EightXBetVideoResponse> videos) {
		return videos.stream()
				.map(video -> VideoEntity.builder()
						.info(video.getInfo())
						.type(video.getType())
						.source(video.getSource())
						.build())
				.collect(Collectors.toList());
	}

}
