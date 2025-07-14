package com.msa.do_security.security.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocalAccount {
	private int localNo;
	private int userNo;
	private String userId;
	private String userPwd;
}