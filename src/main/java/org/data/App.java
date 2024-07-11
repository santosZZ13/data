package org.data;

import lombok.*;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Objects;

@SpringBootApplication
public class App {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	public static class Name {
		private int age;
		private String name;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Name name1 = (Name) o;
			return age == name1.age && Objects.equals(name, name1.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(age, name);
		}
	}
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
