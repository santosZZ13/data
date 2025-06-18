package org.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

import java.io.IOException;


@SpringBootApplication
@EnableRetry
//@EnableDiscoveryClient
public class App {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(App.class, args);
	}
}
