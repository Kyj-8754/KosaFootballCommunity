package com.msa.do_login.user.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.msa.do_login.user.dao.LoginDAO;
import com.msa.do_login.user.dto.SocialUserInfoDTO;
import com.msa.do_login.user.dto.SocialUserRegisterDTO;
import com.msa.do_login.user.vo.SocialAccount;
import com.msa.do_login.user.vo.UserVO;
import com.msa.do_security.security.util.JWTUtil;
import com.msa.do_security.security.vo.SocialAccountVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialService {

	private final ClientRegistrationRepository clientRegistrationRepository;
	private final LoginDAO loginDAO;
	private final JWTUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final WebClient webClient;

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final SecureRandom RANDOM = new SecureRandom();

	private String generateUniqueUserCode(int length) {
		String code;
		do {
			code = generateRandomCode(length);
		} while (loginDAO.existsByUserCode(code));
		return code;
	}

	private String generateRandomCode(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
		}
		return sb.toString();
	}

	public String buildAuthorizationUrl(String provider) {
		ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(provider);
		if (registration == null) {
			throw new IllegalArgumentException("Unknown provider: " + provider);
		}

		String clientId = registration.getClientId();
		String redirectUri = registration.getRedirectUri();

		return switch (provider.toLowerCase()) {
		case "kakao" -> "https://kauth.kakao.com/oauth/authorize?response_type=code" + "&client_id=" + clientId
				+ "&redirect_uri=" + redirectUri;

		case "google" -> "https://accounts.google.com/o/oauth2/v2/auth?response_type=code" + "&client_id=" + clientId
				+ "&redirect_uri=" + redirectUri + "&scope=openid%20email%20profile";

		case "naver" -> {
			String state = UUID.randomUUID().toString();
			yield "https://nid.naver.com/oauth2.0/authorize?response_type=code" + "&client_id=" + clientId
					+ "&redirect_uri=" + redirectUri + "&state=" + state;
		}

		default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
		};
	}

	public String getAccessToken(String provider, String code, String state) {
		log.info("[{}] AccessToken 요청 시작 - code: {}, state: {}", provider, code, state);
		ClientRegistration reg = clientRegistrationRepository.findByRegistrationId(provider);

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("grant_type", "authorization_code");
		formData.add("client_id", reg.getClientId());
		formData.add("client_secret", reg.getClientSecret());
		formData.add("redirect_uri", reg.getRedirectUri());
		formData.add("code", code);
		if ("naver".equals(provider)) {
			formData.add("state", state);
		}

		Map<String, Object> tokenRes = webClient.post().uri(reg.getProviderDetails().getTokenUri())
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.body(BodyInserters.fromFormData(formData)).retrieve().bodyToMono(Map.class)
				.doOnNext(body -> log.info("[{}] AccessToken 응답: {}", provider, body))
				.doOnError(e -> log.error("[{}] AccessToken 요청 실패", provider, e)).block();
		if (tokenRes == null || !tokenRes.containsKey("access_token")) {
			log.error("[{}] access_token이 응답에 없습니다. 응답 내용: {}", provider, tokenRes);
			throw new RuntimeException("access_token을 받아오지 못했습니다.");
		}
		String accessToken = (String) tokenRes.get("access_token");

		if (accessToken == null || accessToken.isBlank()) {
			log.error("[{}] access_token 값이 null 또는 비어있습니다. 응답 내용: {}", provider, tokenRes);
			throw new RuntimeException("access_token이 null입니다.");
		}

		return accessToken;
	}

	public SocialUserInfoDTO getUserInfo(String provider, String accessToken) {
		log.info("[{}] 사용자 정보 요청 시작 - accessToken: {}", provider, accessToken);

		String userInfoUri = switch (provider) {
		case "kakao" -> "https://kapi.kakao.com/v2/user/me";
		case "google" -> "https://www.googleapis.com/oauth2/v3/userinfo";
		case "naver" -> "https://openapi.naver.com/v1/nid/me";
		default -> throw new IllegalArgumentException("지원되지 않는 provider: " + provider);
		};

		Map<String, Object> userRes = webClient.post().uri(userInfoUri)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).retrieve().bodyToMono(Map.class)
				.doOnNext(body -> log.info("[{}] 사용자 정보 응답: {}", provider, body))
				.doOnError(e -> log.error("[{}] 사용자 정보 요청 실패", provider, e)).block();

		log.info("[{}] 최종 사용자 정보 응답 Map: {}", provider, userRes);
		return parseUserInfo(provider, userRes);
	}

	private SocialUserInfoDTO parseUserInfo(String provider, Map<String, Object> data) {
		log.info("[{}] 사용자 데이터 파싱 시작: {}", provider, data);
		return switch (provider) {
		case "kakao" -> {
			String id = String.valueOf(data.get("id"));
			Map<String, Object> props = (Map<String, Object>) data.get("properties");
			yield new SocialUserInfoDTO(id, provider, (String) props.get("nickname"),
					(String) props.get("profile_image"));
		}
		case "google" -> {
			String id = (String) data.get("sub");
			yield new SocialUserInfoDTO(id, provider, (String) data.get("name"), (String) data.get("picture"));
		}
		case "naver" -> {
		    Object resRaw = data.get("response");
		    if (resRaw == null) {
		        log.error("네이버 응답에서 response 키가 없음!");
		        throw new RuntimeException("네이버 사용자 정보 파싱 실패 - response 없음");
		    }
		    Map<String, Object> response = (Map<String, Object>) resRaw;

		    String nickname = (String) response.get("nickname");
		    if (nickname == null || nickname.isBlank()) {
		        nickname = (String) response.get("name"); // fallback 처리
		    }
		    String profile = (String) response.getOrDefault("profile_image", null);
		    yield new SocialUserInfoDTO((String) response.get("id"), provider, nickname, profile);
		}
		default -> throw new IllegalArgumentException("지원되지 않는 provider: " + provider);
		};
	}

	public SocialAccount findSocialAccount(String provider, String providerId) {
		log.info("소셜 계정 조회 - provider: {}, providerId: {}", provider, providerId);
		return loginDAO.findSocialAccount(provider, providerId);
	}

	public UserVO findUserByUserNo(int userNo) {
		log.info("유저 조회 - userNo: {}", userNo);
		return loginDAO.findUserByUserNo(userNo);
	}

	public Map<String, String> generateToken(String provider, String providerId) {
		SocialAccount account = findSocialAccount(provider, providerId);
		log.info("소셜 계정 조회 결과: {}", account);
		if (account == null) {
			throw new IllegalArgumentException("소셜 계정이 존재하지 않습니다.");
		}

		UserVO user = findUserByUserNo(account.getUserNo());
		if (user == null) {
			throw new IllegalArgumentException("유저 정보가 존재하지 않습니다.");
		}

		Map<String, Object> claim = Map.of("userId", account.getProviderId(), "userNo", user.getUserNo(), "userName",
				URLEncoder.encode(user.getUserName(), StandardCharsets.UTF_8), "authCode",
				"ROLE_" + user.getAuthCode());

		return Map.of("accessToken", jwtUtil.generateToken(claim, 1), "refreshToken", jwtUtil.generateToken(claim, 5));
	}

	@Transactional
	public void register(SocialUserRegisterDTO dto) {
		String userCode = generateUniqueUserCode(5);

		UserVO user = UserVO.builder().userName(dto.getUserName()).userBirth(dto.getUserBirth())
				.userPhone(dto.getUserPhone()).userPostCode(dto.getUserPostcode()).userAddr(dto.getUserAddr())
				.userDetailAddr(dto.getUserDetailAddr()).userGender(dto.getUserGender()).authCode("A3")
				.userCode(userCode).userRegDate(new Date()).build();

		loginDAO.insertUser(user);

		SocialAccount account = SocialAccount.builder().userNo(user.getUserNo()).providerId(dto.getProviderId())
				.provider(dto.getProvider()).build();

		loginDAO.insertSocialAccount(account);
	}

}
