package org.data.test;

import java.util.HashSet;
import java.util.Set;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Data
@RedisHash
public class User {
	@Id
	@ToString.Include
	private String id;


	@ToString.Include
	private String name;


	@EqualsAndHashCode.Include
	@ToString.Include
	@Indexed
	private String email;


	private String password;

	@Transient
	private String passwordConfirm;

	@Reference
	private Set<Role> roles = new HashSet<Role>();

	public void addRole(Role role) {
		roles.add(role);
	}
}