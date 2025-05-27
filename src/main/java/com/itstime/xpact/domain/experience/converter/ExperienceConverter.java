package com.itstime.xpact.domain.experience.converter;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.dto.request.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.request.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.entity.File;
import com.itstime.xpact.domain.experience.entity.Keyword;
import com.itstime.xpact.domain.experience.entity.embeddable.SimpleForm;
import com.itstime.xpact.domain.experience.entity.embeddable.StarForm;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.exception.GeneralException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.itstime.xpact.domain.experience.common.ExperienceType.IS_QUALIFICATION;

@Component
public class ExperienceConverter {

    public List<Experience> toEntity(ExperienceCreateRequestDto createRequestDto) {
        return createRequestDto.getSubExps().stream()
                .map(subExperience -> {
                    // 공통 필드 처리
                    Experience.ExperienceBuilder experienceBuilder = commonFieldsBuilder(createRequestDto);
                    // 나머지 필드 처리
                    Experience experience = subFieldsBuilder(experienceBuilder, subExperience, ExperienceType.valueOf(createRequestDto.getExperienceType()));
                    // 각 sub경험마다 keywords와 files 매핑
                    setKeywordsAndFiles(experience, subExperience);
                    return experience;
                })
                .toList();
    }

    /**
     * metaData, title, startDate, endDate로만 experience 엔티티 생성
     * 후에 나머지 값들은 subFieldsBuilder에서 builder처리
     */
    private Experience.ExperienceBuilder commonFieldsBuilder(ExperienceCreateRequestDto createRequestDto) {
        // 수상 및 자격증 경험일 때
        if(IS_QUALIFICATION.contains(ExperienceType.valueOf(createRequestDto.getExperienceType()))) {
            return Experience.builder()
                    .experienceType(ExperienceType.valueOf(createRequestDto.getExperienceType()))
                    .qualification(createRequestDto.getQualification())
                    .publisher(createRequestDto.getPublisher())
                    .startDate(createRequestDto.getStartDate())
                    .endDate(createRequestDto.getEndDate())
                    .isEnded(createRequestDto.getEndDate().isBefore(LocalDate.now()));
        } else {
            return Experience.builder()
                    .experienceType(ExperienceType.valueOf(createRequestDto.getExperienceType()))
                    .title(createRequestDto.getTitle())
                    .startDate(createRequestDto.getStartDate())
                    .endDate(createRequestDto.getEndDate())
                    .isEnded(createRequestDto.getEndDate().isBefore(LocalDate.now()));
        }
    }

    private Experience subFieldsBuilder(Experience.ExperienceBuilder experienceBuilder, ExperienceCreateRequestDto.SubExperience subExperience, ExperienceType experienceType) {
        Experience experience = null;
        // 수상 및 자격증 경험일 때
        if(IS_QUALIFICATION.contains(experienceType)) {
            experience = experienceBuilder
                    .subTitle(subExperience.getSubTitle())
                    .simpleDescription(subExperience.getSimpleDescription())
                    .status(Status.valueOf(subExperience.getStatus()))
                    .formType(FormType.STAR_FORM)
                    .build();
        } else {
            switch (FormType.valueOf(subExperience.getFormType())) {
                case STAR_FORM -> experience = experienceBuilder
                            .subTitle(subExperience.getSubTitle())
                            .status(Status.valueOf(subExperience.getStatus()))
                            .formType(FormType.STAR_FORM)
                            .starForm(StarForm.builder()
                                    .situation(subExperience.getSituation())
                                    .task(subExperience.getTask())
                                    .action(subExperience.getAction())
                                    .result(subExperience.getResult())
                                    .build())
                            .build();
                case SIMPLE_FORM -> experience = experienceBuilder
                            .subTitle(subExperience.getSubTitle())
                            .status(Status.valueOf(subExperience.getStatus()))
                            .formType(FormType.SIMPLE_FORM)
                            .simpleForm(SimpleForm.builder()
                                    .role(subExperience.getRole())
                                    .perform(subExperience.getPerform())
                                    .build())
                            .build();
            }
        }
        return experience;
    }

    private void setKeywordsAndFiles(Experience experience, ExperienceCreateRequestDto.SubExperience subExperience) {
        experience.setKeywords(subExperience.getKeywords().stream()
                .map(str -> Keyword.builder()
                            .name(str)
                            .experience(experience)
                            .build()).toList());

        experience.setFiles(subExperience.getFiles().stream()
                .map(url -> File.builder()
                        .fileUrl(url)
                        .experience(experience)
                        .build()).toList());
    }

    public Experience updateEntity(Map<Long, Experience> existingExperiences, ExperienceUpdateRequestDto updateRequestDto, ExperienceUpdateRequestDto.SubExperience targetExperience) {
        Experience experience = existingExperiences.getOrDefault(targetExperience.getSubExperienceId(), new Experience());
        experience.updateCommonFields(updateRequestDto);

        experience.updateSubFields(targetExperience, ExperienceType.valueOf(updateRequestDto.getExperienceType()));
        setKeywordsAndFiles(experience, targetExperience);

        if(Status.SAVE.equals(experience.getStatus()) && Status.DRAFT.equals(Status.valueOf(targetExperience.getStatus()))){
            throw GeneralException.of(ErrorCode.INVALID_SAVE);
        }

        return experience;
    }

    private void setKeywordsAndFiles(Experience experience, ExperienceUpdateRequestDto.SubExperience subExperience) {
        experience.getKeywords().clear();
        experience.setKeywords(subExperience.getKeywords().stream()
                .map(str -> Keyword.builder()
                        .name(str)
                        .experience(experience)
                        .build()).toList());

        experience.getFiles().clear();
        experience.setFiles(subExperience.getFiles().stream()
                .map(url -> File.builder()
                        .fileUrl(url)
                        .experience(experience)
                        .build()).toList());
    }
}
