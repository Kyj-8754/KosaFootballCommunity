package com.msa.do_login.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.msa.do_login.admin.dao.AdminDAO;
import com.msa.do_login.user.vo.UserVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminDAO adminDAO;
	public List<UserVO> getUserList(){
		return adminDAO.getUserList();
	}
}
