package org.data.persistent.repository;

import org.data.persistent.entity.PredictionLotteryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PredictionLotteryRepository extends MongoRepository<PredictionLotteryEntity, Long> {
}
