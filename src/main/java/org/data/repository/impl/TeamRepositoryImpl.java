package org.data.repository.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.dto.common.sofa.TeamDto;
import org.data.persistent.entity.TeamEntity;
import org.data.persistent.repository.TeamMongoRepository;
import org.data.repository.TeamRepository;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@AllArgsConstructor
@Log4j2
public class TeamRepositoryImpl implements TeamRepository {

	private final TeamMongoRepository teamMongoRepository;
	private final MongoTemplate mongoTemplate;

	@Override
	public void saveTeam(TeamDto teamDto) {
		if (teamDto == null || (teamDto.getSofaTeamDto() == null && teamDto.getExBetTeamDto() == null)) {
			log.warn("TeamDto is null or has no data to save.");
			return;
		}

		Integer sofaTeamId = teamDto.getSofaTeamDto() != null ? teamDto.getSofaTeamDto().getTeamId() : null;
		Integer exBetTeamId = teamDto.getExBetTeamDto() != null ? teamDto.getExBetTeamDto().getTeamId() : null;

		if (sofaTeamId == null && exBetTeamId == null) {
			log.warn("Both SofaTeamDto and ExBetTeamDto have null teamId, skipping.");
			return;
		}

		// Tạo Query để upsert dựa trên sofaTeamInfo.teamId hoặc exBetTeamInfo.teamId
		Query query;
		if (sofaTeamId != null && exBetTeamId != null) {
			query = new Query(new Criteria().orOperator(
					Criteria.where("sofaTeamInfo.teamId").is(sofaTeamId),
					Criteria.where("exBetTeamInfo.teamId").is(exBetTeamId)
			));
		} else if (sofaTeamId != null) {
			query = new Query(Criteria.where("sofaTeamInfo.teamId").is(sofaTeamId));
		} else {
			query = new Query(Criteria.where("exBetTeamInfo.teamId").is(exBetTeamId));
		}

		// Tạo Update object
		Update update = new Update();
		if (teamDto.getSofaTeamDto() != null) {
			TeamEntity.SofaTeamEntity sofaTeamEntity = TeamEntity.SofaTeamEntity.builder()
					.teamId(sofaTeamId)
					.name(teamDto.getSofaTeamDto().getName())
					.country(teamDto.getSofaTeamDto().getCountry())
					.normalizedName(teamDto.getSofaTeamDto().getNormalizedName())
					.shortName(teamDto.getSofaTeamDto().getShortName())
					.build();
			update.set("sofaTeamInfo", sofaTeamEntity);
		}
		if (teamDto.getExBetTeamDto() != null) {
			TeamEntity.ExBetTeamEntity exBetTeamEntity = TeamEntity.ExBetTeamEntity.builder()
					.teamId(exBetTeamId)
					.name(teamDto.getExBetTeamDto().getName())
					.normalizedName(teamDto.getExBetTeamDto().getNormalizedName())
					.build();
			update.set("exBetTeamInfo", exBetTeamEntity);
		}

		// Thực hiện upsert
		try {
			mongoTemplate.upsert(query, update, TeamEntity.class);
			log.debug("Successfully saved or updated team with sofaTeamId: {}, exBetTeamId: {}", sofaTeamId, exBetTeamId);
		} catch (Exception e) {
			log.error("Error saving team with sofaTeamId: {}, exBetTeamId: {}. Error: {}", sofaTeamId, exBetTeamId, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void saveTeamsFromSofa(List<TeamDto> teamDtos) {
		if (teamDtos == null || teamDtos.isEmpty()) {
			log.info("No teams to save from SofaScore.");
			return;
		}

		// Chuẩn bị danh sách các đội để upsert
		List<TeamDto> teamsToSave = new ArrayList<>();
		for (TeamDto teamDto : teamDtos) {
			if (teamDto.getSofaTeamDto() == null || teamDto.getSofaTeamDto().getTeamId() == null) {
				continue;
			}
			teamsToSave.add(teamDto);
		}

		// Sử dụng Bulk Write Operations để lưu dữ liệu
		if (!teamsToSave.isEmpty()) {
			long startTime = System.currentTimeMillis();
			log.info("Starting to save {} teams from SofaScore at {}", teamsToSave.size(), new Date());
			int batchSize = 500;
			for (int i = 0; i < teamsToSave.size(); i += batchSize) {
				List<TeamDto> batch = teamsToSave.subList(i, Math.min(i + batchSize, teamsToSave.size()));
				BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, TeamEntity.class);

				for (TeamDto teamDto : batch) {
					Integer teamId = teamDto.getSofaTeamDto().getTeamId();
					TeamEntity.SofaTeamEntity sofaTeamEntity = TeamEntity.SofaTeamEntity.builder()
							.teamId(teamId)
							.name(teamDto.getSofaTeamDto().getName())
							.country(teamDto.getSofaTeamDto().getCountry())
							.normalizedName(teamDto.getSofaTeamDto().getNormalizedName())
							.shortName(teamDto.getSofaTeamDto().getShortName())
							.build();

					Query upsertQuery = new Query(Criteria.where("sofaTeamInfo.teamId").is(teamId));
					Update update = new Update()
							.set("sofaTeamInfo", sofaTeamEntity)
							.setOnInsert("exBetTeamInfo", null); // chỉ áp dụng khi tạo mới tài liệu, không ảnh hưởng đến các tài liệu đã tồn tại.
					bulkOps.upsert(upsertQuery, update);
				}

				try {
					bulkOps.execute();
					log.info("Saved batch of {} teams from SofaScore (total processed: {}).", batch.size(), Math.min(i + batchSize, teamsToSave.size()));
				} catch (Exception e) {
					log.error("Error saving batch of teams from SofaScore: {}", e.getMessage(), e);
					throw e;
				}
			}

			log.info("Finished saving {} teams from SofaScore in {} ms at {}", teamsToSave.size(), System.currentTimeMillis() - startTime, new Date());
		}
	}

	public void saveTeamsFromExBet(List<TeamDto> teamDtos) {
		if (teamDtos == null || teamDtos.isEmpty()) {
			log.info("No teams to save from ExBet.");
			return;
		}

		List<TeamDto> teamsToSave = new ArrayList<>();
		for (TeamDto teamDto : teamDtos) {
			if (teamDto.getExBetTeamDto() == null || teamDto.getExBetTeamDto().getTeamId() == null) {
				continue;
			}
			teamsToSave.add(teamDto);
		}

		if (!teamsToSave.isEmpty()) {
			long startTime = System.currentTimeMillis();
			log.info("Starting to save {} teams from ExBet at {}", teamsToSave.size(), new Date());

			int batchSize = 500;
			for (int i = 0; i < teamsToSave.size(); i += batchSize) {
				List<TeamDto> batch = teamsToSave.subList(i, Math.min(i + batchSize, teamsToSave.size()));
				BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, TeamEntity.class);

				for (TeamDto teamDto : batch) {
					Integer teamId = teamDto.getExBetTeamDto().getTeamId();
					TeamEntity.ExBetTeamEntity exBetTeamEntity = TeamEntity.ExBetTeamEntity.builder()
							.teamId(teamId)
							.name(teamDto.getExBetTeamDto().getName())
							.normalizedName(teamDto.getExBetTeamDto().getNormalizedName())
							.build();

					Query upsertQuery = new Query(Criteria.where("exBetTeamInfo.teamId").is(teamId));
					Update update = new Update()
							.set("exBetTeamInfo", exBetTeamEntity)
							.setOnInsert("sofaTeamInfo", null);
					bulkOps.upsert(upsertQuery, update);
				}

				try {
					bulkOps.execute();
					log.info("Saved batch of {} teams from ExBet (total processed: {}).", batch.size(), Math.min(i + batchSize, teamsToSave.size()));
				} catch (Exception e) {
					log.error("Error saving batch of teams from ExBet: {}", e.getMessage(), e);
					throw e;
				}
			}

			log.info("Finished saving {} teams from ExBet in {} ms at {}", teamsToSave.size(), System.currentTimeMillis() - startTime, new Date());
		}
	}

	public void deleteTeam(Integer sofaTeamId, Integer exBetTeamId) {
		if (sofaTeamId == null && exBetTeamId == null) {
			log.warn("Both sofaTeamId and exBetTeamId are null, cannot delete.");
			return;
		}

		Query query;
		if (sofaTeamId != null && exBetTeamId != null) {
			query = new Query(new Criteria().orOperator(
					Criteria.where("sofaTeamInfo.teamId").is(sofaTeamId),
					Criteria.where("exBetTeamInfo.teamId").is(exBetTeamId)
			));
		} else if (sofaTeamId != null) {
			query = new Query(Criteria.where("sofaTeamInfo.teamId").is(sofaTeamId));
		} else {
			query = new Query(Criteria.where("exBetTeamInfo.teamId").is(exBetTeamId));
		}

		try {
			mongoTemplate.remove(query, TeamEntity.class);
			log.info("Deleted team with sofaTeamId: {}, exBetTeamId: {}", sofaTeamId, exBetTeamId);
		} catch (Exception e) {
			log.error("Error deleting team with sofaTeamId: {}, exBetTeamId: {}. Error: {}", sofaTeamId, exBetTeamId, e.getMessage(), e);
			throw e;
		}
	}
}