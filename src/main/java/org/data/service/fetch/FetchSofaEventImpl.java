package org.data.service.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.persistent.entity.HistoryFetchEventEntity;
import org.data.persistent.repository.HistoryFetchEventEntityRepository;
import org.data.properties.ConnectionProperties;
import org.data.sofa.dto.SofaEventsResponse;
import org.data.sofa.repository.impl.SofaEventsRepository;
import org.data.util.RestConnector;
import org.data.util.TimeUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import static org.data.sofa.dto.SofaEventsByDateDTO.SCHEDULED_EVENT_TEAM_LAST;
import static org.data.sofa.dto.SofaEventsByDateDTO.SCHEDULED_EVENT_TEAM_NEXT;

@Service
@Log4j2
@AllArgsConstructor
public class FetchSofaEventImpl implements FetchSofaEvent {

	private final HistoryFetchEventEntityRepository historyFetchEventEntityRepository;
	private final SofaEventsRepository sofaEventsRepository;
	private final RestConnector restConnector;

	private static final int BATCH_SIZE = 4; // Adjust batch size as needed
	private static final int BATCH_SIZE_FOR_FETCH = 5; // Adjust batch size as needed
	private static final long DELAY_BETWEEN_BATCHES_MS = 5000;

	@Override
	public void fetchHistoricalMatches(List<Integer> ids) {
		List<Integer> idsTest = ids.subList(0, 10);
		List<List<Integer>> batches = createBatches(idsTest, BATCH_SIZE);

		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

		for (List<Integer> batch : batches) {
			future = future.thenCompose(previous -> processBatchWithDelay(batch, executorService));
		}

		future.whenComplete(
				(result, throwable) -> executorService.shutdown()
		);
	}

