package org.data.repository.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.converter.ExBetMatchConverter;
import org.data.dto.common.ex.ExBetMatchDto;
import org.data.persistent.entity.ExBetMatchEntity;
import org.data.persistent.repository.ExBetCustomRepository;
import org.data.persistent.repository.ExBetMongoRepository;
import org.data.repository.ExBetRepository;
import org.data.repository.SofaRepository;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Log4j2
public class ExBetRepositoryImpl implements ExBetRepository {

	private final ExBetMongoRepository exBetMongoRepository;
	private final ExBetCustomRepository exBetCustomRepository;
	private final SofaRepository sofaRepository;
	private final MongoTemplate mongoTemplate;

	public void saveExBetMatchDto(List<ExBetMatchDto> exBetMatchDto) {
		log.info("Starting to save {} matches from EightXBet", exBetMatchDto.size());
		if (exBetMatchDto.isEmpty()) {
			log.warn("No matches to save.");
			return;
		}

		Map<Integer, ExBetMatchDto> uniqueMatchesDto = exBetMatchDto.stream()
				.filter(matchDto -> matchDto.getId() != 0)
				.collect(Collectors.toMap(
						ExBetMatchDto::getId,
						matchDto -> matchDto,
						(existing, replacement) -> existing,
						LinkedHashMap::new
				));

		List<Integer> matchDtoIds = new ArrayList<>(uniqueMatchesDto.keySet());
		if (matchDtoIds.isEmpty()) {
			log.warn("No valid match IDs to process.");
			return;
		}

		Map<Integer, ExBetMatchEntity> entitiesMapFromDB = exBetCustomRepository.getEntitiesMap(matchDtoIds);
		List<ExBetMatchEntity> entitiesToSave = new ArrayList<>();

		for (ExBetMatchDto matchDto : uniqueMatchesDto.values()) {
			ExBetMatchEntity entityMatchFromDto = ExBetMatchConverter.toEntity(matchDto);
			ExBetMatchEntity existingEntityMatch = entitiesMapFromDB.get(matchDto.getId());
			if (existingEntityMatch == null || !existingEntityMatch.equals(entityMatchFromDto)) {
				if (existingEntityMatch != null) {
					entityMatchFromDto.setId(existingEntityMatch.getId());
				}
				entitiesToSave.add(entityMatchFromDto);
			}
		}

		if (!entitiesToSave.isEmpty()) {
			exBetCustomRepository.saveAll(entitiesToSave);
		}

		log.info("Finished saving matches. Total processed: {} at {}", entitiesToSave.size(), new Date());
	}


	@Override
	public List<ExBetMatchDto> getExBetByDate(String date) {
		if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
			log.warn("Invalid date format: {}. Expected YYYY-MM-DD.", date);
			return List.of();
		}
		ZonedDateTime startOfDay = ZonedDateTime.parse(date + "T00:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME);
		ZonedDateTime endOfDay = ZonedDateTime.parse(date + "T23:59:59Z", DateTimeFormatter.ISO_ZONED_DATE_TIME);
		return exBetMongoRepository.findAllByKickoffTimeBetween(startOfDay, endOfDay)
				.stream()
				.map(ExBetMatchConverter::toDto)
				.collect(Collectors.toList());
	}



	@Override
	public void updateStatusByIds(List<Integer> matchIds, String status) {
		if (matchIds.isEmpty()) {
			log.warn("No match IDs provided for status update.");
			return;
		}

		log.info("Updating status to {} for {} matches.", status, matchIds.size());
		BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ExBetMatchEntity.class);
		for (Integer matchId : matchIds) {
			Query query = new Query(Criteria.where("matchId").is(matchId));
			Update update = new Update().set("status", status);
			bulkOps.updateOne(query, update);
		}
		bulkOps.execute();
		log.info("Updated status for {} matches.", matchIds.size());
	}
}
