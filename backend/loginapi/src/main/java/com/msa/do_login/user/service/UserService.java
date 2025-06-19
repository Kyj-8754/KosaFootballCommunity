package com.msa.do_login.user.service;

import org.springframework.stereotype.Service;

import com.msa.do_login.user.dao.UserDAO;
import com.msa.do_login.user.vo.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserDAO userDAO;
	public int register(User user) {
		int result = userDAO.register(user);
		return result;
	}
}
