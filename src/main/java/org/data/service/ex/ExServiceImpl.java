package org.data.service.ex;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.data.dto.ImportMatchesJsonFile;
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
			List<ImportMatchesJsonFile.ExBetMatchDto> exBetMatchResponseDtos = convertToExBetMatchResponseDto(tournaments);
			saveToDB(exBetMatchResponseDtos);
			return ImportMatchesJsonFile.Response.builder()
					.matches(exBetMatchResponseDtos)
					.build();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	private List<ImportMatchesJsonFile.ExBetMatchDto> convertToExBetMatchResponseDto(
			List<ExBetTournamentResponse> tournaments) {

		List<ImportMatchesJsonFile.ExBetMatchDto> exBetMatchResponseDtos = new ArrayList<>();
		for (ExBetTournamentResponse tournament : tournaments) {
			String tournamentName = tournament.getName();
			for (ExBetMatchResponse match : tournament.getMatches()) {
				ImportMatchesJsonFile.ExBetMatchDto exBetMatchResponseDto = ImportMatchesJsonFile.ExBetMatchDto.builder()
						.tournamentName(tournamentName)
						.kickoffTime(TimeUtil.convertUnixTimestampToLocalDateTime(match.getKickoffTime()))
						.homeId(match.getHome().getId())
						.homeName(match.getHome().getName())
						.awayId(match.getAway().getId())
						.awayName(match.getAway().getName())
						.round(ImportMatchesJsonFile.RoundDto.builder()
								.roundName(match.getRound().getRoundName())
								.roundType(match.getRound().getRoundType())
								.build())
						.build();
				exBetMatchResponseDtos.add(exBetMatchResponseDto);
			}
		}

		return exBetMatchResponseDtos;
	}

	public void saveToDB(List<ImportMatchesJsonFile.ExBetMatchDto> exBetMatchResponseDtos) {
		exBetRepository.saveExBetMatchDto(exBetMatchResponseDtos);
	}
}
