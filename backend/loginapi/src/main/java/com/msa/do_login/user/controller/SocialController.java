package com.msa.do_login.user.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msa.do_login.user.service.SocialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth/authorize")
public class SocialController {
	 private final SocialService socialService;
	 	
	    @GetMapping("/{provider}")
	    public ResponseEntity<Map<String, String>> getAuthorizationUrl(@PathVariable String provider) {
	        String url = socialService.buildAuthorizationUrl(provider);
	        return ResponseEntity.ok(Map.of("authorizationUrl", url));
	    }
}
