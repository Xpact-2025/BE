package com.itstime.xpact.domain.member.service;

import com.itstime.xpact.domain.member.dto.request.MemberSaveRequestDto;
import com.itstime.xpact.domain.member.dto.response.EducationSaveResponseDto;
import com.itstime.xpact.domain.member.dto.response.MemberSaveResponseDto;
import com.itstime.xpact.domain.member.dto.response.MypageInfoResponseDto;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.domain.recruit.dto.response.DesiredRecruitResponseDto;
import com.itstime.xpact.domain.recruit.service.RecruitService;
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

    private final EducationService educationService;
    private final RecruitService recruitService;

    // Member 정보 저장하기
    @Transactional
    public MemberSaveResponseDto updateMember(MemberSaveRequestDto requestDto) {

        Long memberId = securityProvider.getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

        member.updateMemberInfo(requestDto);

        EducationSaveResponseDto educationDto = null;
        DesiredRecruitResponseDto desiredRecruitDto = null;

        if (requestDto.educationSaveRequestDto() != null ) {
            educationDto = educationService.saveEducationInfo(member, requestDto.educationSaveRequestDto());
        }

        if (requestDto.desiredRecruitRequestDto() != null ) {
            desiredRecruitDto = recruitService.updateDesiredRecruit(requestDto.desiredRecruitRequestDto());
        }

        return member.toMemberSaveResponseDto(member, educationDto, desiredRecruitDto);
    }

    public MypageInfoResponseDto getMember() {

        Long memberId = securityProvider.getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

        log.info("{} : 회원의 정보 조회 시작 ... ", member.getEmail());

        return member.toMypageInfoResponseDto(member);
    }
}
