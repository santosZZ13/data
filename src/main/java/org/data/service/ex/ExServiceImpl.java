package org.data.service.ex;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.dto.common.*;
import org.data.dto.ex.*;
import org.data.repository.ex.ExBetRepository;
import org.data.repository.sofa.SofaRepository;
import org.data.response.ex.ExBetResponse;
import org.data.response.ex.ExBetTournamentResponse;
import org.data.response.sf.parent.SofaMatchResponseDetailDto;
import org.data.util.LevenshteinMatcher;
import org.data.util.NormalizeTeamName;
import org.data.util.service.SofaApiService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
		if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
			throw new IllegalArgumentException("Invalid date format. Expected YYYY-MM-DD.");
		}

		List<ExBetMatchResponseDto> exBetMatchResponseFromDB = exBetRepository.getExBetByDate(date);
		List<ExBetMatchRequestDto> matchesFromRequest = request.getMatches();

		if (matchesFromRequest == null || matchesFromRequest.isEmpty()) {
			return SaveExBetMatchDto.Response.builder()
					.matches(exBetMatchResponseFromDB)
					.build();
		}

		updateEndedMatches(matchesFromRequest, exBetMatchResponseFromDB);
		exBetRepository.saveExBetMatchDto(toExBetMatchResponseDto(matchesFromRequest));

		List<ExBetMatchResponseDto> exBetMatchesByDate = exBetRepository.getExBetByDate(date);

		for (ExBetMatchResponseDto exBetMatchResponseDto : exBetMatchesByDate) {
			String normalizedHomeName = NormalizeTeamName.normalize(exBetMatchResponseDto.getHomeName());
			String normalizedAwayName = NormalizeTeamName.normalize(exBetMatchResponseDto.getAwayName());

			List<SofaMatchDto> candidates = sofaRepository.findSofaMatchByName(normalizedHomeName);
			if (candidates == null || candidates.isEmpty()) {
				candidates = sofaRepository.findSofaMatchByName(normalizedAwayName);
			}

			if (candidates == null || candidates.isEmpty()) {
				exBetMatchResponseDto.setIsMatched(false);
				exBetMatchResponseDto.setSofaData(null);
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
						exBetMatchResponseDto.getHomeName(), exBetMatchResponseDto.getAwayName(), bestMatch.getHomeTeam().getName(), bestMatch.getAwayTeam().getName());

				ExBetMatchCommonDto.SofaData sofa = ExBetMatchCommonDto.SofaData.builder()
						.sofaMatchId(bestMatch.getMatchId())
						.sofaHomeId(bestMatch.getHomeTeam().getId())
						.sofaAwayId(bestMatch.getAwayTeam().getId())
						.sofaHomeName(bestMatch.getHomeTeam().getName())
						.sofaAwayName(bestMatch.getAwayTeam().getName())
						.build();

				exBetMatchResponseDto.setSofaData(sofa);
				exBetMatchResponseDto.setIsMatched(Boolean.TRUE);
			} else {
				log.info("No SofaScore match found for 8xbet match: {} vs {}", exBetMatchResponseDto.getHomeName(), exBetMatchResponseDto.getAwayName());
				exBetMatchResponseDto.setIsMatched(false);
				exBetMatchResponseDto.setSofaData(null);
			}
		}


		return SaveExBetMatchDto.Response.builder()
				.matches(exBetMatchesByDate)
				.build();
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
