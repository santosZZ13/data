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
import org.data.util.utils.DateUtils;
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
			sofaRepository.getMatchesByDate(date);
			List<MatchedMatchesDto> matchedMatchesDto = new ArrayList<>();
			List<ExBetMatchDto> matchesFromDB = exBetRepository.getExBetByDate(date);
			List<ExBetMatchRequestDto> matchesFromRequest = request.getMatches();

			if (matchesFromRequest == null || matchesFromRequest.isEmpty()) {
				matchedMatchesDto = matchingMatches(matchesFromDB);
				return SaveExBetMatchDto.Response.builder()
						.matches(matchedMatchesDto)
						.build();
			}
			updateEndedMatches(matchesFromRequest, matchesFromDB);
			exBetRepository.saveExBetMatchDto(toExBetMatchResponseDto(matchesFromRequest));
			List<ExBetMatchDto> exBetMatchesByDate = exBetRepository.getExBetByDate(date);
			if (exBetMatchesByDate == null) {
				exBetMatchesByDate = List.of();
			}
			matchedMatchesDto = matchingMatches(exBetMatchesByDate);
			return SaveExBetMatchDto.Response.builder()
					.matches(matchedMatchesDto)
					.build();
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
			List<MatchedMatchesDto> matches = request.getMatches();
			List<GetAnalystDto.MatchAnalysisDto> analyzedMatches = new ArrayList<>();
			Set<Integer> teamIds = getIds(matches);
			Map<Integer, List<SofaMatchResponseDetail>> teamHistories = sofaApiService.getHistoriesByTeamIds(teamIds);

			for (MatchedMatchesDto match : matches) {
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
														  MatchedMatchesDto match,
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


	private Set<Integer> getIds(List<MatchedMatchesDto> matches) {
		return matches.stream()
				.flatMap(match -> Stream.of(match.getSofaData().getSofaHomeId(), match.getSofaData().getSofaAwayId()))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}


	private List<MatchedMatchesDto> matchingMatches(List<ExBetMatchDto> exBetMatchDto) {
		try {
			if (exBetMatchDto == null || exBetMatchDto.isEmpty()) {
				log.info("No matches to match with SofaScore data");
				return List.of();
			}

			List<MatchedMatchesDto> result = new ArrayList<>();
			for (ExBetMatchDto dto : exBetMatchDto) {
				if (dto.getHomeName() == null || dto.getAwayName() == null) {
					log.warn("Invalid match data: homeName or awayName is null for match ID {}", dto.getId());
					continue;
				}

				MatchedMatchesDto matchedDto = MatchedMatchesDto.builder()
						.id(dto.getId())
						.tournamentName(dto.getTournamentName())
//						.kickoffTime(DateUtils.toUtcZonedDateTime(dto.getKickoffTime()))
						.homeId(dto.getHomeId())
						.homeName(dto.getHomeName())
						.awayId(dto.getAwayId())
						.awayName(dto.getAwayName())
						.status(dto.getStatus())
						.round(dto.getRound())
						.build();

				String normalizedHomeName = NormalizeTeamName.normalize(dto.getHomeName());
				String normalizedAwayName = NormalizeTeamName.normalize(dto.getAwayName());

				List<SofaMatchDto> candidates = sofaRepository.findSofaMatchByName(normalizedHomeName);
				if (candidates == null || candidates.isEmpty()) {
					candidates = sofaRepository.findSofaMatchByName(normalizedAwayName);
				}

				if (candidates == null || candidates.isEmpty()) {
					log.info("No SofaScore candidates found for match: {} vs {}", dto.getHomeName(), dto.getAwayName());
					matchedDto.setIsMatched(false);
					matchedDto.setSofaData(null);
					result.add(matchedDto);
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
					log.info("Found SofaScore match for match: {} vs {} with SofaScore match: {} vs {}",
							dto.getHomeName(), dto.getAwayName(), bestMatch.getHomeTeam().getName(), bestMatch.getAwayTeam().getName());

					ExBetMatchCommonDto.SofaData sofa = ExBetMatchCommonDto.SofaData.builder()
							.sofaMatchId(bestMatch.getMatchId())
							.sofaHomeId(bestMatch.getHomeTeam().getId())
							.sofaAwayId(bestMatch.getAwayTeam().getId())
							.sofaHomeName(bestMatch.getHomeTeam().getName())
							.sofaAwayName(bestMatch.getAwayTeam().getName())
							.build();

					matchedDto.setSofaData(sofa);
					matchedDto.setIsMatched(true);
				} else {
					log.info("No SofaScore match found for match: {} vs {}", dto.getHomeName(), dto.getAwayName());
					matchedDto.setIsMatched(false);
					matchedDto.setSofaData(null);
				}
				result.add(matchedDto);
			}
			return result;
		} catch (Exception e) {
			log.error("Failed to match matches with SofaScore data: {}", e.getMessage(), e);
			throw new AnalysisProcessingException(
					"Failed to match matches with SofaScore data",
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


	private List<ExBetMatchDto> toExBetMatchResponseDto(List<ExBetMatchRequestDto> exBetMatchesRequestDto) {
		List<ExBetMatchDto> exBetMatchesResponseDto = new ArrayList<>();
		exBetMatchesRequestDto.forEach(exBetMatchRequestDto -> {
			ExBetMatchDto build = ExBetMatchDto.builder()
					.id(exBetMatchRequestDto.getId())
					.tournamentName(exBetMatchRequestDto.getTournamentName())
					.kickoffTime(DateUtils.toUtcZonedDateTime(exBetMatchRequestDto.getKickoffTime()))
					.homeId(exBetMatchRequestDto.getHomeId())
					.homeName(exBetMatchRequestDto.getHomeName())
					.awayId(exBetMatchRequestDto.getAwayId())
					.awayName(exBetMatchRequestDto.getAwayName())
					.status("notstarted")
					.round(exBetMatchRequestDto.getRound())
					.build();
			exBetMatchesResponseDto.add(build);
		});

		return exBetMatchesResponseDto;
	}
}
