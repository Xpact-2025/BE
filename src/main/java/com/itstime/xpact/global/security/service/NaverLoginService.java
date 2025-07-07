package com.itstime.xpact.global.security.service;

import com.itstime.xpact.domain.member.common.Role;
import com.itstime.xpact.domain.member.common.Type;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.TokenProvider;
import com.itstime.xpact.global.security.common.LoginStrategy;
import com.itstime.xpact.global.security.dto.response.LoginResponseDto;
import com.itstime.xpact.global.security.dto.response.NaverInfoResponseDto;
import com.itstime.xpact.global.security.util.NaverUtil;
import com.itstime.xpact.global.security.util.RefreshTokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverLoginService implements LoginStrategy {

    @Override
    public Type supports() {
        return Type.NAVER;
    }

    private final NaverUtil naverUtil;
    private final MemberRepository memberRepository;

    private final TokenProvider tokenProvider;
    private final RefreshTokenUtil refreshTokenUtil;

    // 인증코드 얻기
    @Transactional(readOnly = true)
    public String getLoginUrl() {
        return naverUtil.buildLoginUrl();
    }

    @Transactional
    public LoginResponseDto loginWithNaver(HttpServletResponse response, String code, String state) {

        NaverInfoResponseDto profile = naverUtil.findProfile(naverUtil.getToken(code, state).getAccessToken());

        Member member = memberRepository.findByEmailAndType(profile.getResponse().getEmail(), Type.NAVER)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .name(profile.getResponse().getName())
                            .email(profile.getResponse().getEmail())
                            .role(Role.ROLE_USER)
                            .type(Type.NAVER)
                            .build();
                    return memberRepository.save(newMember);
                });

        String accessToken = tokenProvider.generateAccessToken(member);
        String refreshToken = tokenProvider.generateRefreshToken(member);

        refreshTokenUtil.saveRefreshToken(member.getId(), refreshToken);
        refreshTokenUtil.addRefreshTokenCookie(response, refreshToken);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole())
                .type(member.getType())
                .build();
    }
}
