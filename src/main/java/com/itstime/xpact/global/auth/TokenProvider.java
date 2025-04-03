package com.itstime.xpact.global.auth;

import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.CustomExceptionHandler;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.response.RestResponse;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String KEY_ROLES = "roles";
    private final MemberRepository memberRepository;
    private final CustomExceptionHandler customExceptionHandler;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${spring.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    // token 생성
    public String generateToken(
            Member member, long expiration, boolean isRefreshToken
    ) {

        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expiration * 1000);

            return Jwts.builder()
                    .setHeaderParam("type", isRefreshToken ? "refresh" : "access")
                    .setSubject(String.valueOf(member.getId()))
                    .claim(KEY_ROLES, member.getRole())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS256, this.secretKey)
                    .compact();

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Token 생성에 실패하였습니다.");
        }
    }

    // Access Token 생성
    public String generateAccessToken(Member member) {
        return generateToken(member, accessTokenExpiration, false);
    }

    // Refresh Token 생성
    public String generateRefreshToken(Member member) {
        return generateToken(member, refreshTokenExpiration, true);
    }

    // token으로부터 ID 얻기
    public long getMemberIdFromToken(String token) throws RuntimeException {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(this.secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String memberId = claims.getSubject();

        if(!StringUtils.hasText(memberId)) {
            // TODO: Custom Exception 생성
            throw new RuntimeException();
        }
        return Long.parseLong(memberId);
    }

    // Payload(body) 정보 얻기
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // JWT 검증
    public RestResponse<?> validationToken(String token) throws CustomException {
        try {
            final Claims claims = getClaims(token);
            return RestResponse.ok(claims);
        } catch (MalformedJwtException e) {
            log.warn("유효하지 않은 JWT token: {}", e.getMessage());
            throw CustomException.of(ErrorCode.INVALID_JWT_TOKEN);
        } catch (SignatureException e) {
            log.warn("유효하지 않은 서명의 JWT token: {}", e.getMessage());
            throw CustomException.of(ErrorCode.INVALID_JWT_SIGNATURE);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT token: {}", e.getMessage());
            throw CustomException.of(ErrorCode.EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT token: {}", e.getMessage());
            throw CustomException.of(ErrorCode.UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException ex) {
            log.warn("Empty JWT token: {}", ex.getMessage());
            throw CustomException.of(ErrorCode.TOKEN_NOT_FOUND);
        }
    }
}