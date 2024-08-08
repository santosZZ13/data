package org.data.test;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("Student")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student implements Serializable {

	public enum Gender {
		MALE, FEMALE
	}

	private String id;
	private String name;
	private Gender gender;
	private int grade;
}