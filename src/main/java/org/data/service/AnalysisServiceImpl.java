package org.data.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.dto.MatchDto;
import org.data.dto.TeamDto;
import org.data.dto.TeamStats;
import org.data.dto.analysis.GetAnalysisDto;
import org.data.dto.sf.SfEventsResponse;
import org.data.properties.ConnectionProperties;
import org.data.repository.SofaRepository;
import org.data.repository.TeamRepository;
import org.data.response.MatchResponse;
import org.data.response.sf.EventChildResponse;
import org.data.response.sf.EventsResponse;
import org.data.util.RestConnector;
import org.data.util.TimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.data.dto.sf.GetSofaEventsByDateDto.SCHEDULED_EVENT_TEAM_LAST;
import static org.data.dto.sf.GetSofaEventsByDateDto.SCHEDULED_EVENT_TEAM_NEXT;


@Service
@AllArgsConstructor
@Log4j2
public class AnalysisServiceImpl implements AnalysisService {

	private final RestTemplate restTemplate;
	private final SofaRepository sofaRepository;
	private final TeamRepository teamRepository;

	private static final int BATCH_SIZE = 30;
	private static final long DELAY_BETWEEN_BATCHES_MS = 5000;


//	@Value("${sofascore.api.base-url}")
	private final String baseUrl = "";

	private final RestConnector restConnector;


	@Override
	public GetAnalysisDto.Response getAnalysisByDate(Date date) {
		// Call API lấy danh sách trận đấu ngày hôm đó
		String url = baseUrl + "/sport/football/scheduled-events/" + date;
		String urlInverse = baseUrl + "/sport/football/scheduled-events/" + date;
		// Get all matches on 03/01/2025,...
		EventsResponse response = restTemplate.getForObject(url, EventsResponse.class);

		List<EventChildResponse> matches = List.of();

		if (response != null && response.getEvents() != null) {
			matches = response.getEvents();

			Set<Integer> ids = matches.stream()
					.flatMap(match -> Stream.of(match.getHomeTeam().getId(), match.getAwayTeam().getId()))
					.collect(Collectors.toSet());
			fetchData(ids);
		}


		// Lấy ra danh sách teamIds
		List<List<Integer>> matchesIds = matches.stream()
				.map(match -> List.of(match.getHomeTeam().getId(), match.getAwayTeam().getId()))
				.collect(Collectors.toList());

		// Tính toán dữ liệu
		List<GetAnalysisDto.AnalysisResponse> analysisResponses = new ArrayList<>();

		for (List<Integer> matchId : matchesIds) {
			// Lấy lịch sử trận đấu của teamId
			Integer homeId = matchId.get(0);
			Integer awayId = matchId.get(1);
			List<MatchDto> historyMatchesByHomeId = sofaRepository.getMatchesDto(homeId, homeId);
			List<MatchDto> historyMatchesByAwayId = sofaRepository.getMatchesDto(awayId, awayId);

			TeamStats homeStats = getAnalysisResponse(homeId, historyMatchesByHomeId);
			TeamStats awayStats = getAnalysisResponse(awayId, historyMatchesByAwayId);

			double over15Index = (homeStats.getMetrics().getOver15Period1().getPercentage() +
					awayStats.getMetrics().getOver15Period1().getPercentage()) * 0.5 +
					(homeStats.getMetrics().getGoalsScored().getAvgPeriod1() +
							awayStats.getMetrics().getGoalsConceded().getAvgPeriod1() +
							awayStats.getMetrics().getGoalsScored().getAvgPeriod1() +
							homeStats.getMetrics().getGoalsConceded().getAvgPeriod1()) * 0.5;

			double over05Index = (homeStats.getMetrics().getOver05Period1().getPercentage() + awayStats.getMetrics().getOver05Period1().getPercentage()) * 0.5 +
					(homeStats.getMetrics().getGoalsScored().getAvgPeriod1() + awayStats.getMetrics().getGoalsConceded().getAvgPeriod1() +
							awayStats.getMetrics().getGoalsScored().getAvgPeriod1() + homeStats.getMetrics().getGoalsConceded().getAvgPeriod1()) * 0.5;

			GetAnalysisDto.AnalysisResponse analysisResponse = GetAnalysisDto.AnalysisResponse.builder()
					.home(null)
					.away(null)
					.over15Index(over15Index)
					.over05Index(over05Index)
					.build();

			analysisResponses.add(analysisResponse);
		}

		GetAnalysisDto.ResponseData responseData = GetAnalysisDto.ResponseData.builder()
				.total(analysisResponses.size())
				.analysis(analysisResponses)
				.build();

		return GetAnalysisDto.Response.builder()
				.data(responseData)
				.build();
	}

