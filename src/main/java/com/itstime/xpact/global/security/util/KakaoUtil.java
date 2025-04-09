package com.itstime.xpact.global.security.util;

import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.security.dto.response.KakaoInfoResponseDto;
import com.itstime.xpact.global.security.dto.response.KakaoTokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Component
@Slf4j
public class KakaoUtil {

    private final WebClient.Builder webClientBuilder;
    // 일단 kakao utils에 필요한 것들만 선언
    @Value("${spring.security.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.kakao.redirect-uri}")
    private String redirectUri;

    private static final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com"; // 카카오 인증 URI
    private static final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com"; // 카카오 사용자 정보 요청 URI

    public KakaoUtil(@Qualifier("webClientBuilder") WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    // 인증코드 URL
    public String buildLoginUrl() {
        return String.format(
                "%s/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                KAUTH_TOKEN_URL_HOST, clientId, redirectUri
        );
    }

    // 카카오로부터 AccessToken 받아오기
    public KakaoTokenDto requestAccessToken(String authorizationCode) {

        try {
            return webClientBuilder
                    .baseUrl(KAUTH_TOKEN_URL_HOST)
                    .build()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/oauth/token")
                            .queryParam("grant_type", "authorization_code")
                            .queryParam("client_id", clientId)
                            .queryParam("redirect_uri", redirectUri)
                            .queryParam("code", authorizationCode)
                            .build())
                    .retrieve()
                    .bodyToMono(KakaoTokenDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("카카오 서버로 AccessToken 요청 중 오류 : {} ", e.getResponseBodyAsString());
            throw CustomException.of(ErrorCode.ACCESS_TOKEN_REQUEST_FAILED);
        }
    }

    // 카카오로부터 Profile 얻어오기
    public KakaoInfoResponseDto requestProfile(String token) {
        try {
            return webClientBuilder
                    .baseUrl(KAUTH_USER_URL_HOST)
                    .build()
                    .get()
                    .uri("/v2/user/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(KakaoInfoResponseDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("카카오로부터 사용자 Info 요청 중 오류 : {} ", e.getResponseBodyAsString());
            throw CustomException.of(ErrorCode.ACCESS_TOKEN_REQUEST_FAILED);
        }
    }

    // KAKAO로부터 얻어온 token의 정보 콘솔 로그 출력
    public void logTokenInfo(KakaoTokenDto tokenDto) {
        log.info("[Kakao Token] Access Token: {}", tokenDto.getAccessToken());
        log.info("[Kakao Token] Refresh Token: {}", tokenDto.getRefreshToken());
        log.info("[Kakao Token] Scope: {}", tokenDto.getScope());
    }
}
