//package org.data.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		http
//				.authorizeHttpRequests(authorize -> authorize
//						.requestMatchers("/public").permitAll() // API công khai
//						.anyRequest().authenticated() // Các API khác cần token
//				)
//				.oauth2ResourceServer(oauth2 -> oauth2
//						.jwt()
//				);
//
//		return http.build();
//	}
//}
