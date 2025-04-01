package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.domain.member.service.MemberService;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.openai.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DraftExperienceService {

    private final ExperienceRepository experienceRepository;
    private final SecurityProvider securityProvider;
    private final MemberService memberService;
    private final OpenAIService openAIService;

    public void create(ExperienceCreateRequestDto createRequestDto) {
        // member 조회
        Long currentMemberId = securityProvider.getCurrentMemberId();
        Member member = memberService.findMember(currentMemberId);

        if(createRequestDto.getStatus().equals(Status.SAVE))
            throw CustomException.of(ErrorCode.STATUS_NOT_CONSISTENCY);

        Experience experience;
        // experience entity 생성 (experience 형식에 따라 StarForm, SimpleForm 으로 나뉨)
        if(createRequestDto.getFormType() == FormType.SIMPLE_FORM) {
            experience = Experience.SimpleForm(createRequestDto);
        } else if(createRequestDto.getFormType() == FormType.STAR_FORM) {
            experience = Experience.StarForm(createRequestDto);
        } else throw CustomException.of(ErrorCode.INVALID_FORMTYPE);

        experience.addMember(member);
        experienceRepository.save(experience);
    }

    public void update(Long experienceId, ExperienceUpdateRequestDto updateRequestDto) {
        Long currentMemberId = securityProvider.getCurrentMemberId();

        // experience 조회
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> CustomException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        if (!experience.getMember().getId().equals(currentMemberId))
            throw CustomException.of(ErrorCode.NOT_YOUR_EXPERIENCE);

        if(experience.getStatus().equals(Status.SAVE))
            throw CustomException.of(ErrorCode.STATUS_NOT_CONSISTENCY);

        // form에 따라 수정방식을 다르게 잡음
        if (updateRequestDto.getFormType().equals(FormType.SIMPLE_FORM)) {
            experience.updateToSimpleForm(updateRequestDto);
        } else if (updateRequestDto.getFormType().equals(FormType.STAR_FORM)) {
            experience.updateToStarForm(updateRequestDto);
        } else throw CustomException.of(ErrorCode.INVALID_FORMTYPE);

        experienceRepository.save(experience);

        if (updateRequestDto.getStatus().equals(Status.SAVE)) {
            openAIService.summarizeContentOfExperience(experience);
        }
    }

    public void delete(Long experienceId) {
        Long currentMemberId = securityProvider.getCurrentMemberId();

        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> CustomException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        if(!experience.getMember().getId().equals(currentMemberId)) {
            throw CustomException.of(ErrorCode.NOT_YOUR_EXPERIENCE);
        }

        experienceRepository.delete(experience);
    }
}
