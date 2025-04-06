package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.entity.*;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.service.MemberService;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final MemberService memberService;
    private final SecurityProvider securityProvider;
    private final OpenAiService openAiService;

    public void create(ExperienceCreateRequestDto createRequestDto) throws CustomException {
        // member 조회
        Long currentMemberId = securityProvider.getCurrentMemberId();
        Member member = memberService.findMember(currentMemberId);

        // enum타입이 될 string 필드 검증 로직 (INVALID한 값이 들어오면 CustomException발생)
        Status.validateStatus(createRequestDto.getStatus());
        FormType.validateFormType(createRequestDto.getFormType());
        ExperienceType.validateExperienceType(createRequestDto.getExperienceType());

        Experience experience = switch (FormType.valueOf(createRequestDto.getFormType())) {
            case SIMPLE_FORM -> Experience.SimpleForm(createRequestDto);
            case STAR_FORM -> Experience.StarForm(createRequestDto);
        };

        // member-entity간 설정
        experience.addMember(member);
        experienceRepository.save(experience);

        if(Status.valueOf(createRequestDto.getStatus()).equals(Status.SAVE))
            openAiService.summarizeContentOfExperience(experience);
    }

    public void update(Long experienceId, ExperienceUpdateRequestDto updateRequestDto) throws CustomException {
        Long currentMemberId = securityProvider.getCurrentMemberId();

        // experience 조회
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> CustomException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        if(!experience.getMember().getId().equals(currentMemberId))
            throw CustomException.of(ErrorCode.NOT_YOUR_EXPERIENCE);

        // enum타입이 될 string 필드 검증 로직 (INVALID한 값이 들어오면 CustomException발생)
        Status.validateStatus(updateRequestDto.getStatus());
        FormType.validateFormType(updateRequestDto.getFormType());
        ExperienceType.validateExperienceType(updateRequestDto.getExperienceType());

        switch (FormType.valueOf(updateRequestDto.getFormType())) {
            case STAR_FORM -> experience.updateToStarForm(updateRequestDto);
            case SIMPLE_FORM -> experience.updateToSimpleForm(updateRequestDto);
        }

        experienceRepository.save(experience);

        if(Status.valueOf(updateRequestDto.getStatus()).equals(Status.SAVE))
            openAiService.summarizeContentOfExperience(experience);
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
