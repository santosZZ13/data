package org.data.service.ex;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.cache.SofaCache;
import org.data.dto.common.*;
import org.data.dto.ex.*;
import org.data.exception.AnalysisProcessingException;
import org.data.exception.ExternalServiceException;
import org.data.exception.InvalidRequestException;
import org.data.exception.TeamNotFoundException;
import org.data.repository.ex.ExBetRepository;
import org.data.repository.sofa.SofaRepository;
import org.data.response.ex.ExBetResponse;
import org.data.response.ex.ExBetTournamentResponse;
import org.data.response.sf.parent.SofaMatchResponseDetailDto;
import org.data.util.LevenshteinMatcher;
import org.data.util.NormalizeTeamName;
import org.data.util.analyzer.MatchAnalyzer;
import org.data.util.analyzer.TeamAnalyzer;
import org.data.util.response.ErrorCodeRegistry;
import org.data.util.service.SofaApiService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
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
	private final SofaCache sofaCache;
	private final ExecutorService executorService;

	@Override
	public ImportMatchesJsonFile.Response getDataFile(MultipartFile file) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			InputStream inputStream = file.getInputStream();
			ExBetResponse exBetResponse = objectMapper.readValue(inputStream, ExBetResponse.class);
			ExBetResponse.Data data = exBetResponse.getData();
			List<ExBetTournamentResponse> tournaments = data.getTournaments();
			List<ExBetMatchResponseDto> exBetMatchResponseDtos = convertToExBetMatchResponseDto(tournaments);
			int saveToDB = saveToDB(exBetMatchResponseDtos);
			return ImportMatchesJsonFile.Response.builder()
					.matches(exBetMatchResponseDtos)
					.totalMatches(exBetMatchResponseDtos.size())
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
		List<ExBetMatchResponseDto> matchesDto = request.getMatches();
		if (matchesDto != null && !matchesDto.isEmpty()) {
			for (ExBetMatchResponseDto match : matchesDto) {
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


	private List<ExBetMatchResponseDto> convertToExBetMatchResponseDto(List<ExBetTournamentResponse> tournaments) {
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

	@Override
	public MatchWithSofaDto.Response getMatchesWithSofa(MatchWithSofaDto.Request request) {
		List<MatchedMatchesDto> result = new ArrayList<>();
		for (ExBetMatchResponseDto exBetMatchResponseDto : request.getMatches()) {
			MatchedMatchesDto matchedMatch = exBetRepository.getMatchedMatch(exBetMatchResponseDto);
			result.add(matchedMatch);
		}
		return MatchWithSofaDto.Response.builder()
				.matches(result)
				.build();
	}

	public int saveToDB(List<ExBetMatchResponseDto> exBetMatchResponseDtos) {
//		return exBetRepository.saveExBetMatchDto(exBetMatchResponseDtos);
		return 0;
	}

	@Override
	public SaveExBetMatchDto.Response saveMatches(SaveExBetMatchDto.Request request, String date) {
		sofaRepository.getMatchesByDate(date);

		List<ExBetMatchResponseDto> exBetMatchResponseFromDB = exBetRepository.getExBetByDate(date);
		List<ExBetMatchRequestDto> matchesFromRequest = request.getMatches();
		SaveExBetMatchDto.Response responses = new SaveExBetMatchDto.Response();

		if (matchesFromRequest == null || matchesFromRequest.isEmpty()) {
			matchingMatches(exBetMatchResponseFromDB);
			responses.setMatches(exBetMatchResponseFromDB);
			return responses;
		}

		updateEndedMatches(matchesFromRequest, exBetMatchResponseFromDB);
		exBetRepository.saveExBetMatchDto(toExBetMatchResponseDto(matchesFromRequest));
		List<ExBetMatchResponseDto> exBetMatchesByDate = exBetRepository.getExBetByDate(date);
		matchingMatches(exBetMatchesByDate);
		responses.setMatches(exBetMatchesByDate);
		return responses;
	}

	@Override
	public GetAnalystDto.Response getAnalyst(GetAnalystDto.Request request) {
		try {
			if (request.getMatches() == null || request.getMatches().isEmpty()) {
				throw new InvalidRequestException("Matches list cannot be null or empty", ErrorCodeRegistry.INVALID_REQUEST);
			}
			List<ExBetMatchResponseDto> matches = request.getMatches();
			List<GetAnalystDto.MatchAnalysisDto> analyzedMatches = new ArrayList<>();
			Set<Integer> teamIds = getIds(matches);
			Map<Integer, List<SofaMatchResponseDetailDto>> teamHistories = getHistories(teamIds);

			for (ExBetMatchResponseDto match : matches) {
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

				List<SofaMatchResponseDetailDto> historiesForHome = teamHistories.getOrDefault(sofaHomeId, List.of());
				List<SofaMatchResponseDetailDto> historiesForAway = teamHistories.getOrDefault(sofaAwayId, List.of());

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
				List<SofaMatchResponseDetailDto> headToHead = fetchHeadToHead(sofaHomeId, sofaAwayId);
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
														  ExBetMatchResponseDto match,
														  List<SofaMatchResponseDetailDto> histories) {
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
				String teamHomeNameSofa = match.getSofaData().getSofaHomeName();
				analysisDto = GetAnalystDto.TeamAnalysisDto.builder()
						.teamId(teamId)
						.teamName(teamHomeNameSofa)
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


	private Set<Integer> getIds(List<ExBetMatchResponseDto> matches) {
		return matches.stream()
				.flatMap(match -> Stream.of(match.getSofaData().getSofaHomeId(), match.getSofaData().getSofaAwayId()))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}

	private Map<Integer, List<SofaMatchResponseDetailDto>> getHistories(Set<Integer> teamIds) {
		Map<Integer, List<SofaMatchResponseDetailDto>> teamHistories = new HashMap<>();
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		for (Integer teamId : teamIds) {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					List<SofaMatchResponseDetailDto> history = sofaCache.getTeamHistory(teamId);
					if (history == null) {
						history = sofaApiService.getSofaTeamId(teamId, 10);
						if (history == null) {
							throw new TeamNotFoundException(
									String.format("No history found for team ID: %d", teamId),
									ErrorCodeRegistry.NOT_FOUND_EVENT
							);
						}
						sofaCache.putTeamHistory(teamId, history);
					}
					synchronized (teamHistories) {
						teamHistories.put(teamId, history);
					}
				} catch (Exception e) {
					throw new ExternalServiceException(
							String.format("Failed to fetch history for team ID: %d", teamId),
							ErrorCodeRegistry.EXTERNAL_SERVICE_ERROR
					);
				}
			}, executorService);
			futures.add(future);
		}

		try {
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
		} catch (CompletionException e) {
			if (e.getCause() instanceof ExternalServiceException || e.getCause() instanceof TeamNotFoundException) {
				throw (RuntimeException) e.getCause();
			}

			throw new ExternalServiceException(
					ErrorCodeRegistry.EXTERNAL_SERVICE_ERROR,
					"Failed to fetch team histories",
					e
			);
		}
		return teamHistories;
	}

	private void matchingMatches(List<ExBetMatchResponseDto> exBetMatchResponseDto) {
		for (ExBetMatchResponseDto dto : exBetMatchResponseDto) {
			String normalizedHomeName = NormalizeTeamName.normalize(dto.getHomeName());
			String normalizedAwayName = NormalizeTeamName.normalize(dto.getAwayName());

			List<SofaMatchDto> candidates = sofaRepository.findSofaMatchByName(normalizedHomeName);
			if (candidates == null || candidates.isEmpty()) {
				candidates = sofaRepository.findSofaMatchByName(normalizedAwayName);
			}

			if (candidates == null || candidates.isEmpty()) {
				dto.setIsMatched(false);
				dto.setSofaData(null);
			}

			SofaMatchDto bestMatch = null;
			int minDistance = Integer.MAX_VALUE;
			int threshold = 3;

			for (SofaMatchDto sofaMatch : candidates) {
				String sofaHome = sofaMatch.getHomeTeam().getNormalizedName();
				String sofaAway = sofaMatch.getAwayTeam().getNormalizedName();

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
				log.info("Found SofaScore match for 8xbet match: {} vs {} with SofaScore match: {} vs {}",
						dto.getHomeName(), dto.getAwayName(), bestMatch.getHomeTeam().getName(), bestMatch.getAwayTeam().getName());

				ExBetMatchCommonDto.SofaData sofa = ExBetMatchCommonDto.SofaData.builder()
						.sofaMatchId(bestMatch.getMatchId())
						.sofaHomeId(bestMatch.getHomeTeam().getId())
						.sofaAwayId(bestMatch.getAwayTeam().getId())
						.sofaHomeName(bestMatch.getHomeTeam().getName())
						.sofaAwayName(bestMatch.getAwayTeam().getName())
						.build();

				dto.setSofaData(sofa);
				dto.setIsMatched(Boolean.TRUE);
			} else {
				log.info("No SofaScore match found for 8xbet match: {} vs {}", dto.getHomeName(), dto.getAwayName());
				dto.setIsMatched(false);
				dto.setSofaData(null);
			}
		}
	}

	private void updateEndedMatches(List<ExBetMatchRequestDto> exBetMatchRequestDto, List<ExBetMatchResponseDto> exBetMatchResponseDtoFromDB) {
		List<Integer> requestMatchIds = exBetMatchRequestDto.stream()
				.map(ExBetMatchRequestDto::getId)
				.toList();

		List<Integer> endedMatchIds = exBetMatchResponseDtoFromDB.stream()
				.map(ExBetMatchResponseDto::getId)
				.filter(id -> !requestMatchIds.contains(id))
				.toList();

		if (!endedMatchIds.isEmpty()) {
			exBetRepository.updateStatusByIds(endedMatchIds, "finished");
		}
	}


	private List<ExBetMatchResponseDto> toExBetMatchResponseDto(List<ExBetMatchRequestDto> exBetMatchesRequestDto) {
		List<ExBetMatchResponseDto> exBetMatchesResponseDto = new ArrayList<>();
		exBetMatchesRequestDto.forEach(exBetMatchRequestDto -> {
			ExBetMatchResponseDto build = ExBetMatchResponseDto.builder()
					.id(exBetMatchRequestDto.getId())
					.tournamentName(exBetMatchRequestDto.getTournamentName())
					.kickoffTime(exBetMatchRequestDto.getKickoffTime())
					.homeId(exBetMatchRequestDto.getHomeId())
					.homeName(exBetMatchRequestDto.getHomeName())
					.awayId(exBetMatchRequestDto.getAwayId())
					.awayName(exBetMatchRequestDto.getAwayName())
					.status("notstarted")
					.round(exBetMatchRequestDto.getRound())
					.isMatched(false)
					.sofaData(null)
					.build();
			exBetMatchesResponseDto.add(build);
		});

		return exBetMatchesResponseDto;
	}
}
