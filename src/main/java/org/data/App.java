package org.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;


//@SpringBootApplication
public class App {
	public static void main(String[] args) {
//		SpringApplication.run(App.class, args);

		LocalDateTime now = LocalDateTime.now();

		// get date
		now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);

		System.out.println("Hello World! " + now);
	}
}
