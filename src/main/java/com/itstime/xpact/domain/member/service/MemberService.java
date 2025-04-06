package com.itstime.xpact.domain.member.service;

import com.itstime.xpact.domain.member.dto.response.MemberInfoResponseDto;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.TokenProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    // 마이페이지 조회하기
    @Transactional(readOnly = true)
    public MemberInfoResponseDto getMyinfo(String token) throws CustomException {

        Member member = getMemberFromToken(token);

        return member.toMemberInfoResponseDto(member);
    }

    // 회원 정보 수정하기
    @Transactional
    public MemberInfoResponseDto saveMyinfo(String token) throws CustomException {

        Member member = getMemberFromToken(token);

        return null;
    }

    // 학력에 대한 조회



    // getMemberFromToke 메소드 생성
    private Member getMemberFromToken(String token) throws CustomException {
        Long memberId = tokenProvider.getMemberIdFromToken(token);

        return memberRepository.findById(memberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.FAILED_JWT_INFO));
    }

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));
    }
}