	private CompletionStage<Void> processBatchWithDelay(List<Integer> batch, ScheduledExecutorService executorService) {

		CompletableFuture<Void> batchFuture = new CompletableFuture<>();

		executorService.schedule(() -> {
					log.info("----------------------------------------------------------------------------------------------------------");
					log.info("#processBatchWithDelay - [Processing] batch with size: [{}] - {} in Thread: [{}]", batch.size(), batch, Thread.currentThread().getName());
					List<CompletableFuture<Void>> futures = new ArrayList<>();

					for (Integer innerBatch : batch) {

						CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
							try {
								if (!isFetchHistoricalMatchesForId(innerBatch)) {
									fetchHistoricalMatchesForId(innerBatch);
								} else {
									log.info("Historical events for id: {} already fetched", innerBatch);
								}
							} catch (Exception e) {
								log.error("Error fetching historical event for id: {}", innerBatch, e);
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


	private List<List<Integer>> createBatches(List<Integer> ids, int batchSize) {
		log.info("#createBatches - [Creating] batches for ids with size: [{}] in Thread: [{}]", ids.size(), Thread.currentThread().getName());
		List<List<Integer>> batches = new ArrayList<>();

		for (int i = 0; i < ids.size(); i += batchSize) {
			int end = Math.min(ids.size(), i + batchSize);
			batches.add(new ArrayList<>(ids.subList(i, end)));
		}

		return batches;
	}

	/**
	 * // TODO: Check if the team with id needs to be fetched or not
	 *
	 * @param id
	 * @return
	 */
	public Boolean isFetchHistoricalMatchesForId(Integer id) {
		Optional<HistoryFetchEventEntity> byIdTeam = historyFetchEventEntityRepository.findByTeamId(id);
		if (byIdTeam.isPresent()) {
			return true;
		}
		return false;
	}


	public void fetchHistoricalMatchesForId(Integer id)  {
		// TODO:
		// Caching Strategies for APIs: https://medium.com/@satyendra.jaiswal/caching-strategies-for-apis-improving-performance-and-reducing-load-1d4bd2df2b44
		Instant start = Instant.now();
		log.info("#fetchHistoricalMatchesForId - [STARTING] fetch for [id: {}] in [Thread : {}]", id, Thread.currentThread().getName());

		int initLast = 0;
		int intNext = 0;
		boolean isLast = true;
		boolean isNext = true;

		int timesTestForLast = 0;
		int timesTestForNext = 0;

		List<SofaEventsResponse> sofaEventsRespons = new ArrayList<>();

		do {
			if (isLast) {

//				Thread.sleep(500);
//				timesTestForLast++;
//
//				if (timesTestForLast > 5) {
//					isLast = false;
//				}
				SofaEventsResponse sofaEventsResponseLast = restConnector.restGet(ConnectionProperties.Host.SOFASCORE,
						SCHEDULED_EVENT_TEAM_LAST, SofaEventsResponse.class, Arrays.asList(id, initLast));
				if (sofaEventsResponseLast.getHasNextPage()) {
					initLast++;
					sofaEventsRespons.add(sofaEventsResponseLast);
				} else {
					sofaEventsRespons.add(sofaEventsResponseLast);
					isLast = false;
				}
			}

			if (isNext) {

//				Thread.sleep(500);
//				timesTestForNext++;
//
//				if (timesTestForNext > 2) {
//					isNext = false;
//				}

				SofaEventsResponse sofaEventsResponseNext = restConnector.restGet(ConnectionProperties.Host.SOFASCORE,
						SCHEDULED_EVENT_TEAM_NEXT, SofaEventsResponse.class, Arrays.asList(id, intNext));

				if (sofaEventsResponseNext.getHasNextPage()) {
					sofaEventsRespons.add(sofaEventsResponseNext);
					intNext++;
				} else {
					sofaEventsRespons.add(sofaEventsResponseNext);
					isNext = false;
				}
			}

		} while (isLast || isNext);


		List<SofaEventsResponse.EventResponse> eventResponses = new ArrayList<>();

		// sum of events
		Optional<Integer> totalEvents = sofaEventsRespons
				.stream()
				.map(sofaScheduledEventsResponse -> {
					eventResponses.addAll(sofaScheduledEventsResponse.getEvents());
					return sofaScheduledEventsResponse;
				})
				.map(sofaScheduledEventsResponse -> sofaScheduledEventsResponse.getEvents().size())
				.reduce(Integer::sum);

		totalEvents.ifPresent(totalEvent -> log.info("#fetchHistoricalMatchesForId - [Total] events for id: {} is: {}", id, totalEvents.get()));

		saveEventBatch(eventResponses, id);
		long timeElapses = TimeUtil.calculateTimeElapsed(start, Instant.now());
		saveHistoryFetch(id, timeElapses, totalEvents.get());
		log.info("#fetchHistoricalMatchesForId - [END] time elapsed for [id: {}] in [{} ms]", id, timeElapses);

	}

	private void saveHistoryFetch(Integer id, Long timeElapses,
								  Integer totalEvents) {
		HistoryFetchEventEntity historyFetchEventEntity = HistoryFetchEventEntity
				.builder()
				.teamId(id)
				.timeElapsed(timeElapses)
				.total(totalEvents)
				.createdDate(LocalDateTime.now())
				.build();
		historyFetchEventEntityRepository.save(historyFetchEventEntity);
	}

	private void saveEventBatch(List<SofaEventsResponse.EventResponse> eventResponses, Integer idMatch) {
		log.info("#fetchHistoricalMatchesForId - saving events for [id: {}] with size: [{}]", idMatch, eventResponses.size());
		sofaEventsRepository.saveEvents(eventResponses);
		//			if (eventResponses.size() >= BATCH_SIZE) {
//				log.info("#fetchHistoricalMatchesForId - saving events for [id: {}] with size: [{}]", id, eventResponses.size());
//				scheduledEventsRepository.saveEvents(eventResponses);
//				eventResponses.clear();
//			}
//		for (int i = 0; i < eventResponses.size(); i += BATCH_SIZE) {
//			int end = Math.min(eventResponses.size(), i + BATCH_SIZE);
//			List<EventResponse> batch = eventResponses.subList(i, end);
//			log.info("#fetchHistoricalMatchesForId - saving events for [id: {}] with size: [{}]", id, batch.size());
//			scheduledEventsRepository.saveEvents(batch);
//		}
	}
}
