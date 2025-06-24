package com.msa.do_login.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao")
public class KakaoLoginController {
	@GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
