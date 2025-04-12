package com.itstime.xpact.domain.member.service;

import com.itstime.xpact.domain.member.dto.request.MemberInfoRequestDto;
import com.itstime.xpact.domain.member.dto.request.SchoolSaveRequestDto;
import com.itstime.xpact.domain.member.dto.response.MemberInfoResponseDto;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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

        return member.toMemberInfoResponseDto(member);
    }

    // 프로필 정보 등록하기
    @Transactional
    public MemberInfoResponseDto saveMyinfo(
            MemberInfoRequestDto requestDto
    ) throws CustomException {

        // Member 조회
        Long memberId = securityProvider.getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

        member.builder()
                .name(requestDto.name())
                .imgurl(requestDto.imgurl())
                .age(requestDto.age())
                .education(requestDto.schoolInfo())
                .recruit(requestDto.recruit())
                .build();
        return null;
    }

    // 학력에 대한 조회
    private String toEducation(SchoolSaveRequestDto schoolInfo) {

        // 학력에 대한 정보를 얻어왔다면 그대로 가져와서 변환하도록 설정

        // 학력에 대한 정보를 얻지 못하였다면 직접 입력 로직
        return null;
    }

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));
    }
}
