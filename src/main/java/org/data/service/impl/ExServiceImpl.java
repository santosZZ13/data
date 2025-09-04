package org.data.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.data.dto.common.ex.ExBetMatchCommonDto;
import org.data.dto.common.ex.ExBetMatchDto;
import org.data.dto.common.ex.ExBetMatchRequestDto;
import org.data.dto.ex.*;
import org.data.exception.AnalysisProcessingException;
import org.data.exception.ExternalServiceException;
import org.data.exception.InvalidRequestException;
import org.data.repository.ExBetRepository;
import org.data.repository.SofaRepository;
import org.data.external.ex.modal.ExBetResponse;
import org.data.external.ex.modal.ExBetTournamentResponse;
import org.data.external.sofa.model.SofaMatchResponseDetail;
import org.data.external.sofa.service.SofaApiService;
import org.data.service.ExService;
import org.data.util.LevenshteinMatcher;
import org.data.util.NormalizeTeamName;
import org.data.util.analyzer.TeamAnalyzer;
import org.data.util.response.ErrorCodeRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.data.util.analyzer.MatchAnalyzer.*;

@Service
@AllArgsConstructor
@Log4j2
public class ExServiceImpl implements ExService {

	private final ExBetRepository exBetRepository;
	private final SofaRepository sofaRepository;
	private final SofaApiService sofaApiService;


	@Override
	public ImportMatchesJsonFile.Response getDataFile(MultipartFile file) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			InputStream inputStream = file.getInputStream();
			ExBetResponse exBetResponse = objectMapper.readValue(inputStream, ExBetResponse.class);
			ExBetResponse.Data data = exBetResponse.getData();
			List<ExBetTournamentResponse> tournaments = data.getTournaments();
			List<ExBetMatchDto> exBetMatchDtos = convertToExBetMatchResponseDto(tournaments);
			int saveToDB = saveToDB(exBetMatchDtos);
			return ImportMatchesJsonFile.Response.builder()
					.matches(exBetMatchDtos)
					.totalMatches(exBetMatchDtos.size())
					.totalMatchesSaved(saveToDB)
					.build();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public GetMatchesExByDateDto.Response getMatchesByDate(String[] date, boolean isFavorite) {
//		List<GetMatchesExByDateDto.ExBetMatchDto> exBetByDate = exBetRepository.getExBetByDate(date, isFavorite);
//		return GetMatchesExByDateDto.Response.builder()
//				.matches(exBetByDate)
//				.build();
		return null;
	}

	// Implementation for saving matches will go here
	@Override
	public SaveMatchesDto.Response saveMatchesFavorite(SaveMatchesDto.Request request, boolean isFavorite) {
		List<ExBetMatchDto> matchesDto = request.getMatches();
		if (matchesDto != null && !matchesDto.isEmpty()) {
			for (ExBetMatchDto match : matchesDto) {
				match.setFavorite(isFavorite);
			}
			int savedCount = saveToDB(matchesDto);
			return SaveMatchesDto.Response.builder()
					.message("Matches saved successfully")
					.totalMatches(savedCount)
					.build();
		}
		return SaveMatchesDto.Response.builder()
				.message("No matches to save")
				.build();
	}


	private List<ExBetMatchDto> convertToExBetMatchResponseDto(List<ExBetTournamentResponse> tournaments) {
//		List<ExBetMatchDto> exBetMatchResponseDtos = new ArrayList<>();
//		for (ExBetTournamentResponse tournament : tournaments) {
//			String tournamentName = tournament.getName();
//			for (ExBetMatchResponse match : tournament.getMatches()) {
//				ExBetMatchDto exBetMatchResponseDto = ExBetMatchDto.builder()
//						.id(match.getIid())
//						.tournamentName(tournamentName)
//						.kickoffTime(TimeUtil.convertUnixTimestampToLocalDateTime(match.getKickoffTime()))
//						.homeId(match.getHome().getId())
//						.homeName(match.getHome().getName())
//						.awayId(match.getAway().getId())
//						.awayName(match.getAway().getName())
//						.round(ExBetMatchDto.RoundDto.builder()
//								.roundName(match.getRound().getRoundName())
//								.roundType(match.getRound().getRoundType())
//								.build())
//						.build();
//				exBetMatchResponseDtos.add(exBetMatchResponseDto);
//			}
//		}
//
//		return exBetMatchResponseDtos;
		return null;
	}


