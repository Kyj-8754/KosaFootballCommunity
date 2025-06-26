package com.msa.do_security.security.service;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.msa.do_security.security.dao.UserDAO;
import com.msa.do_security.security.vo.OAuth2UserVO;
import com.msa.do_security.security.vo.SocialAccountVO;
import com.msa.do_security.security.vo.UserVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserDAO userDAO;
    private final HttpSession session;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = extractEmail(provider, attributes);
        String providerId = extractProviderId(provider, attributes);
        String nickname = extractName(provider, attributes);
        String profileImg = extractProfileImage(provider, attributes);

        if (email == null || providerId == null) {
            throw new OAuth2AuthenticationException("소셜 로그인 정보 추출 실패");
        }

        SocialAccountVO account = userDAO.findSocialAccount(provider, providerId);

        if (account != null) {
            UserVO userVO = userDAO.findUserByUserNo(account.getUserNo());
            return new OAuth2UserVO(userVO, account, attributes);
        }

        // 회원가입이 필요한 경우: 세션에 소셜 정보 저장 후 예외 발생
        session.setAttribute("SOCIAL_JOIN_INFO", Map.of(
            "email", email,
            "provider", provider,
            "providerId", providerId,
            "nickname", nickname,
            "profileImg", profileImg
        ));

        throw new OAuth2AuthenticationException("추가 회원가입 필요");
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "google" -> (String) attributes.get("email");
            case "kakao" -> {
                Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
                yield (String) account.get("email");
            }
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                yield (String) response.get("email");
            }
            default -> null;
        };
    }

    private String extractProviderId(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "google" -> (String) attributes.get("sub");
            case "kakao" -> String.valueOf(attributes.get("id"));
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                yield (String) response.get("id");
            }
            default -> null;
        };
    }

    private String extractName(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "google" -> (String) attributes.get("name");
            case "kakao" -> {
                Map<String, Object> props = (Map<String, Object>) attributes.get("properties");
                yield (String) props.get("nickname");
            }
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                yield (String) response.get("name");
            }
            default -> "Unknown";
        };
    }

    private String extractProfileImage(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "google" -> (String) attributes.get("picture");
            case "kakao" -> {
                Map<String, Object> props = (Map<String, Object>) attributes.get("properties");
                yield (String) props.get("profile_image");
            }
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                yield (String) response.get("profile_image");
            }
            default -> null;
        };
    }
}