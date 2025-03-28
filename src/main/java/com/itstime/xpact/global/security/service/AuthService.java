package com.itstime.xpact.global.security.service;

import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.TokenProvider;
import com.itstime.xpact.global.security.dto.request.SignupRequestDto;
import com.itstime.xpact.global.security.dto.response.SignupResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    // 회원 가입 서비스
    @Transactional
    public SignupResponseDto register(SignupRequestDto requestDto) {

        // 회원 가입 여부 확인
        if (memberRepository.existsByEmail(requestDto.email())) {
            // TODO : Custom Exception 변경
            throw new RuntimeException("이미 존재하는 회원입니다.");
        }

        String encodedPassword = this.passwordEncoder.encode(requestDto.password());
        Member member = SignupRequestDto.toEntity(requestDto);
        member.setPassword(encodedPassword);
        memberRepository.save(member);

        return SignupResponseDto.toDto(member);
    }
}
