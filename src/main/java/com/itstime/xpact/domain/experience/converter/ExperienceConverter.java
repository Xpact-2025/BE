package com.itstime.xpact.domain.experience.converter;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.dto.request.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.request.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.entity.File;
import com.itstime.xpact.domain.experience.entity.Keyword;
import com.itstime.xpact.domain.experience.entity.SubExperience;
import com.itstime.xpact.domain.experience.entity.embeddable.SimpleForm;
import com.itstime.xpact.domain.experience.entity.embeddable.StarForm;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.itstime.xpact.domain.experience.common.ExperienceType.IS_QUALIFICATION;

@Component
public class ExperienceConverter {

    public Experience createExperience(ExperienceCreateRequestDto createRequestDto) {
        Experience experience = commonFieldsBuilder(createRequestDto);

        List<SubExperience> subExperiences = createRequestDto.getSubExperiences().stream()
                .map(subExperienceDto -> {
                // 나머지 subExperiences 생성
                SubExperience subExperience = subFieldsBuilder(subExperienceDto, ExperienceType.valueOf(createRequestDto.getExperienceType()));

                if(!IS_QUALIFICATION.contains(experience.getExperienceType())) {
                    setFiles(subExperience, subExperienceDto.getFiles());
                }
                setKeywords(subExperience, subExperienceDto.getKeywords());

                return subExperience;
            }).toList();

        setMapping(experience, subExperiences);
        return experience;
    }

    private void setMapping(Experience experience, List<SubExperience> subExperiences) {
        experience.getSubExperiences().clear();
        experience.getSubExperiences().addAll(subExperiences);
        subExperiences.forEach(subExperience -> subExperience.setExperience(experience));
    }

    /**
     * metaData, title, startDate, endDate로만 experience 엔티티 생성
     * 후에 나머지 값들은 subFieldsBuilder에서 builder처리
     */
    private Experience commonFieldsBuilder(ExperienceCreateRequestDto createRequestDto) {
        // 수상 및 자격증 경험일 때
        if(IS_QUALIFICATION.contains(ExperienceType.valueOf(createRequestDto.getExperienceType()))) {
            return Experience.builder()
                    .status(Status.valueOf(createRequestDto.getStatus()))
                    .experienceType(ExperienceType.valueOf(createRequestDto.getExperienceType()))
                    .qualification(createRequestDto.getQualification())
                    .publisher(createRequestDto.getPublisher())
                    .startDate(createRequestDto.getStartDate())
                    .endDate(createRequestDto.getEndDate())
                    .isEnded(createRequestDto.getEndDate().isBefore(LocalDate.now()))
                    .build();
        } else {
            return Experience.builder()
                    .status(Status.valueOf(createRequestDto.getStatus()))
                    .experienceType(ExperienceType.valueOf(createRequestDto.getExperienceType()))
                    .title(createRequestDto.getTitle())
                    .startDate(createRequestDto.getStartDate())
                    .endDate(createRequestDto.getEndDate())
                    .isEnded(createRequestDto.getEndDate().isBefore(LocalDate.now()))
                    .build();
        }
    }

    private SubExperience subFieldsBuilder(ExperienceCreateRequestDto.SubExperienceRequestDto subExperienceDto, ExperienceType experienceType) {
        SubExperience subExperience = null;
        // 수상 및 자격증 경험일 때
        if(IS_QUALIFICATION.contains(experienceType)) {
            subExperience = SubExperience.builder()
                    .subTitle(subExperienceDto.getSubTitle())
                    .simpleDescription(subExperienceDto.getSimpleDescription())
                    .formType(FormType.valueOf(subExperienceDto.getFormType()))
                    .build();
        } else {
            switch (FormType.valueOf(subExperienceDto.getFormType())) {
                case STAR_FORM -> subExperience = SubExperience.builder()
                            .subTitle(subExperienceDto.getSubTitle())
                            .formType(FormType.STAR_FORM)
                            .starForm(StarForm.builder()
                                    .situation(subExperienceDto.getSituation())
                                    .task(subExperienceDto.getTask())
                                    .action(subExperienceDto.getAction())
                                    .result(subExperienceDto.getResult())
                                    .build())
                            .build();
                case SIMPLE_FORM -> subExperience = SubExperience.builder()
                            .subTitle(subExperienceDto.getSubTitle())
                            .formType(FormType.SIMPLE_FORM)
                            .simpleForm(SimpleForm.builder()
                                    .role(subExperienceDto.getRole())
                                    .perform(subExperienceDto.getPerform())
                                    .build())
                            .build();
            }
        }
        return subExperience;
    }

