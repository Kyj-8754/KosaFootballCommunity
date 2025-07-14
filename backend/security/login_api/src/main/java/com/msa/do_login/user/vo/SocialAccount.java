package com.msa.do_login.user.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialAccount {
	private int socialNo;
    private int userNo;
    private String provider;
    private String providerId;
}
