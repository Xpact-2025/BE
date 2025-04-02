package com.itstime.xpact.global.security.service;

import com.itstime.xpact.domain.member.common.Type;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.TokenProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.response.ErrorResponse;
import com.itstime.xpact.global.security.common.LoginStrategy;
import com.itstime.xpact.global.security.dto.response.KakaoInfoResponseDto;
import com.itstime.xpact.global.security.dto.response.LoginResponseDto;
import com.itstime.xpact.global.security.util.KakaoUtil;
import com.itstime.xpact.global.security.util.RefreshTokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService implements LoginStrategy {

    private final KakaoUtil kakaoUtil;
    private final TokenProvider tokenProvider;
    private final RefreshTokenUtil refreshTokenUtil;
    private final MemberRepository memberRepository;

    @Override
    public Type supports() {
        return Type.KAKAO;
    }

    @Transactional
    public LoginResponseDto login(String code, HttpServletResponse response) {
        String kakaoToken = kakaoUtil.reqeustToken(code);
        KakaoInfoResponseDto.KakaoAccount profile = kakaoUtil.requestProfile(kakaoToken);

        String email = profile.getEmail();

        // 이미 회원이면 예외
        if (memberRepository.findByEmail(email).isPresent()) {
            throw CustomException.of(ErrorCode.MEMBER_ALREADY_EXISTS);
        }

        // 새로운 회원 저장
        Member member = Member.builder()
                .email(email)
                .name(profile.getName())
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
