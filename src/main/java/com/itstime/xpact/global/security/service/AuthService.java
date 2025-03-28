package com.itstime.xpact.global.security.service;

import com.itstime.xpact.domain.member.common.Role;
import com.itstime.xpact.domain.member.common.Type;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.TokenProvider;
import com.itstime.xpact.global.security.dto.request.LoginRequestDto;
import com.itstime.xpact.global.security.dto.request.SignupRequestDto;
import com.itstime.xpact.global.security.dto.response.LoginResponseDto;
import com.itstime.xpact.global.security.dto.response.SignupResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    // 회원 가입 서비스
    @Transactional
    public SignupResponseDto register(SignupRequestDto requestDto) {

        // 회원 가입 여부 확인
        if (memberRepository.existsByEmail(requestDto.email())) {
            log.warn("이미 존재하는 회원입니다.");
            // TODO : Custom Exception 변경
            throw new RuntimeException("이미 존재하는 회원입니다.");
        }

        log.info("{" + requestDto.email() + "} :  회원 가입 시작");
        String encodedPassword = this.passwordEncoder.encode(requestDto.password());
        SignupRequestDto updatedRequestDto = new SignupRequestDto(
                requestDto.email(),
                encodedPassword,
                requestDto.name(),
                requestDto.birthDate(),
                Type.FORM,
                Role.ROLE_USER
        );

        Member member = SignupRequestDto.toEntity(updatedRequestDto);
        memberRepository.save(member);

        log.info("{" + requestDto.email() + "} : 회원 가입 완료");
        return SignupResponseDto.toDto(member);
    }

    @Transactional
    public LoginResponseDto generalLogin(
            LoginRequestDto requestDto,
            HttpServletResponse httpResponse
    ) {
        // TODO: RuntimeException -> CustomException 변경

        // 가입된 회원인지 조회
        log.info(requestDto.email() + "의 회원 조회를 시작합니다.");
        Member member = memberRepository.findByEmail(requestDto.email())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // 비밀번호 검증
        log.info("비밀번호 검증을 시작합니다.");
        if (!passwordEncoder.matches(requestDto.password(), member.getPassword())) {
            throw new RuntimeException();
        }
        log.info("로그인에 성공하였습니다.");

        String accessToken = tokenProvider.generateAccessToken(member);
        String refreshToken = tokenProvider.generateRefreshToken(member);
        refreshTokenService.saveRefreshToken(member.getId(), refreshToken);
        refreshTokenService.addRefreshTokenCookie(httpResponse, refreshToken);

        return new LoginResponseDto(accessToken);
    }

    // 로그아웃
    @Transactional
    public void logout(HttpServletResponse response, String token) {
        Long memberId = tokenProvider.getMemberIdFromToken(token);

        refreshTokenService.removeRefreshTokenCookie(response, memberId);
        refreshTokenService.removeRefreshTokenCookie(response, memberId);
    }

    // 액세스 토큰 재발급
    @Transactional
    public LoginResponseDto refresh(
            HttpServletRequest request, HttpServletResponse response
    ) {
        Long memberId = refreshTokenService.getMemberIdFromCookie(request);

        log.info(memberId + "의 회원을 조회합니다.");
        Member member = memberRepository.findById(memberId)
                // TODO: CustomException 변경
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        log.info("Access Token을 재발급합니다.");
        String newAccessToken = tokenProvider.generateAccessToken(member);
        return new LoginResponseDto(newAccessToken);
    }
}
