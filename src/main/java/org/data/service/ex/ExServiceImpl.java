package org.data.service.ex;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.data.dto.common.MatchedMatchesDto;
import org.data.dto.ex.*;
import org.data.dto.common.ExBetMatchDto;
import org.data.repository.ex.ExBetRepository;
import org.data.response.ex.ExBetMatchResponse;
import org.data.response.ex.ExBetResponse;
import org.data.response.ex.ExBetTournamentResponse;
import org.data.util.TimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
			List<ExBetMatchDto> exBetMatchResponseDtos = convertToExBetMatchResponseDto(tournaments);
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
		List<GetMatchesExByDateDto.ExBetMatchDto> exBetByDate = exBetRepository.getExBetByDate(date, isFavorite);
		return GetMatchesExByDateDto.Response.builder()
				.matches(exBetByDate)
				.build();
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
		List<ExBetMatchDto> exBetMatchResponseDtos = new ArrayList<>();
		for (ExBetTournamentResponse tournament : tournaments) {
			String tournamentName = tournament.getName();
			for (ExBetMatchResponse match : tournament.getMatches()) {
				ExBetMatchDto exBetMatchResponseDto = ExBetMatchDto.builder()
						.id(match.getIid())
						.tournamentName(tournamentName)
						.kickoffTime(TimeUtil.convertUnixTimestampToLocalDateTime(match.getKickoffTime()).toString())
						.homeId(match.getHome().getId())
						.homeName(match.getHome().getName())
						.awayId(match.getAway().getId())
						.awayName(match.getAway().getName())
						.round(ExBetMatchDto.RoundDto.builder()
								.roundName(match.getRound().getRoundName())
								.roundType(match.getRound().getRoundType())
								.build())
						.build();
				exBetMatchResponseDtos.add(exBetMatchResponseDto);
			}
		}

		return exBetMatchResponseDtos;
	}

	@Override
	public MatchWithSofaDto.Response getMatchesWithSofa(MatchWithSofaDto.Request request) {
		List<MatchedMatchesDto> result = new ArrayList<>();
		for (ExBetMatchDto exBetMatchDto : request.getMatches()) {
			MatchedMatchesDto matchedMatch = exBetRepository.getMatchedMatch(exBetMatchDto);
			result.add(matchedMatch);
		}
		return MatchWithSofaDto.Response.builder()
				.matches(result)
				.build();
	}

	public int saveToDB(List<ExBetMatchDto> exBetMatchResponseDtos) {
		return exBetRepository.saveExBetMatchDto(exBetMatchResponseDtos);
	}

	@Override
	public SaveMatchExDto.Response saveMatches(SaveMatchExDto.Request request) {
		exBetRepository.saveExBetMatchDto(request.getMatches());
		return SaveMatchExDto.Response.builder()
				.message("Matches saved successfully")
				.totalMatches(request.getMatches().size())
				.build();
	}
}
