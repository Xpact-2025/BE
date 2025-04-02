package com.itstime.xpact.global.security.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.security.dto.response.KakaoInfoResponseDto;
import com.itstime.xpact.global.security.dto.response.KakaoTokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
@Slf4j
public class KakaoUtil {

    // 일단 kakao utils에 필요한 것들만 선언
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private static String redirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private static String authorizationUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userInfoUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenUri;

    // 카카오로부터 AccessToken 받아오기
    public String reqeustToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new org.springframework.http.HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenDto> response =
                restTemplate.exchange(
                        tokenUri,
                        HttpMethod.POST,
                        request,
                        KakaoTokenDto.class
                );

        ObjectMapper objectMapper = new ObjectMapper();

        KakaoTokenDto kakaoToken = null;

        try {
            kakaoToken = objectMapper.readValue(
                    response.getBody().accessToken, KakaoTokenDto.class
            );
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return kakaoToken.accessToken;
    }

    // 카카오로부터 Profile 얻어오기
    public KakaoInfoResponseDto.KakaoAccount requestProfile(String token) throws CustomException {

        RestTemplate restTemplate2 = new RestTemplate();
        HttpHeaders headers2 = new HttpHeaders();

        headers2.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        headers2.add("Authorization", "Bearer " + token);

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers2);

        ResponseEntity<String> response2 = restTemplate2.exchange(
                userInfoUri,
                HttpMethod.GET,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();

        KakaoInfoResponseDto.KakaoAccount profile = null;

        try {
            profile = objectMapper.readValue(
                    response2.getBody(), KakaoInfoResponseDto.KakaoAccount.class);
        } catch (JsonProcessingException e) {
            log.info(Arrays.toString(e.getStackTrace()));
            throw CustomException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return profile;
    }
}
