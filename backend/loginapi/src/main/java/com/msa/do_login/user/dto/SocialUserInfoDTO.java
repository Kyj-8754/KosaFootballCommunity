package com.msa.do_login.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserInfoDTO {
    private String providerId;
    private String provider;
    private String nickname;
    private String profileImage;
}