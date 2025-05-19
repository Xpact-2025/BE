package com.itstime.xpact.domain.member.service;

import com.itstime.xpact.domain.member.dto.request.MemberSaveRequestDto;
import com.itstime.xpact.domain.member.dto.response.EducationSaveResponseDto;
import com.itstime.xpact.domain.member.dto.response.MemberSaveResponseDto;
import com.itstime.xpact.domain.member.dto.response.MypageInfoResponseDto;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.recruit.dto.response.DesiredRecruitResponseDto;
import com.itstime.xpact.domain.recruit.service.RecruitService;
import com.itstime.xpact.global.auth.SecurityProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final SecurityProvider securityProvider;

    private final EducationService educationService;
    private final RecruitService recruitService;

    // Member 정보 저장하기
    @Transactional
    public MemberSaveResponseDto updateMember(MemberSaveRequestDto requestDto) {

        Member member = securityProvider.getCurrentMember();

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

        Member member = securityProvider.getCurrentMember();

        log.info("{} : 회원의 정보 조회 시작 ... ", member.getEmail());

        return member.toMypageInfoResponseDto(member);
    }
}
