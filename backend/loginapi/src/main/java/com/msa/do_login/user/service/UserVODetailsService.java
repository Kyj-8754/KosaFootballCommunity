package com.msa.do_login.user.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.msa.do_login.user.dao.UserDAO;
import com.msa.do_login.user.dto.UserDTO;
import com.msa.do_login.user.vo.LocalAccount;
import com.msa.do_login.user.vo.UserVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserVODetailsService implements UserDetailsService {
	private final UserDAO userDAO;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("username = {}", username);
		Optional<UserVO> optional = userDAO.findByUserId(username);
		UserVO userVO = optional.orElseThrow(() -> new UsernameNotFoundException("멤버 아이디를 찾을 수 없습니다"));
		log.info("userVO = {}", userVO);

		LocalAccount account = userDAO.findAccountByUserNo(userVO.getUserNo());
		if (account == null)
			throw new UsernameNotFoundException("계정을 찾을 수 없습니다.");

		UserDTO userDTO = UserDTO.of(userVO, account);
		log.info("userDTO = {}", userDTO);
		return userDTO;
	}

}
