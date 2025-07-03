package org.data.persistent.repository;

import org.data.persistent.entity.LotteryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LotteryRepositoryMongoRepository extends MongoRepository<LotteryEntity, Integer> {

}