    public SubExperience updateSubExperience(Map<Long, SubExperience> subExperienceMap, ExperienceUpdateRequestDto updateRequestDto, ExperienceUpdateRequestDto.SubExperienceRequestDto subExperienceRequestDto) {
        SubExperience subExperience = subExperienceMap.getOrDefault(subExperienceRequestDto.getSubExperienceId(), new SubExperience());
        subExperience.updateSubFields(subExperienceRequestDto, ExperienceType.valueOf(updateRequestDto.getExperienceType()));

        if(!IS_QUALIFICATION.contains(ExperienceType.valueOf(updateRequestDto.getExperienceType()))) {
            setFiles(subExperience, subExperienceRequestDto.getFiles());
        }
        setKeywords(subExperience, subExperienceRequestDto.getKeywords());

        return subExperience;
    }

    public static final String[] ALLOW_EXPERIENCE_FIELDS = {"id", "experienceType", "title", "qualification", "publisher", "summary"};
    public String toText(Experience experience) {
        StringBuilder text = new StringBuilder();
        for (Field field : experience.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if(List.of(ALLOW_EXPERIENCE_FIELDS).contains(field.getName()) && field.get(experience) != null) {
                    text.append(field.getName()).append(": ").append(field.get(experience)).append("\n");                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return text.toString();
    }

    public static final String[] ALLOW_SUB_EXPERIENCE_FIELDS = {"subTitle", "simpleDescription"};
    public static final String[] ALLOW_SUB_EXPERIENCE_EMBEDDABBLE = {"starForm", "simpleForm"};
    public String toText(List<SubExperience> subExperiences) {
        StringBuilder text = new StringBuilder();
        for (SubExperience subExperience : subExperiences) {
            for (Field field : subExperience.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if(List.of(ALLOW_SUB_EXPERIENCE_EMBEDDABBLE).contains(field.getName()) && field.get(subExperience) != null) {
                        text.append(embeddableToText(field.get(subExperience)));
                    }
                    else if(List.of(ALLOW_SUB_EXPERIENCE_FIELDS).contains(field.getName()) && field.get(subExperience) != null) {
                        text.append(field.getName()).append(": ").append(field.get(subExperience)).append("\n");                }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return text.toString();
    }

    private String embeddableToText(Object obj) {
        StringBuilder sb = new StringBuilder();
        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    sb.append(field.getName()).append(": ").append(value).append("\n");
                }
            } catch (IllegalAccessException e) {
                sb.append(field.getName()).append(": access error, ");
            }
        }

        if (sb.length() > 2) {
            sb.setLength(sb.length() - 1); // 마지막 ", " 제거
        }

        return sb.toString();
    }

    private void setFiles(SubExperience subExperience, List<String> files) {
        subExperience.getFiles().clear();
        subExperience.setFiles(files.stream()
                .map(url -> File.builder()
                        .fileUrl(url)
                        .subExperience(subExperience)
                        .build()).toList());
    }

    private void setKeywords(SubExperience subExperience, List<String> keywords) {
        subExperience.getKeywords().clear();
        subExperience.setKeywords(keywords.stream()
                .map(str -> Keyword.builder()
                        .name(str)
                        .subExperience(subExperience)
                        .build()).toList());

    }
}