	public int saveToDB(List<ExBetMatchDto> exBetMatchDtos) {
//		return exBetRepository.saveExBetMatchDto(exBetMatchResponseDtos);
		return 0;
	}

	@Override
	public SaveExBetMatchDto.Response saveMatches(SaveExBetMatchDto.Request request, String date) {
		try {
			if (request == null || request.getMatches() == null) {
				log.warn("Invalid request: matches list is null for date {}", date);
				throw new InvalidRequestException("Matches list cannot be null", ErrorCodeRegistry.INVALID_REQUEST);
			}
			if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
				log.warn("Invalid date format: {}. Expected YYYY-MM-DD", date);
				throw new InvalidRequestException("Invalid date format. Expected YYYY-MM-DD", ErrorCodeRegistry.INVALID_REQUEST);
			}


			log.info("Fetching ExBet matches from DB for date: {}", date);
			List<ExBetMatchDto> matchesFromDB = exBetRepository.getExBetByDate(date);
			if (matchesFromDB == null) {
				matchesFromDB = List.of();
			}
			Map<Integer, ExBetMatchDto> combinedMatches = matchesFromDB.stream()
					.collect(
							Collectors.toMap(
									ExBetMatchDto::getId,
									dto -> dto,
									(existing, replacement) -> existing)
					);

			List<ExBetMatchRequestDto> matchesFromRequest = request.getMatches();
			List<ExBetMatchDto> matchDtoFromRequest = toExBetMatchDto(matchesFromRequest);

			for (ExBetMatchDto matchRequest : matchDtoFromRequest) {
				ExBetMatchDto existingMatchDto = combinedMatches.get(matchRequest.getId());
				if (existingMatchDto != null) {
					// Trận đã tồn tại trong DB, kiểm tra cần cập nhật không
					boolean isUpdateMatch = existingMatchDto.getIsMatched() == null || !existingMatchDto.getIsMatched() ||
							existingMatchDto.getSofaData() == null || existingMatchDto.getSofaData().getHomeScore() == null ||
							existingMatchDto.getSofaData().getAwayScore() == null;

					if (isUpdateMatch) {
						log.info("Updating match {} with new matching logic", existingMatchDto.getId());
						matchingMatches(existingMatchDto); // Matching lại trận đơn
					}
				} else {
					// Trận mới từ request, thêm vào danh sách
					log.info("Adding new match from request: {}", matchRequest.getId());
					matchingMatches(matchRequest);
					combinedMatches.put(matchRequest.getId(), matchRequest);
				}

			}

			List<ExBetMatchDto> allMatches = new ArrayList<>(combinedMatches.values());
			updateMatchStatuses(allMatches);
			log.info("Saving {} matches to DB for date: {}", allMatches.size(), date);
			exBetRepository.saveExBetMatchDto(allMatches);

			// Xử lý các trận trong DB nhưng không còn trong request (cập nhật trạng thái "finished" nếu cần)
//			List<Integer> requestMatchIds = matchesFromRequest.stream().map(ExBetMatchRequestDto::getId).toList();
//			List<Integer> endedMatchIds = matchesFromDB.stream()
//					.filter(m -> !requestMatchIds.contains(m.getId()) && !"finished".equals(m.getStatus()))
//					.map(ExBetMatchDto::getId)
//					.toList();
//			if (!endedMatchIds.isEmpty()) {
//				log.info("Updating status to 'finished' for {} matches", endedMatchIds.size());
//				exBetRepository.updateStatusByIds(endedMatchIds, "finished");
//			}

			// Trả về kết quả
			return processAndReturnResponse(allMatches);

		} catch (InvalidRequestException e) {
			log.warn("Invalid request for date {}: {}", date, e.getMessage());
			throw e;
		} catch (ExternalServiceException e) {
			log.error("External service error for date {}: {}", date, e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Failed to save matches for date {}: {}", date, e.getMessage(), e);
			throw new AnalysisProcessingException(
					"Failed to save matches for date: " + date,
					ErrorCodeRegistry.ANALYSIS_ERROR,
					e
			);
		}
	}