	private TeamStats getAnalysisResponse(Integer teamId, List<MatchDto> historyMatchesByTeamId) {
		int wins = 0;
		int losses = 0;
		int draws = 0;
		int goalsScoredP1 = 0, goalsConcededP1 = 0;
		int over15Count = 0, over05Count = 0;

		for (MatchDto match : historyMatchesByTeamId) {
			boolean isHome = match.getTeams().getHome().getTeamId().equals(teamId);
			int homeP1 = match.getScore().getHome().getPeriod1();
			int awayP1 = match.getScore().getAway().getPeriod1();

			if (isHome) {
				goalsScoredP1 += homeP1;
				goalsConcededP1 += awayP1;

				if (match.getScore().getHome().getNormaltime() > match.getScore().getAway().getNormaltime()) {
					wins++;
				} else if (match.getScore().getHome().getNormaltime() < match.getScore().getAway().getNormaltime()) {
					losses++;
				} else {
					draws++;
				}

			} else {
				goalsScoredP1 += awayP1;
				goalsConcededP1 += homeP1;

				if (match.getScore().getHome().getNormaltime() < match.getScore().getAway().getNormaltime()) {
					wins++;
				} else if (match.getScore().getHome().getNormaltime() > match.getScore().getAway().getNormaltime()) {
					losses++;
				} else {
					draws++;
				}
			}
			if (homeP1 + awayP1 >= 2) over15Count++;
			if (homeP1 + awayP1 >= 1) over05Count++;
		}


		TeamStats.Metrics metrics = TeamStats.Metrics.builder()
				.matchesPlayed(historyMatchesByTeamId.size())
				.wins(wins)
				.losses(losses)
				.draws(draws)
				.goalsScored(TeamStats.Goals.builder()
						.period1(goalsScoredP1)
						.avgPeriod1(goalsScoredP1 / historyMatchesByTeamId.size())
						.build())
				.goalsConceded(TeamStats.Goals.builder()
						.period1(goalsConcededP1)
						.avgPeriod1(goalsConcededP1 / historyMatchesByTeamId.size())
						.build())
				.over15Period1(TeamStats.Over15.builder()
						.count(over15Count)
						.percentage((double) over15Count / historyMatchesByTeamId.size())
						.build())
				.over05Period1(TeamStats.Over05.builder()
						.count(over05Count)
						.percentage((double) over05Count / historyMatchesByTeamId.size())
						.build())
				.build();

		TeamStats teamStats = TeamStats.builder()
				.teamId(teamId)
				.metrics(metrics)
				.build();


		return teamStats;
	}

	private GetAnalysisDto.AnalysisResponse getAnalysisResponse(TeamStats teamStats) {
		return null;
	}

	// check if any team in the match is needed fetching data in the database
	// if yes, call API /{teamId} to get the history of the team
	public void fetchData(Set<Integer> ids) {

		List<Integer> idsForFetch = new ArrayList<>();

		for (Integer teamId : ids) {
			boolean isNeeded = checkIfNeedFetch(teamId);
			if (isNeeded) {
				// call API to get the history of the team
				idsForFetch.add(teamId);
			}
		}

		if (idsForFetch.isEmpty()) {
			log.info("#fetchData - No team ids to fetch");
			return;
		}

		List<List<Integer>> batches = createBatches(idsForFetch, BATCH_SIZE);
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
		for (List<Integer> batch : batches) {
			future = future.thenCompose(previous -> processBatchWithDelay(batch, executorService));
		}

		// Đợi tất cả batch hoàn thành
		future.join();
		executorService.shutdown();
	}

	// write a method to check if the team is needed fetching data in the database
	private boolean checkIfNeedFetch(Integer teamId) {
		// The first time, we need to fetch all data
		TeamDto teamById = teamRepository.getTeamById(teamId);
		if (teamById == null) {
			return true; // Đội chưa có trong database, cần fetch toàn bộ
		}

		TeamDto.FetchStatus fetchStatus = teamById.getFetchStatus();
		if (fetchStatus == null || !fetchStatus.getIsFullyFetched()) {
			return true; // Chưa fetch toàn bộ, cần fetch
		}


		return false;
	}


