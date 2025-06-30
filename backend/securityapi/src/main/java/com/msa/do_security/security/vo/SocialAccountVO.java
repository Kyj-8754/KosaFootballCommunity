package com.msa.do_security.security.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialAccountVO {
	private int socialNo;
    private int userNo;
    private String provider;
    private String providerId;
    private String email;
    private String nickname;
    private String profileImg;
}
