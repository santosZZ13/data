package org.data.persistent.repository;

import lombok.AllArgsConstructor;
import org.data.persistent.entity.CollectionSeq;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@AllArgsConstructor
public class CollectionSeqRepository {

	private final MongoOperations operations;

	public long next(String collection) {
		CollectionSeq next = operations.findAndModify(
				query(where("collection").is(collection)),
				new Update().inc("current", 1),
				options().returnNew(true).upsert(true),
				CollectionSeq.class
		);
		return Objects.requireNonNull(next).getCurrent();
	}
}
