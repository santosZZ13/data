package org.data.service.ex;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.dto.common.*;
import org.data.dto.ex.*;
import org.data.exception.AnalysisProcessingException;
import org.data.exception.ExternalServiceException;
import org.data.exception.InvalidRequestException;
import org.data.repository.ex.ExBetRepository;
import org.data.repository.sofa.SofaRepository;
import org.data.external.ex.modal.ExBetResponse;
import org.data.external.ex.modal.ExBetTournamentResponse;
import org.data.external.sofa.model.SofaMatchResponseDetail;
import org.data.external.sofa.service.SofaApiService;
import org.data.util.LevenshteinMatcher;
import org.data.util.NormalizeTeamName;
import org.data.util.analyzer.TeamAnalyzer;
import org.data.util.response.ErrorCodeRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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


	public int saveToDB(List<ExBetMatchResponseDto> exBetMatchResponseDtos) {
//		return exBetRepository.saveExBetMatchDto(exBetMatchResponseDtos);
		return 0;
	}

	@Override
	public SaveExBetMatchDto.Response saveMatches(SaveExBetMatchDto.Request request, String date) {
		try {
			if (request == null || request.getMatches() == null) {
				log.warn("Invalid request: matches list is null");
				throw new InvalidRequestException(
						"Matches list cannot be null",
						ErrorCodeRegistry.INVALID_REQUEST
				);
			}
			log.info("Fetching Sofa matches for date: {}", date);
			sofaRepository.getMatchesByDate(date);

			log.info("Fetching ExBet matches for date: {}", date);
			List<ExBetMatchResponseDto> exBetMatchResponseFromDB = exBetRepository.getExBetByDate(date);
			if (exBetMatchResponseFromDB == null) {
				log.warn("No ExBet matches found for date: {}", date);
				exBetMatchResponseFromDB = List.of();
			}


			List<ExBetMatchRequestDto> matchesFromRequest = request.getMatches();
			SaveExBetMatchDto.Response responses = new SaveExBetMatchDto.Response();

			if (matchesFromRequest == null || matchesFromRequest.isEmpty()) {
				log.info("No matches in request, processing existing matches for date: {}", date);
				matchingMatches(exBetMatchResponseFromDB);
				responses.setMatches(exBetMatchResponseFromDB);
				return responses;
			}

			log.info("Updating ended matches for date: {}", date);
			updateEndedMatches(matchesFromRequest, exBetMatchResponseFromDB);

			log.info("Saving new matches for date: {}", date);
			exBetRepository.saveExBetMatchDto(toExBetMatchResponseDto(matchesFromRequest));

			log.info("Fetching updated ExBet matches for date: {}", date);
			List<ExBetMatchResponseDto> exBetMatchesByDate = exBetRepository.getExBetByDate(date);
			if (exBetMatchesByDate == null) {
				log.warn("No updated ExBet matches found for date: {}", date);
				exBetMatchesByDate = List.of();
			}
			matchingMatches(exBetMatchesByDate);
			responses.setMatches(exBetMatchesByDate);
			return responses;
		} catch (Exception ex) {
			return null;
		}
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
			Map<Integer, List<SofaMatchResponseDetail>> teamHistories = sofaApiService.getHistoriesByTeamIds(teamIds);

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
														  ExBetMatchResponseDto match,
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


	private void matchingMatches(List<ExBetMatchResponseDto> exBetMatchResponseDto) {
		try {
			if (exBetMatchResponseDto == null || exBetMatchResponseDto.isEmpty()) {
				log.info("No matches to match with SofaScore data");
				return;
			}

			for (ExBetMatchResponseDto dto : exBetMatchResponseDto) {
				String normalizedHomeName = NormalizeTeamName.normalize(dto.getHomeName());
				String normalizedAwayName = NormalizeTeamName.normalize(dto.getAwayName());

				List<SofaMatchDto> candidates = sofaRepository.findSofaMatchByName(normalizedHomeName);
				if (candidates == null || candidates.isEmpty()) {
					candidates = sofaRepository.findSofaMatchByName(normalizedAwayName);
				}

				if (candidates == null || candidates.isEmpty()) {
					log.info("No SofaScore candidates found for match: {} vs {}", dto.getHomeName(), dto.getAwayName());
					dto.setIsMatched(false);
					dto.setSofaData(null);
					continue;
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
		} catch (Exception e) {
			log.error("Failed to match matches with SofaScore data: {}", e.getMessage(), e);
			throw new AnalysisProcessingException(
					"Failed to match matches with SofaScore data",
					ErrorCodeRegistry.ANALYSIS_ERROR,
					e
			);
		}
	}

	private void updateEndedMatches(List<ExBetMatchRequestDto> exBetMatchRequestDto, List<ExBetMatchResponseDto> exBetMatchResponseDtoFromDB) {
		try {
			List<Integer> requestMatchIds = exBetMatchRequestDto.stream()
					.map(ExBetMatchRequestDto::getId)
					.filter(Objects::nonNull)
					.toList();

			List<Integer> endedMatchIds = exBetMatchResponseDtoFromDB.stream()
					.map(ExBetMatchResponseDto::getId)
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
