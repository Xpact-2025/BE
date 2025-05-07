package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.dto.request.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.request.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.entity.*;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.domain.member.service.MemberService;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final SecurityProvider securityProvider;
    private final OpenAiService openAiService;
    private final MemberRepository memberRepository;

    public void create(ExperienceCreateRequestDto createRequestDto) throws CustomException {
        // member 조회
        Member member = memberRepository.findById(securityProvider.getCurrentMemberId()).orElseThrow(() ->
                CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

        // enum타입이 될 string 필드 검증 로직 (INVALID한 값이 들어오면 CustomException발생)
        Status.validateStatus(createRequestDto.getStatus());
        FormType.validateFormType(createRequestDto.getFormType());
        ExperienceType.validateExperienceType(createRequestDto.getExperienceType());

        // 키워드 설정하지 않는 로직 (따로 keyword 설정 필요)
        Experience experience = switch (FormType.valueOf(createRequestDto.getFormType())) {
            case SIMPLE_FORM -> Experience.SimpleForm(createRequestDto);
            case STAR_FORM -> Experience.StarForm(createRequestDto);
        };

        // keyword 개수 체크
        Keyword.validateKeyword(createRequestDto.getKeywords());

        // dto의 keywords를 keyword 엔티티로 변경
        List<Keyword> keywords = createRequestDto.getKeywords().stream()
                .map(keywordStr -> Keyword.builder()
                        .name(keywordStr)
                        .experience(experience)
                        .build())
                .collect(Collectors.toList());

        // experience와의 연관관계 설정
        experience.setKeyword(keywords);
        experience.addMember(member);

        experienceRepository.save(experience);

        if(Status.valueOf(createRequestDto.getStatus()).equals(Status.SAVE))
            openAiService.summarizeExperience(experience);
    }

    public void update(Long experienceId, ExperienceUpdateRequestDto updateRequestDto) throws CustomException {
        Long currentMemberId = securityProvider.getCurrentMemberId();

        // experience 조회
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> CustomException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        // 저장된 경험을 임시저장으로 돌리는 플로우 막는 로직
        if(experience.getStatus().equals(Status.SAVE) && updateRequestDto.getStatus().equals(Status.DRAFT.name())) {
            throw CustomException.of(ErrorCode.INVALID_SAVE);
        }


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

        Keyword.validateKeyword(updateRequestDto.getKeywords());

        experienceRepository.save(experience);

        if(Status.valueOf(updateRequestDto.getStatus()).equals(Status.SAVE))
            openAiService.summarizeExperience(experience);
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
