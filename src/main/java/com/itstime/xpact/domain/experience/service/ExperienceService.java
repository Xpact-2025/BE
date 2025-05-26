package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.converter.ExperienceConverter;
import com.itstime.xpact.domain.experience.dto.request.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.request.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.entity.*;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.itstime.xpact.domain.experience.common.ExperienceType.IS_QUALIFICATION;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final ExperienceConverter experienceConverter;
    private final SecurityProvider securityProvider;
    private final OpenAiService openAiService;

    public void create(ExperienceCreateRequestDto createRequestDto) throws GeneralException {
        // member 조회
        Member member = securityProvider.getCurrentMember();

        List<Experience> experiences = experienceConverter.toEntity(createRequestDto);
        setMapping(member, experiences);
        experienceRepository.saveAll(experiences);

        experiences.stream()
                .filter(experience -> experience.getStatus().equals(Status.SAVE))
                .forEach(this::setSummaryAndDetailRecruit);
    }

    private void setMapping(Member member, List<Experience> experiences) {
        member.getExperiences().addAll(experiences);
        experiences.forEach(experience -> experience.setMember(member));
    }


    public void update(Long experienceId, ExperienceUpdateRequestDto updateRequestDto) throws GeneralException {
        Long currentMemberId = securityProvider.getCurrentMemberId();

        // experience 조회
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> GeneralException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        // 저장된 경험을 임시저장으로 돌리는 플로우 막는 로직
        if(Status.SAVE.equals(experience.getMetaData().getStatus()) && Status.DRAFT.equals(Status.valueOf(updateRequestDto.getStatus()))) {
            throw GeneralException.of(ErrorCode.INVALID_SAVE);
        }


        if(!experience.getMember().getId().equals(currentMemberId))
            throw GeneralException.of(ErrorCode.NOT_YOUR_EXPERIENCE);

        // enum타입이 될 string 필드 검증 로직 (INVALID한 값이 들어오면 CustomException발생)
        Status.validateStatus(updateRequestDto.getStatus());
        FormType.validateFormType(updateRequestDto.getFormType());
        ExperienceType.validateExperienceType(updateRequestDto.getExperienceType());

        if(IS_QUALIFICATION.contains(ExperienceType.valueOf(updateRequestDto.getExperienceType()))) {
            experience.updateToQualification(updateRequestDto);
        } else {
            // 아닐 때는 star, simple 나눠야함
            switch (FormType.valueOf(updateRequestDto.getFormType())) {
                case STAR_FORM -> experience.updateToStarForm(updateRequestDto);
                case SIMPLE_FORM -> experience.updateToSimpleForm(updateRequestDto);
            }
            saveNonQualification(experience, updateRequestDto);
        }

        experienceRepository.save(experience);

        if(Status.valueOf(updateRequestDto.getStatus()).equals(Status.SAVE)) {
            setSummaryAndDetailRecruit(experience);
        }
    }

    private void saveNonQualification(Experience experience, ExperienceUpdateRequestDto updateRequestDto) {
        Keyword.validateKeyword(updateRequestDto.getKeywords());
        if(updateRequestDto.getKeywords() != null && !updateRequestDto.getKeywords().isEmpty()) {
            List<Keyword> keywords = updateRequestDto.getKeywords().stream()
                    .map(keywordStr -> Keyword.builder()
                            .name(keywordStr)
                            .experience(experience)
                            .build())
                    .collect(Collectors.toCollection(ArrayList::new));
            experience.setKeywords(keywords);
        }

        List<File> files = updateRequestDto.getFiles().stream()
                .map(fileUrl -> File.builder()
                        .fileUrl(fileUrl)
                        .experience(experience)
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));
        experience.setFiles(files);
    }

    public void delete(Long experienceId) {
        Long currentMemberId = securityProvider.getCurrentMemberId();

        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> GeneralException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        if(!experience.getMember().getId().equals(currentMemberId)) {
            throw GeneralException.of(ErrorCode.NOT_YOUR_EXPERIENCE);
        }

        experienceRepository.delete(experience);
    }

    private void setSummaryAndDetailRecruit(Experience experience) {
        openAiService.summarizeExperience(experience);
        openAiService.getDetailRecruitFromExperience(experience);
    }

    public void deleteAll() {
        Member member = securityProvider.getCurrentMember();
        experienceRepository.deleteAllByMember(member);
    }
}
