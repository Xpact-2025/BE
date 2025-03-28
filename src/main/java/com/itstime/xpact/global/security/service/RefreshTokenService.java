package com.itstime.xpact.global.security.service;

import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

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
    public void deleteRefreshToken(long memberId) {
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

    // RefreshToken 쿠키에서 삭제하기
    public void removeRefreshTokenCookie(HttpServletResponse response, long memberId) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 즉시 만료
        response.addCookie(refreshTokenCookie);
    }

    // 기록된 쿠키 조회 후 검증
    public Long getMemberIdFromCookie(HttpServletRequest request) {

        log.info("쿠키로부터 Refresh Token 찾기 시작...");
        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            log.warn("쿠키가 비어 있습니다.");
            // TODO : CustomException으로 변경하기
            throw new RuntimeException("쿠키가 존재하지 않습니다.");
        }

        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                String refreshToken = cookie.getValue();
                log.info("Refresh Token 발견. 유효성 검사 시작...");

                if (tokenProvider.validationToken(refreshToken).getSuccess()) {
                    Long memberId = tokenProvider.getMemberIdFromToken(refreshToken);
                    log.info("유효한 Refresh token입니다. Member Id: {}", memberId);
                    return memberId;
                } else {
                    log.warn("유효하지 않은 Refresh Token입니다.");
                    // TODO : CustomException으로 변경
                    throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
                }
            }
        }
        log.warn("Refresh token 쿠키를 찾을 수 없습니다.");
        // TODO : CustomException으로 변경
        throw new RuntimeException("refreshToken 쿠키가 없습니다.");
    }
}