	/**
	 * Update match statuses and calculate time status based on kickoffTime.
	 *
	 * @param matches List of matches to update
	 */
	private void updateMatchStatuses(List<ExBetMatchDto> matches) {
		ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault()); // Sử dụng múi giờ hệ thống
		matches.forEach(match -> {
			Instant instant = Instant.ofEpochSecond(match.getKickoffTime());
			ZonedDateTime kickoffTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
			ZonedDateTime endTime = kickoffTime.plusMinutes(95);

			if (currentTime.isBefore(kickoffTime)) {
				match.setStatus("notstarted");
				match.setTime(calculateTimeBefore(kickoffTime, currentTime)); // "in X hours"
			} else if (currentTime.isBefore(endTime)) {
				match.setStatus("inprogress");
				match.setTime(null); // Không cần time cho inprogress
			} else {
				match.setStatus("finished");
				match.setTime(calculateTimeAfter(endTime, currentTime)); // "X minutes/hours/days/years ago"
			}
		});
	}


	/**
	 * Calculate time status for matches that have not started (in X hours).
	 *
	 * @param kickoffTime Start time of the match
	 * @param currentTime Current time
	 * @return String representing time remaining (e.g., "in 2 hours")
	 */
	private String calculateTimeBefore(ZonedDateTime kickoffTime, ZonedDateTime currentTime) {
		long minutesUntil = ChronoUnit.MINUTES.between(currentTime, kickoffTime);
		if (minutesUntil >= 1440) { // > 1 ngày
			long days = minutesUntil / 1440;
			return "in " + days + " days";
		} else if (minutesUntil >= 60) { // > 1 giờ
			long hours = minutesUntil / 60;
			return "in " + hours + " hours";
		} else {
			return "in " + minutesUntil + " minutes";
		}
	}

	/**
	 * Calculate time status for matches that have finished (X minutes/hours/days/years ago).
	 *
	 * @param endTime     End time of the match
	 * @param currentTime Current time
	 * @return String representing time elapsed (e.g., "18 minutes ago")
	 */
	private String calculateTimeAfter(ZonedDateTime endTime, ZonedDateTime currentTime) {
		long minutesAgo = ChronoUnit.MINUTES.between(endTime, currentTime);
		if (minutesAgo >= 525600) { // > 1 năm (365 ngày * 24 giờ * 60 phút)
			long years = minutesAgo / 525600;
			return years + " years ago";
		} else if (minutesAgo >= 1440) { // > 1 ngày
			long days = minutesAgo / 1440;
			return days + " days ago";
		} else if (minutesAgo >= 60) { // > 1 giờ
			long hours = minutesAgo / 60;
			return hours + " hours ago";
		} else {
			return minutesAgo + " minutes ago";
		}
	}


	//TODO: change to
	private String calculateTimeEnd(ZonedDateTime kickoffTime, ZonedDateTime currentTime) {
		ZonedDateTime endTime = kickoffTime.plusMinutes(95);
		long minutesAgo = ChronoUnit.MINUTES.between(endTime, currentTime);
		if (minutesAgo > 0) {
			return minutesAgo + " minutes ago";
		} else if (minutesAgo == 0) {
			return "just ended";
		} else {
			return "in future"; // Trường hợp hiếm (currentTime < endTime)
		}
	}

	private SaveExBetMatchDto.Response processAndReturnResponse(List<ExBetMatchDto> matches) {
		int total = matches.size();
		int totalMatched = (int) matches.stream().filter(ExBetMatchDto::getIsMatched).count();
		int totalUnmatched = total - totalMatched;

		return SaveExBetMatchDto.Response.builder()
				.total(total)
				.totalMatched(totalMatched)
				.totalUnmatched(totalUnmatched)
				.matches(matches)
				.build();
	}

	@Override
	public GetAnalystDto.Response getAnalyst(GetAnalystDto.Request request) {
		try {
			if (request.getMatches() == null || request.getMatches().isEmpty()) {
				throw new InvalidRequestException("Matches list cannot be null or empty", ErrorCodeRegistry.INVALID_REQUEST);
			}
			List<ExBetMatchDto> matches = request.getMatches();
			List<GetAnalystDto.MatchAnalysisDto> analyzedMatches = new ArrayList<>();
			Set<Integer> teamIds = getIds(matches);
			Map<Integer, List<SofaMatchResponseDetail>> teamHistories = sofaApiService.getHistoriesByTeamIds(teamIds);

			for (ExBetMatchDto match : matches) {
				if (match.getSofaData() == null) {
					log.warn("No SofaScore data for match: {} vs {}", match.getHomeName(), match.getAwayName());
					continue;
				}

				Integer sofaHomeId = match.getSofaData().getSofaHomeId();
				Integer sofaAwayId = match.getSofaData().getSofaAwayId();
				if (sofaHomeId == null || sofaAwayId == null) {
					log.warn("Missing team IDs for match: {} vs {}", match.getHomeName(), match.getAwayName());
					continue;
				}

				List<SofaMatchResponseDetail> historiesForHome = teamHistories.getOrDefault(sofaHomeId, List.of());
				List<SofaMatchResponseDetail> historiesForAway = teamHistories.getOrDefault(sofaAwayId, List.of());

				GetAnalystDto.TeamAnalysisDto homeTeamAnalysisDto = getTeamAnalysis(sofaHomeId, match, historiesForHome);
				GetAnalystDto.TeamAnalysisDto awayTeamAnalysisDto = getTeamAnalysis(sofaAwayId, match, historiesForAway);

				Double over05Index = TeamAnalyzer.calculateOver05Index(homeTeamAnalysisDto, awayTeamAnalysisDto);
				Double over15Index = TeamAnalyzer.calculateOver15Index(homeTeamAnalysisDto, awayTeamAnalysisDto);
				Double over25Index = TeamAnalyzer.calculateOver25Index(homeTeamAnalysisDto, awayTeamAnalysisDto);
				Double bttsIndex = TeamAnalyzer.calculateBttsIndex(homeTeamAnalysisDto, awayTeamAnalysisDto);
				Double firstHalfOver05Index = TeamAnalyzer.calculateFirstHalfOver05Index(homeTeamAnalysisDto, awayTeamAnalysisDto);
				Double firstHalfOver15Index = TeamAnalyzer.calculateFirstHalfOver15Index(homeTeamAnalysisDto, awayTeamAnalysisDto);
				Double firstHalfBttsIndex = TeamAnalyzer.calculateFirstHalfBttsIndex(homeTeamAnalysisDto, awayTeamAnalysisDto);
				String recommendedBet = TeamAnalyzer.determineRecommendedBet(over15Index, over25Index, bttsIndex, firstHalfOver05Index);
				List<SofaMatchResponseDetail> headToHead = fetchHeadToHead(sofaHomeId, sofaAwayId);
				if (headToHead.isEmpty()) {
					log.warn("No head-to-head data found for teams {} vs {}", sofaHomeId, sofaAwayId);
				}
				GetAnalystDto.MatchAnalysisDto matchAnalysisDto = GetAnalystDto.MatchAnalysisDto.builder()
						.match(match)
						.homeTeamAnalysis(homeTeamAnalysisDto)
						.awayTeamAnalysis(awayTeamAnalysisDto)
						.over05Index(over05Index)
						.over15Index(over15Index)
						.over25Index(over25Index)
						.bttsIndex(bttsIndex)
						.firstHalfOver05Index(firstHalfOver05Index)
						.firstHalfOver15Index(firstHalfOver15Index)
						.firstHalfBttsIndex(firstHalfBttsIndex)
//					.matchPriority(match.getTournamentId()) // Giả định tournamentId
//					.headToHead(headToHeadDtos)
						.recommendedBet(recommendedBet)
						.build();
				analyzedMatches.add(matchAnalysisDto);
			}
			return GetAnalystDto.Response.builder()
					.analyzedMatches(analyzedMatches)
					.build();
		} catch (ExternalServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new AnalysisProcessingException("Failed to process match analysis", ErrorCodeRegistry.ANALYSIS_ERROR, e);
		}

	}

	private GetAnalystDto.TeamAnalysisDto getTeamAnalysis(Integer teamId,
														  ExBetMatchDto match,
														  List<SofaMatchResponseDetail> histories) {
		GetAnalystDto.TeamAnalysisDto analysisDto = null;
		try {
			if (histories == null || histories.isEmpty()) {
				analysisDto = GetAnalystDto.TeamAnalysisDto.builder()
						.teamId(teamId)
						.totalMatchesAnalyzed(0)
						.build();
			} else {
				GetAnalystDto.TeamStats statsHomeSofa = calculateStats(histories, teamId);
				List<GetAnalystDto.RecentMatchDto> recentMatchesHomeSofa = convertRecentMatches(histories, teamId);
				String name = Objects.equals(match.getSofaData().getSofaHomeId(), teamId) ? match.getSofaData().getSofaHomeName() :
						match.getSofaData().getSofaAwayName();
				analysisDto = GetAnalystDto.TeamAnalysisDto.builder()
						.teamId(teamId)
						.teamName(name)
						.stats(statsHomeSofa)
						.totalMatchesAnalyzed(histories.size())
						.recentMatches(recentMatchesHomeSofa)
						.build();
			}
			return analysisDto;
		} catch (RuntimeException ex) {
			log.warn("Error in getting the analysis for teamId: {}", teamId);
			throw new RuntimeException(String.format("Error in getting the analysis for teamId: %s", teamId));
		}
	}


	private Set<Integer> getIds(List<ExBetMatchDto> matches) {
		return matches.stream()
				.flatMap(match -> Stream.of(match.getSofaData().getSofaHomeId(), match.getSofaData().getSofaAwayId()))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}


	private List<ExBetMatchDto> matchingMatches(List<ExBetMatchDto> exBetMatchDtos, String date) {
		try {
			log.info("Fetching SofaScore matches for date: {}", date);
			List<SofaMatchResponseDetail> sofaMatches = sofaRepository.getMatchesByDate(date);
			if (sofaMatches == null || sofaMatches.isEmpty()) {
				log.info("No SofaScore matches available for matching");
				exBetMatchDtos.forEach(dto -> {
					dto.setIsMatched(false);
					dto.setSofaData(null);
				});
				return exBetMatchDtos;
			}

			if (exBetMatchDtos == null || exBetMatchDtos.isEmpty()) {
				log.info("No ExBet matches to match with SofaScore data");
				return List.of();
			}

			// Normalize SofaScore team names once
			Map<SofaMatchResponseDetail, Pair<String, String>> sofaTeamNames = sofaMatches.stream()
					.collect(Collectors.toMap(
							sofaMatch -> sofaMatch,
							sofaMatch -> new Pair<>(
									NormalizeTeamName.normalize(sofaMatch.getHomeTeam().getName()),
									NormalizeTeamName.normalize(sofaMatch.getAwayTeam().getName())
							)
					));

			for (ExBetMatchDto dto : exBetMatchDtos) {
				if (dto.getHomeName() == null || dto.getAwayName() == null) {
					log.warn("Invalid match data: homeName or awayName is null for match ID {}", dto.getId());
					dto.setIsMatched(false);
					dto.setSofaData(null);
					continue;
				}

				String normalizedHomeName = NormalizeTeamName.normalize(dto.getHomeName());
				String normalizedAwayName = NormalizeTeamName.normalize(dto.getAwayName());

				SofaMatchResponseDetail bestMatch = null;
				int minDistance = Integer.MAX_VALUE;
				int threshold = 3;

				for (Map.Entry<SofaMatchResponseDetail, Pair<String, String>> entry : sofaTeamNames.entrySet()) {
					SofaMatchResponseDetail sofaMatch = entry.getKey();
					String sofaHome = entry.getValue().getFirst();
					String sofaAway = entry.getValue().getSecond();

					int homeDistance = LevenshteinMatcher.calculateLevenshteinDistance(normalizedHomeName, sofaHome);
					int awayDistance = LevenshteinMatcher.calculateLevenshteinDistance(normalizedHomeName, sofaAway);

					if (homeDistance <= threshold || awayDistance <= threshold) {
						String otherTeam = homeDistance <= threshold ? normalizedAwayName : normalizedHomeName;
						String otherSofaTeam = homeDistance <= threshold ? sofaAway : sofaHome;
						int otherDistance = LevenshteinMatcher.calculateLevenshteinDistance(otherTeam, otherSofaTeam);

						if (otherDistance <= threshold && (homeDistance + otherDistance) < minDistance) {
							minDistance = homeDistance + otherDistance;
							bestMatch = sofaMatch;
						}
					}
				}

				if (bestMatch != null) {
					log.info("Found SofaScore match for match: {} vs {} with SofaScore match: {} vs {}",
							dto.getHomeName(), dto.getAwayName(), bestMatch.getHomeTeam().getName(), bestMatch.getAwayTeam().getName());

					ExBetMatchCommonDto.SofaData sofa = ExBetMatchCommonDto.SofaData.builder()
							.sofaMatchId(bestMatch.getMatchId())
							.sofaHomeId(bestMatch.getHomeTeam().getId())
							.sofaAwayId(bestMatch.getAwayTeam().getId())
							.sofaHomeName(bestMatch.getHomeTeam().getName())
							.sofaAwayName(bestMatch.getAwayTeam().getName())
							.build();

					ExBetMatchCommonDto.ScoreData homeScoreData = ExBetMatchCommonDto.ScoreData.builder()
							.period1(bestMatch.getHomeScore().getPeriod1())
							.period2(bestMatch.getHomeScore().getPeriod2())
							.current(bestMatch.getHomeScore().getCurrent())
							.display(bestMatch.getHomeScore().getDisplay())
							.normalTime(bestMatch.getHomeScore().getNormalTime())
							.extra1(bestMatch.getHomeScore().getExtra1())
							.extra2(bestMatch.getHomeScore().getExtra2())
							.overtime(bestMatch.getHomeScore().getOvertime())
							.penalties(bestMatch.getHomeScore().getPenalties())
							.scoreEmpty(bestMatch.getHomeScore().getScoreEmpty())
							.aggregated(bestMatch.getHomeScore().getAggregated())
							.build();

					ExBetMatchCommonDto.ScoreData awayScoreData = ExBetMatchCommonDto.ScoreData.builder()
							.period1(bestMatch.getAwayScore().getPeriod1())
							.period2(bestMatch.getAwayScore().getPeriod2())
							.current(bestMatch.getAwayScore().getCurrent())
							.display(bestMatch.getAwayScore().getDisplay())
							.normalTime(bestMatch.getAwayScore().getNormalTime())
							.extra1(bestMatch.getAwayScore().getExtra1())
							.extra2(bestMatch.getAwayScore().getExtra2())
							.overtime(bestMatch.getAwayScore().getOvertime())
							.penalties(bestMatch.getAwayScore().getPenalties())
							.scoreEmpty(bestMatch.getAwayScore().getScoreEmpty())
							.aggregated(bestMatch.getAwayScore().getAggregated())
							.build();

					sofa.setHomeScore(homeScoreData);
					sofa.setAwayScore(awayScoreData);
					dto.setSofaData(sofa);
					dto.setIsMatched(true);

				} else {
					log.info("No SofaScore match found for match: {} vs {}", dto.getHomeName(), dto.getAwayName());
					dto.setIsMatched(false);
					dto.setSofaData(null);
				}
			}
			return exBetMatchDtos;
		} catch (Exception e) {
			log.error("Failed to match matches with SofaScore data: {}", e.getMessage(), e);
			throw new AnalysisProcessingException(
					"Failed to match matches with SofaScore data",
					ErrorCodeRegistry.ANALYSIS_ERROR,
					e
			);
		}
	}

	/**
	 * Match a single match with SofaScore data.
	 */
	private void matchingMatches(ExBetMatchDto match) {
		try {
			String date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(match.getKickoffTime()), ZoneId.systemDefault())
					.format(DateTimeFormatter.ISO_LOCAL_DATE);
			List<SofaMatchResponseDetail> sofaMatches = sofaRepository.getMatchesByDate(date);
			if (sofaMatches == null || sofaMatches.isEmpty()) {
				log.info("No SofaScore matches available for matching on date: {}", date);
				match.setIsMatched(false);
				match.setSofaData(null);
				return;
			}

			if (match.getHomeName() == null || match.getAwayName() == null) {
				log.warn("Invalid match data: homeName or awayName is null for match ID {}", match.getId());
				match.setIsMatched(false);
				match.setSofaData(null);
				return;
			}

			String normalizedHomeName = NormalizeTeamName.normalize(match.getHomeName());
			String normalizedAwayName = NormalizeTeamName.normalize(match.getAwayName());

			SofaMatchResponseDetail bestMatch = null;
			int minDistance = Integer.MAX_VALUE;
			int threshold = 3;

			Map<SofaMatchResponseDetail, Pair<String, String>> sofaTeamNames = sofaMatches.stream()
					.collect(Collectors.toMap(
							sofaMatch -> sofaMatch,
							sofaMatch -> new Pair<>(
									NormalizeTeamName.normalize(sofaMatch.getHomeTeam().getName()),
									NormalizeTeamName.normalize(sofaMatch.getAwayTeam().getName())
							)
					));

			for (Map.Entry<SofaMatchResponseDetail, Pair<String, String>> entry : sofaTeamNames.entrySet()) {
				SofaMatchResponseDetail sofaMatch = entry.getKey();
				String sofaHome = entry.getValue().getFirst();
				String sofaAway = entry.getValue().getSecond();

				int homeDistance = LevenshteinMatcher.calculateLevenshteinDistance(normalizedHomeName, sofaHome);
				int awayDistance = LevenshteinMatcher.calculateLevenshteinDistance(normalizedHomeName, sofaAway);

				if (homeDistance <= threshold || awayDistance <= threshold) {
					String otherTeam = homeDistance <= threshold ? normalizedAwayName : normalizedHomeName;
					String otherSofaTeam = homeDistance <= threshold ? sofaAway : sofaHome;
					int otherDistance = LevenshteinMatcher.calculateLevenshteinDistance(otherTeam, otherSofaTeam);

					if (otherDistance <= threshold && (homeDistance + otherDistance) < minDistance) {
						minDistance = homeDistance + otherDistance;
						bestMatch = sofaMatch;
					}
				}
			}

			if (bestMatch != null) {
				log.info("Found SofaScore match for match: {} vs {} with SofaScore match: {} vs {}",
						match.getHomeName(), match.getAwayName(), bestMatch.getHomeTeam().getName(), bestMatch.getAwayTeam().getName());

				ExBetMatchCommonDto.SofaData sofa = ExBetMatchCommonDto.SofaData.builder()
						.sofaMatchId(bestMatch.getMatchId())
						.sofaHomeId(bestMatch.getHomeTeam().getId())
						.sofaAwayId(bestMatch.getAwayTeam().getId())
						.sofaHomeName(bestMatch.getHomeTeam().getName())
						.sofaAwayName(bestMatch.getAwayTeam().getName())
						.build();

				ExBetMatchCommonDto.ScoreData homeScoreData = ExBetMatchCommonDto.ScoreData.builder()
						.period1(bestMatch.getHomeScore().getPeriod1())
						.period2(bestMatch.getHomeScore().getPeriod2())
						.current(bestMatch.getHomeScore().getCurrent())
						.display(bestMatch.getHomeScore().getDisplay())
						.normalTime(bestMatch.getHomeScore().getNormalTime())
						.extra1(bestMatch.getHomeScore().getExtra1())
						.extra2(bestMatch.getHomeScore().getExtra2())
						.overtime(bestMatch.getHomeScore().getOvertime())
						.penalties(bestMatch.getHomeScore().getPenalties())
						.scoreEmpty(bestMatch.getHomeScore().getScoreEmpty())
						.aggregated(bestMatch.getHomeScore().getAggregated())
						.build();

				ExBetMatchCommonDto.ScoreData awayScoreData = ExBetMatchCommonDto.ScoreData.builder()
						.period1(bestMatch.getAwayScore().getPeriod1())
						.period2(bestMatch.getAwayScore().getPeriod2())
						.current(bestMatch.getAwayScore().getCurrent())
						.display(bestMatch.getAwayScore().getDisplay())
						.normalTime(bestMatch.getAwayScore().getNormalTime())
						.extra1(bestMatch.getAwayScore().getExtra1())
						.extra2(bestMatch.getAwayScore().getExtra2())
						.overtime(bestMatch.getAwayScore().getOvertime())
						.penalties(bestMatch.getAwayScore().getPenalties())
						.scoreEmpty(bestMatch.getAwayScore().getScoreEmpty())
						.aggregated(bestMatch.getAwayScore().getAggregated())
						.build();

				sofa.setHomeScore(homeScoreData);
				sofa.setAwayScore(awayScoreData);
				match.setSofaData(sofa);
				match.setIsMatched(true);
			} else {
				log.info("No SofaScore match found for match: {} vs {}", match.getHomeName(), match.getAwayName());
				match.setIsMatched(false);
				match.setSofaData(null);
			}
		} catch (Exception e) {
			log.error("Failed to match match with SofaScore data: {}", e.getMessage(), e);
			throw new AnalysisProcessingException(
					"Failed to match match with SofaScore data",
					ErrorCodeRegistry.ANALYSIS_ERROR,
					e
			);
		}
	}


	private void updateEndedMatches(List<ExBetMatchRequestDto> matchesFromRequest, List<ExBetMatchDto> matchesFromDB) {
		try {
			List<Integer> requestMatchIds = matchesFromRequest.stream()
					.map(ExBetMatchRequestDto::getId)
					.toList();

			List<Integer> endedMatchIds = matchesFromDB.stream()
					.map(ExBetMatchDto::getId)
					.filter(id -> !requestMatchIds.contains(id))
					.toList();

			if (!endedMatchIds.isEmpty()) {
				log.info("Updating status to 'finished' for {} matches", endedMatchIds.size());
				exBetRepository.updateStatusByIds(endedMatchIds, "finished");
			}
		} catch (Exception e) {
			log.error("Failed to update ended matches: {}", e.getMessage(), e);
			throw new AnalysisProcessingException(
					"Failed to update ended matches",
					ErrorCodeRegistry.ANALYSIS_ERROR,
					e
			);
		}
	}


	private List<ExBetMatchDto> toExBetMatchDto(List<ExBetMatchRequestDto> matchesRequests) {
		try {
			if (matchesRequests == null || matchesRequests.isEmpty()) {
				return List.of();
			}
			List<ExBetMatchDto> matchesDto = new ArrayList<>();
			for (ExBetMatchRequestDto reqDto : matchesRequests) {
				if (reqDto.getId() == 0 || reqDto.getHomeName() == null || reqDto.getAwayName() == null) {
					log.warn("Invalid match data: {}", reqDto);
					throw new InvalidRequestException(
							"Invalid match data: ID, homeName, or awayName is null or invalid",
							ErrorCodeRegistry.INVALID_REQUEST
					);
				}
				ExBetMatchDto dto = ExBetMatchDto.builder()
						.id(reqDto.getId())
						.tournamentName(reqDto.getTournamentName())
						.kickoffTime(reqDto.getKickoffTime())
						.homeId(reqDto.getHomeId())
						.homeName(reqDto.getHomeName())
						.awayId(reqDto.getAwayId())
						.awayName(reqDto.getAwayName())
						.status("notstarted")
						.round(reqDto.getRound())
						.isMatched(false)
						.sofaData(null)
						.build();
				matchesDto.add(dto);
			}
			return matchesDto;
		} catch (InvalidRequestException e) {
			throw e;
		} catch (Exception e) {
			log.error("Failed to convert match request to response DTO: {}", e.getMessage(), e);
			throw new AnalysisProcessingException(
					"Failed to convert match request to response DTO",
					ErrorCodeRegistry.ANALYSIS_ERROR,
					e
			);
		}
	}

	@Getter
	@Setter
	@AllArgsConstructor
	private static class Pair<F, S> {
		private final F first;
		private final S second;
	}
}
