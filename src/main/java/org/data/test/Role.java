package org.data.test;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("role")
@Builder
public class Role {
	@Id
	private String id;

	private String name;
}
