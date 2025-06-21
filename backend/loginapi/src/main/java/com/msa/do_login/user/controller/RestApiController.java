package com.msa.do_login.user.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msa.do_login.user.dao.UserDAO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class RestApiController {
	private final UserDAO userDAO;
	private final PasswordEncoder passwordEncoder;
}
