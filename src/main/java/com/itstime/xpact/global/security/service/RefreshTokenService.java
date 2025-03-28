package com.itstime.xpact.global.security.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    // RefreshToken redis 서버에 저장
    public void saveRefreshToken(long memberId, String refreshToken) {

        String key = "refreshToken:" + memberId;
        redisTemplate.opsForValue().set(key, refreshToken, refreshTokenExpiration, TimeUnit.SECONDS);
    }

    // RefreshToken 조회
    public String getRefreshToken(long memberId) {
        String key = "refreshToken:" + memberId;

        return (String) redisTemplate.opsForValue().get(key);
    }

    // RefreshToken 삭제하기
    public void deletRefreshToken(long memberId) {
        String key = "refreshToken:" + memberId;
        redisTemplate.delete(key);
    }

    // RefreshToken 쿠키에 추가하기
    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 전송됨
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshTokenExpiration); // 초 단위
        response.addCookie(cookie);
    }
}
