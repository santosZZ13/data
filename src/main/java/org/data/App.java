package org.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;


@SpringBootApplication
//@EnableDiscoveryClient
public class App {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(App.class, args);
	}
}
