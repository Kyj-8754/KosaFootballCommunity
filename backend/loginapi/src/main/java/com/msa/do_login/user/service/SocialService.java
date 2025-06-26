package com.msa.do_login.user.service;

import java.util.UUID;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialService {

	private final ClientRegistrationRepository clientRegistrationRepository;

	public String buildAuthorizationUrl(String provider) {
		ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(provider);
		if (registration == null) {
			throw new IllegalArgumentException("Unknown provider: " + provider);
		}

		String clientId = registration.getClientId();
		String redirectUri = registration.getRedirectUri();

		return switch (provider.toLowerCase()) {
			case "kakao" -> "https://kauth.kakao.com/oauth/authorize?response_type=code"
				+ "&client_id=" + clientId + "&redirect_uri=" + redirectUri;

			case "google" -> "https://accounts.google.com/o/oauth2/v2/auth?response_type=code"
				+ "&client_id=" + clientId + "&redirect_uri=" + redirectUri + "&scope=openid%20email%20profile";

			case "naver" -> {
				String state = UUID.randomUUID().toString();
				yield "https://nid.naver.com/oauth2.0/authorize?response_type=code"
					+ "&client_id=" + clientId + "&redirect_uri=" + redirectUri + "&state=" + state;
			}

			default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
		};
	}
}

