package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.entity.SimpleForm;
import com.itstime.xpact.domain.experience.entity.StarForm;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
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
public class DraftExperienceService {

    private final ExperienceRepository experienceRepository;
    private final SecurityProvider securityProvider;
    private final MemberRepository memberRepository;

    @Transactional
    public void create(ExperienceCreateRequestDto createRequestDto) {
        // member 조회
        Long currentMemberId = securityProvider.getCurrentMemberId();
        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> CustomException.of((ErrorCode.MEMBER_NOT_EXISTS)));

        Experience draftExperience;
        // experience entity 생성 (experience 형식에 따라 StarForm, SimpleForm 으로 나뉨)
        if(createRequestDto.getFormType() == FormType.SIMPLE_FORM) {
            draftExperience = SimpleForm.from(createRequestDto);
        } else if(createRequestDto.getFormType() == FormType.STAR_FORM) {
            draftExperience = StarForm.from(createRequestDto);
        } else throw CustomException.of(ErrorCode.INVALID_FORMTYPE);

        draftExperience.addMember(member);

        experienceRepository.save(draftExperience);
    }

    public void update(Long experienceId, ExperienceUpdateRequestDto updateRequestDto) {

    }

    public void delete(Long experienceId) {

    }
}