	private CompletionStage<Void> processBatchWithDelay(List<Integer> batch, ScheduledExecutorService executorService) {

		CompletableFuture<Void> batchFuture = new CompletableFuture<>();

		executorService.schedule(() -> {
					log.info("----------------------------------------------------------------------------------------------------------");
					log.info("#processBatchWithDelay - [Processing] batch with size: [{}] - {} in Thread: [{}]", batch.size(), batch, Thread.currentThread().getName());
					List<CompletableFuture<Void>> futures = new ArrayList<>();

					for (Integer id : batch) {
						CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
							try {
								//TODO
								// Need to check if needed to fetch all or not.
								// For now, fetching all
								fetchHistoricalMatchesForId(id);
							} catch (Exception e) {
								log.error("Error fetching historical event for id: {}", id, e);
							}
						});
						futures.add(voidCompletableFuture);
					}

					CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).whenComplete((res, ex) -> {
						log.info("----------------------------------------------------------------------------------------------------------");
						batchFuture.complete(null);
					});
				},
				DELAY_BETWEEN_BATCHES_MS,
				TimeUnit.MILLISECONDS);


		return batchFuture;
	}


	public void fetchHistoricalMatchesForId(Integer id) {
		// TODO:
		// Caching Strategies for APIs: https://medium.com/@satyendra.jaiswal/caching-strategies-for-apis-improving-performance-and-reducing-load-1d4bd2df2b44
		Instant start = Instant.now();
		log.info("#fetchHistoricalMatchesForId - [STARTING] fetch for [id: {}] in [Thread : {}]", id, Thread.currentThread().getName());


		TeamDto team = teamRepository.getTeamById(id);
		boolean isFullFetch = team == null || team.getFetchStatus() == null || !team.getFetchStatus().getIsFullyFetched();

		TeamDto.FetchStatus fetchStatus = (team != null && team.getFetchStatus() != null) ? team.getFetchStatus() : new TeamDto.FetchStatus();

		Long lastMatchTimestamp = fetchStatus.getLastMatchTimestamp() != null ? fetchStatus.getLastMatchTimestamp() : 0L;
		Long futureMatchTimestamp = fetchStatus.getFutureMatchTimestamp() != null ? fetchStatus.getFutureMatchTimestamp() : 0L;

		List<SfEventsResponse.EventResponse> allEvents = new ArrayList<>();

		if (isFullFetch) {
			// Fetch toàn bộ lịch sử
			int initLast = 0;
			boolean hasMoreLast = true;
			int initNext = 0;
			boolean hasMoreNext = true;

			// Fetch các trận đã diễn ra
			while (hasMoreLast) {
				try {
					SfEventsResponse response = restConnector.restGet(
							ConnectionProperties.Host.SOFASCORE,
							"/team/{}/events/last/{}",
							SfEventsResponse.class,
							Arrays.asList(id, initLast)
					);
					if (response != null && response.getEvents() != null) {
						allEvents.addAll(response.getEvents());
						hasMoreLast = response.getHasNextPage();
						initLast++;
					} else {
						hasMoreLast = false;
					}
				} catch (Exception e) {
					log.error("Error fetching historical event (last) for id: {} {}", id, e.getMessage());
					hasMoreLast = false;
				}
			}


			// Fetch các trận tương lai
			while (hasMoreNext) {
				try {
					SfEventsResponse response = restConnector.restGet(
							ConnectionProperties.Host.SOFASCORE,
							SCHEDULED_EVENT_TEAM_NEXT,
							SfEventsResponse.class,
							Arrays.asList(id, initNext)
					);
					if (response != null && response.getEvents() != null) {
						allEvents.addAll(response.getEvents());
						hasMoreNext = response.getHasNextPage();
						initNext++;
					} else {
						hasMoreNext = false;
					}
				} catch (Exception e) {
					log.error("Error fetching historical event (next) for id: {} {}", id, e.getMessage());
					hasMoreNext = false;
				}
			}

			// Cập nhật fetchStatus
			fetchStatus.setIsFullyFetched(true);
			fetchStatus.setLastFetchedDate(new Date());
			if (!allEvents.isEmpty()) {
				lastMatchTimestamp = allEvents.stream()
						.filter(e -> e.getStatus().getType().equals("finished"))
						.mapToLong(SfEventsResponse.EventResponse::getStartTimestamp)
						.max()
						.orElse(lastMatchTimestamp);
				futureMatchTimestamp = allEvents.stream()
						.mapToLong(SfEventsResponse.EventResponse::getStartTimestamp)
						.max()
						.orElse(futureMatchTimestamp);
			}
		} else {
			// TODO: Fetch cập nhật: Chỉ lấy các trận cần cập nhật hoặc trận mới
			List<MatchDto> pendingMatches = sofaRepository.getMatchesWithPendingStatus(id, lastMatchTimestamp, futureMatchTimestamp);
		}

		if (!allEvents.isEmpty()) {
			List<MatchDto> matchesToSave = allEvents.stream()
					.filter(event -> sofaRepository.findMatchByMatchId(event.getId()) == null) // Tránh trùng lặp
					.map(this::convertToMatchDto)
					.collect(Collectors.toList());
			sofaRepository.saveMatches(matchesToSave);

			// Cập nhật fetchStatus cho team
			fetchStatus.setLastMatchTimestamp(lastMatchTimestamp);
			fetchStatus.setFutureMatchTimestamp(futureMatchTimestamp);
			team.setFetchStatus(fetchStatus);
			teamRepository.saveTeam(team);

			// Lưu thông tin đội nếu chưa có
//			allEvents.forEach(event -> {
//				saveTeamIfNotExists(event.getHomeTeam());
//				saveTeamIfNotExists(event.getAwayTeam());
//			});
		}

	}

	private List<List<Integer>> createBatches(List<Integer> ids, int batchSize) {
		log.info("#createBatches - [Creating] batches for ids with size: [{}] in Thread: [{}]", ids.size(), Thread.currentThread().getName());
		List<List<Integer>> batches = new ArrayList<>();

		for (int i = 0; i < ids.size(); i += batchSize) {
			int end = Math.min(ids.size(), i + batchSize);
			batches.add(new ArrayList<>(ids.subList(i, end)));
		}

		return batches;
	}

	private MatchDto convertToMatchDto(SfEventsResponse.EventResponse event) {
		return null;
//		MatchDto match = new MatchDto();
//		match.setMatchId(event.getId());
//		match.setDate(new Date(event.getStartTimestamp() * 1000L));
//
//		MatchDto.Teams teams = new MatchDto.Teams();
//		MatchDto.TeamDetails home = new MatchDto.TeamDetails();
//		home.setTeamId(event.getHomeTeam().getId());
//		home.setName(event.getHomeTeam().getName());
//		home.setShortName(event.getHomeTeam().getShortName());
//		MatchDto.TeamDetails away = new MatchDto.TeamDetails();
//		away.setTeamId(event.getAwayTeam().getId());
//		away.setName(event.getAwayTeam().getName());
//		away.setShortName(event.getAwayTeam().getShortName());
//		teams.setHome(home);
//		teams.setAway(away);
//		match.setTeams(teams);
//
//		MatchDto.Tournament tournament = new MatchDto.Tournament();
//		tournament.setId(event.getTournament().getId() != null ? event.getTournament().getId() : event.getTournament().getUniqueTournament().getId());
//		tournament.setName(event.getTournament().getName());
//		tournament.setSlug(event.getTournament().getSlug());
//		match.setTournament(tournament);
//
//		MatchDto.Status status = new MatchDto.Status();
//		status.setCode(event.getStatus().getCode());
//		status.setDescription(event.getStatus().getDescription());
//		status.setType(event.getStatus().getType());
//		match.setStatus(status);
//
//		MatchDto.Score score = new MatchDto.Score();
//		MatchDto.PeriodScore homeScore = new MatchDto.PeriodScore();
//		homeScore.setPeriod1(event.getHomeScore().getPeriod1());
//		homeScore.setPeriod2(event.getHomeScore().getPeriod2());
//		homeScore.setNormaltime(event.getHomeScore().getNormaltime());
//		MatchDto.PeriodScore awayScore = new MatchDto.PeriodScore();
//		awayScore.setPeriod1(event.getAwayScore().getPeriod1());
//		awayScore.setPeriod2(event.getAwayScore().getPeriod2());
//		awayScore.setNormaltime(event.getAwayScore().getNormaltime());
//		score.setHome(homeScore);
//		score.setAway(awayScore);
//		match.setScore(score);
//
//		MatchDto.Time time = new MatchDto.Time();
//		time.setStartTimestamp(event.getStartTimestamp());
//		time.setInjuryTime1(event.getTime() != null ? event.getTime().getInjuryTime1() : null);
//		time.setInjuryTime2(event.getTime() != null ? event.getTime().getInjuryTime2() : null);
//		match.setTime(time);
//
//		match.setUpdatedAt(new Date());
//		return match;
	}

	private void saveTeamIfNotExists(SfEventsResponse.EventResponse team) {
//		if (sofaRepository.findTeamByTeamId(team.getId()) != null) {
//			return;
//		}
//
//		Team teamEntity = new Team();
//		teamEntity.setTeamId(team.getId());
//		teamEntity.setName(team.getName());
//		teamEntity.setShortName(team.getShortName());
//		Team.Country country = new Team.Country();
//		country.setAlpha2(team.getCountry().getAlpha2());
//		country.setName(team.getCountry().getName());
//		teamEntity.setCountry(country);
//		teamEntity.setFetchStatus(new Team.FetchStatus());
//		teamEntity.setUpdatedAt(new Date());
//		sofaRepository.saveTeam(teamEntity);
	}
}
