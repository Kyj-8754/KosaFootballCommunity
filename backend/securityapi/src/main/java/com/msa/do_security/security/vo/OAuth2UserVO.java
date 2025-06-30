package com.msa.do_security.security.vo;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

@Getter
public class OAuth2UserVO implements OAuth2User {

    private final UserVO userVO;
    private final SocialAccountVO socialAccountVO;
    private final Map<String, Object> attributes;

    public OAuth2UserVO(UserVO userVO, SocialAccountVO socialAccountVO, Map<String, Object> attributes) {
        this.userVO = userVO;
        this.socialAccountVO = socialAccountVO;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userVO.getAuthorities();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(userVO.getUserNo());
    }
}
