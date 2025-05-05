package com.itstime.xpact.domain.member.service;

import com.itstime.xpact.domain.member.dto.request.MemberInfoRequestDto;
import com.itstime.xpact.domain.member.dto.response.MemberInfoResponseDto;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final SecurityProvider securityProvider;

    // 마이페이지 조회하기
    @Transactional(readOnly = true)
    public MemberInfoResponseDto getMyinfo() throws CustomException {

        // Member 조회
        Long memberId = securityProvider.getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

        log.info("{} : 회원의 정보 조회 시작 ... ", member.getEmail());
        return member.toMemberInfoResponseDto(member);
    }

    // 프로필 정보 등록하기
    @Transactional
    public MemberInfoResponseDto saveMyinfo(MemberInfoRequestDto requestDto) throws CustomException {

        // Member 조회
       Long memberId = securityProvider.getCurrentMemberId();
       Member member = memberRepository.findById(memberId)
                       .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

        member.updateMemberInfo(requestDto);
        return member.toMemberInfoResponseDto(member);
    }
}
