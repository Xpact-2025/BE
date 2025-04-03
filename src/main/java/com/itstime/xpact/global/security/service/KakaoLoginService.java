package com.itstime.xpact.global.security.service;

import com.itstime.xpact.domain.member.common.Role;
import com.itstime.xpact.domain.member.common.Type;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.TokenProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.security.common.LoginStrategy;
import com.itstime.xpact.global.security.dto.response.KakaoInfoResponseDto;
import com.itstime.xpact.global.security.dto.response.KakaoTokenDto;
import com.itstime.xpact.global.security.dto.response.LoginResponseDto;
import com.itstime.xpact.global.security.util.KakaoUtil;
import com.itstime.xpact.global.security.util.RefreshTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Tag(name ="카카오 로그인 API Controller", description = "카카오 로그인 API")
public class KakaoLoginService implements LoginStrategy {

    private final KakaoUtil kakaoUtil;
    private final TokenProvider tokenProvider;
    private final RefreshTokenUtil refreshTokenUtil;
    private final MemberRepository memberRepository;

    @Override
    public Type supports() {
        return Type.KAKAO;
    }

    // 인증코드 얻기
    @Transactional(readOnly = true)
    public String getLoginUrl() {
        return kakaoUtil.buildLoginUrl();
    }

    @Transactional
    public LoginResponseDto login(String code, HttpServletResponse response) {
        KakaoTokenDto kakaoToken = kakaoUtil.requestAccessToken(code);
        String token = kakaoToken.getAccessToken();
        KakaoInfoResponseDto profile = kakaoUtil.requestProfile(token);

        String email = profile.getKakaoAccount().getEmail();

        // 이미 회원이면 예외
        if (memberRepository.findByEmail(email).isPresent()) {
            log.error("이메일을 통해 회원 조회 시작...");
            throw CustomException.of(ErrorCode.MEMBER_ALREADY_EXISTS);
        }

        // 새로운 회원 저장
        log.info("새로운 회원 저장 시작...");
        Member member = Member.builder()
                .email(email)
                .name(profile.getKakaoAccount().getName())
                .role(Role.ROLE_USER)
                .type(Type.KAKAO)
                .build();
        memberRepository.save(member);

        // 토큰 발급
        String accessToken = tokenProvider.generateAccessToken(member);
        String refreshToken = tokenProvider.generateRefreshToken(member);

        // Refresh Token 쿠키로 설정
        refreshTokenUtil.addRefreshTokenCookie(response, refreshToken);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
