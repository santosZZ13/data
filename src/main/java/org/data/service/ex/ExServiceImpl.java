package org.data.service.ex;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.data.dto.common.ExBetMatchDto;
import org.data.dto.common.MatchedMatchesDto;
import org.data.dto.ex.*;
import org.data.dto.common.ExBetMatchResponseDto;
import org.data.repository.ex.ExBetRepository;
import org.data.response.ex.ExBetResponse;
import org.data.response.ex.ExBetTournamentResponse;
import org.data.util.utils.DateUtils;
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
	public SaveMatchExDto.Response saveMatches(SaveMatchExDto.Request request, String date) {
		// Kiểm tra date hợp lệ
		if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
			throw new IllegalArgumentException("Invalid date format. Expected YYYY-MM-DD.");
		}

		List<ExBetMatchResponseDto> exBetMatchResponseFromDB = exBetRepository.getExBetByDate(date);

		// Lấy danh sách trận đấu từ request
		List<ExBetMatchDto> matchesFromRequest = request.getMatches();
		if (matchesFromRequest == null || matchesFromRequest.isEmpty()) {
			return SaveMatchExDto.Response.builder()
					.matches(exBetMatchResponseFromDB)
					.build();
		}

		// Tìm các trận trong DB nhưng không có trong request (diff)
		Set<Integer> requestMatchIds = matchesFromRequest.stream()
				.map(ExBetMatchDto::getId)
				.collect(Collectors.toSet());

		List<Integer> endedMatchIds = exBetMatchResponseFromDB.stream()
				.map(ExBetMatchResponseDto::getId)
				.filter(id -> !requestMatchIds.contains(id))
				.toList();


		// Cập nhật status của các trận diff thành "ended"
		if (!endedMatchIds.isEmpty()) {
			exBetRepository.updateStatusByIds(endedMatchIds, "finished");
		}

		// Lưu/cập nhật các trận từ request
		exBetRepository.saveExBetMatchDto(toExBetMatchResponseDto(matchesFromRequest));

		// Lấy lại danh sách tất cả trận đấu từ DB cho ngày date để trả về
		List<ExBetMatchResponseDto> allMatches = exBetRepository.getExBetByDate(date);
		return SaveMatchExDto.Response.builder()
				.matches(allMatches)
				.build();
	}

	private List<ExBetMatchResponseDto> toExBetMatchResponseDto(List<ExBetMatchDto> exBetMatchDtos) {
		List<ExBetMatchResponseDto> exBetMatchResponseDtos = new ArrayList<>();
		exBetMatchDtos.forEach(exBetMatchDto ->  {
			ExBetMatchResponseDto build = ExBetMatchResponseDto.builder()
					.id(exBetMatchDto.getId())
					.tournamentName(exBetMatchDto.getTournamentName())
					.kickoffTime(exBetMatchDto.getKickoffTime())
					.homeId(exBetMatchDto.getHomeId())
					.homeName(exBetMatchDto.getHomeName())
					.awayId(exBetMatchDto.getAwayId())
					.awayName(exBetMatchDto.getAwayName())
					.status("notstarted")
					.round(exBetMatchDto.getRound())
					.isMatched(false)
					.sofaData(null)
					.build();
			exBetMatchResponseDtos.add(build);
		});

		return exBetMatchResponseDtos;
	}
}
