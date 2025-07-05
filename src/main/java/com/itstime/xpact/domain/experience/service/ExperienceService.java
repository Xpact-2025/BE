package com.itstime.xpact.domain.experience.service;

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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
        Member member = securityProvider.getCurrentMember();
        Pair<Experience, List<SubExperience>> pairExperience = experienceConverter.createExperience(createRequestDto);
        Experience experience = pairExperience.getLeft();
        List<SubExperience> subExperiences = pairExperience.getRight();
        setMapping(member, experience);
        experienceRepository.save(experience);

        if(experience.getStatus().equals(Status.SAVE)) setSummaryAndDetailRecruit(experience, subExperiences);
    }

    private void setMapping(Member member, Experience experience) {
        experience.setMember(member);
        member.getExperiences().add(experience);
    }

    public void update(Long experienceId, ExperienceUpdateRequestDto updateRequestDto) throws GeneralException {
        Member member = securityProvider.getCurrentMember();

        Experience experience = experienceRepository.findById(experienceId).orElseThrow(() ->
                GeneralException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        validateOwner(member, experience);

        // update experience
        experience.updateExperience(updateRequestDto);

        Map<Long, SubExperience> subExperienceMap = experience.getSubExperiences().stream()
                .collect(Collectors.toMap(SubExperience::getId, subExperience -> subExperience));

        // update subExperience
        List<SubExperience> updatedSubExperiences = updateRequestDto.getSubExperiences().stream()
                .map(subExperienceDto ->
                        experienceConverter.updateSubExperience(subExperienceMap, updateRequestDto, subExperienceDto))
                .toList();

        // mapping
        setMapping(updatedSubExperiences, experience);

        if(experience.getStatus().equals(Status.SAVE)) setSummaryAndDetailRecruit(experience, updatedSubExperiences);

        experienceRepository.save(experience);
    }

    private void validateOwner(Member member, Experience experience) {
        if(!experience.getMember().getId().equals(member.getId())){
            throw GeneralException.of(ErrorCode.EXPERIENCE_NOT_EXISTS);
        }
    }

    // 경험 수정에서 사용됨
    private void setMapping(List<SubExperience> subExperiences, Experience experience) {
        experience.getSubExperiences().clear();
        experience.getSubExperiences().addAll(subExperiences);
        subExperiences.forEach(subExperience -> subExperience.setExperience(experience));
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

    public void deleteAll() {
        Member member = securityProvider.getCurrentMember();
        experienceRepository.deleteAllByMember(member);
    }

    private void setSummaryAndDetailRecruit(Experience experience, List<SubExperience> subExperiences) {
        openAiService.summarizeExperience(experience, subExperiences);
        openAiService.getDetailRecruitFromExperience(experience);
    }
}
