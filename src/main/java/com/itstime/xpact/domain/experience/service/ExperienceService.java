package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.dto.DetailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.dto.ThumbnailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.entity.*;
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
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final MemberService memberService;

    private final SecurityProvider securityProvider;

    private final OpenAIService openAIService;

    /**
     * Experience 저장 로직 : FormType에 따라 양식이 바뀜
     */
    public void create(ExperienceCreateRequestDto createRequestDto) throws CustomException {

        // member 조회
        Long currentMemberId = securityProvider.getCurrentMemberId();
        Member member = memberService.findMember(currentMemberId);

        // createRequestDto에서 Status.DRAFT라면 잘못된 요청
        if(createRequestDto.getStatus().equals(Status.DRAFT))
            throw CustomException.of(ErrorCode.STATUS_NOT_CONSISTENCY);

        Experience experience;
        // experience entity 생성 (experience 형식에 따라 StarForm, SimpleForm 결정)
        if(createRequestDto.getFormType() == FormType.SIMPLE_FORM) {
            experience = Experience.SimpleForm(createRequestDto);
        } else if(createRequestDto.getFormType() == FormType.STAR_FORM) {
            experience = Experience.StarForm(createRequestDto);
        } else throw new CustomException(ErrorCode.INVALID_FORMTYPE);

        // member-entity간 설정
        experience.addMember(member);
        experienceRepository.save(experience);

        /*
            경험 저장 후 openai로 요약본 생성 요청 보냄
            => 이때 경험 저장은 동기 요청
            => openai 요청은 비동기 요청
            => 대시보드의 필요한 데이터들이 비동기로 도착하므로 대시보드 조회 요청도 비동기적으로 다뤄야함
        */
        // TODO : openAI에서 요약정보 받아옴 (create)
        openAIService.summarizeContentOfExperience(experience);
    }

    public void update(Long experienceId, ExperienceUpdateRequestDto updateRequestDto) throws CustomException {
        Long currentMemberId = securityProvider.getCurrentMemberId();

        // experience 조회
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> CustomException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        if(!experience.getMember().getId().equals(currentMemberId))
            throw CustomException.of(ErrorCode.NOT_YOUR_EXPERIENCE);

        if(experience.getStatus().equals(Status.DRAFT))
            throw CustomException.of(ErrorCode.STATUS_NOT_CONSISTENCY);

        // form에 따라 수정방식을 다르게 잡음
        if(updateRequestDto.getFormType().equals(FormType.SIMPLE_FORM)) {
            experience.updateToSimpleForm(updateRequestDto);
        } else if(updateRequestDto.getFormType().equals(FormType.STAR_FORM)) {
            experience.updateToStarForm(updateRequestDto);
        } else throw CustomException.of(ErrorCode.INVALID_FORMTYPE);

        experienceRepository.save(experience);

        // TODO : openai에서 요약정보 받아와야함 (update)
        if(updateRequestDto.getStatus().equals(Status.SAVE))
            openAIService.summarizeContentOfExperience(experience);
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
