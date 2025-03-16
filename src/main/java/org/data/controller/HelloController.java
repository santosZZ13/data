package org.data.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/api/hello")
	public String hello(@AuthenticationPrincipal Jwt jwt) {
		return "Hello, " + jwt.getClaim("username") + "! Your authorities: " + jwt.getClaim("authorities");
	}

	@GetMapping("/public")
	public String publicEndpoint() {
		return "This is a public endpoint!";
	}
}