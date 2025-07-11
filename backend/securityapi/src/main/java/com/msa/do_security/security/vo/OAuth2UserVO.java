package com.msa.do_security.security.vo;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

@Getter
public class OAuth2UserVO implements OAuth2User, UserDetails {

    private final UserVO userVO;
    private final SocialAccountVO socialAccountVO;
    private final Map<String, Object> attributes;

    public OAuth2UserVO(UserVO userVO, SocialAccountVO socialAccountVO, Map<String, Object> attributes) {
        this.userVO = userVO;
        this.socialAccountVO = socialAccountVO;
        this.attributes = attributes;
    }

    /** OAuth2User 메서드 구현 **/
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(userVO.getUserNo());
    }

    /** UserDetails 메서드 구현 **/
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userVO.getAuthorities();
    }

    @Override
    public String getPassword() {
        return null; // 소셜 로그인 사용자는 비밀번호 없음
    }

    @Override
    public String getUsername() {
        return socialAccountVO.getProviderId(); // 소셜 고유 식별자
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (true = 만료 아님)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격증명(비밀번호 등) 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부
    }
}