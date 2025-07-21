package com.msa.do_login.user.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msa.do_login.user.dto.SocialUserInfoDTO;
import com.msa.do_login.user.dto.SocialUserRegisterDTO;
import com.msa.do_login.user.service.SocialService;
import com.msa.do_login.user.vo.SocialAccount;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
@Tag(name = "소셜 로그인 API", description = "소셜 로그인 관련 API")
public class SocialController {

    private final SocialService socialService;

    // 1. 클라이언트가 소셜 로그인 창으로 리다이렉트할 URL 요청
    @Operation(summary = "소셜 로그인 인증 URL 요청", description = "클라이언트가 소셜 로그인 인증 URL을 요청합니다.")
    @GetMapping("/authorize/{provider}")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(@PathVariable String provider) {
        String url = socialService.buildAuthorizationUrl(provider);
        return ResponseEntity.ok(Map.of("authorizationUrl", url));
    }

    // 2. 소셜 로그인 완료 후 리다이렉트되는 콜백 처리
    @Operation(summary = "소셜 로그인 콜백 처리", description = "소셜 로그인 완료 후 콜백을 처리합니다.")
    @GetMapping("/callback/{provider}")
    public ResponseEntity<?> oauthCallback(
    		@PathVariable("provider") String provider,
    	    @RequestParam("code") String code,
    	    @RequestParam(value = "state", required = false) String state) {
    	log.info(">> 소셜 로그인 콜백 요청 - provider: {}, code: {}, state: {}", provider, code, state);
        try {
            // 1. access token 요청
            String accessToken = socialService.getAccessToken(provider, code, state);

            // 2. 사용자 정보 요청
            SocialUserInfoDTO userInfo = socialService.getUserInfo(provider, accessToken);

            // 3. 소셜 계정 DB 조회
            SocialAccount account = socialService.findSocialAccount(provider, userInfo.getProviderId());

            // 4. 회원가입 여부 판단
            if (account == null) {
                // Vue 프론트엔드 회원가입 화면으로 redirect
                String redirectUrl = String.format(
                    "http://www.itsfootball.store/member/socialRegister?provider=%s&providerId=%s&nickname=%s&profileImage=%s",
                    provider,
                    userInfo.getProviderId(),
                    URLEncoder.encode(userInfo.getNickname(), StandardCharsets.UTF_8),
                    URLEncoder.encode(userInfo.getProfileImage(), StandardCharsets.UTF_8)
                );
                return ResponseEntity.status(302)  // 302 Found 리다이렉트
                        .header("Location", redirectUrl)
                        .build();
            }

            // 5. 기존 회원 로그인 처리 (JWT 발급)
            Map<String, String> tokenMap = socialService.generateToken(provider, userInfo.getProviderId());

    		String redirectUrl = String.format(
    			"http://www.itsfootball.store/oauth/success?accessToken=%s&refreshToken=%s",
    			URLEncoder.encode(tokenMap.get("accessToken"), StandardCharsets.UTF_8),
    			URLEncoder.encode(tokenMap.get("refreshToken"), StandardCharsets.UTF_8)
    		);

    		return ResponseEntity.status(302)
    				.header("Location", redirectUrl)
    				.build();

    	} catch (Exception e) {
    		log.error("소셜 로그인 처리 중 예외 발생", e);
    		return ResponseEntity.status(500).body(Map.of(
    				"res_code", "500",
    				"res_msg", "소셜 로그인 처리 중 오류 발생",
    				"error", e.getMessage()
    		));
    	}
    }
    
	// 회원 가입
    @Operation(summary = "소셜 회원 가입", description = "소셜 계정을 통해 회원 가입을 처리합니다.")
	@PostMapping("/na/register")
	public ResponseEntity<Map<String, Object>> register(@RequestBody SocialUserRegisterDTO dto) {
		socialService.register(dto);
		return ResponseEntity.ok(Map.of("res_code", "200", "res_msg", "회원가입 성공"));
	}
}