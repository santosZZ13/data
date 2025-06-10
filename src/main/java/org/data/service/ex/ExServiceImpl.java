package org.data.service.ex;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.data.dto.common.ExBetMatchRequestDto;
import org.data.dto.common.MatchedMatchesDto;
import org.data.dto.ex.*;
import org.data.dto.common.ExBetMatchResponseDto;
import org.data.repository.ex.ExBetRepository;
import org.data.response.ex.ExBetResponse;
import org.data.response.ex.ExBetTournamentResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExServiceImpl implements ExService {

	private final ExBetRepository exBetRepository;

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

		List<ExBetMatchResponseDto> allMatches = exBetRepository.getExBetByDate(date);
		return SaveExBetMatchDto.Response.builder()
				.matches(allMatches)
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
