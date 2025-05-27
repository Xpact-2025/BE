package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.converter.ExperienceConverter;
import com.itstime.xpact.domain.experience.dto.request.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.request.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.entity.*;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.experience.repository.GroupExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceService {

    private final GroupExperienceRepository groupExperienceRepository;
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

    public void update(Long groupId, ExperienceUpdateRequestDto updateRequestDto) throws GeneralException {
        Member member = securityProvider.getCurrentMember();
        GroupExperience groupExperience = groupExperienceRepository.findById(groupId)
                .orElseThrow(() -> GeneralException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        validateOwner(member, groupExperience);

        // 영속화된 experience -> Map으로 만들어 조회를 O(1)으로 만듦
        List<Experience> experiences = experienceRepository.findByGroupExperience(groupExperience);
        Map<Long, Experience> existingExperiences = experiences.stream()
                .collect(Collectors.toMap(Experience::getId, experience -> experience));

        // newExperiences -> 수정된 experience
        List<Experience> updatedExperiences = updateRequestDto.getSubExps().stream()
                .map(subExperience ->
                        experienceConverter.updateEntity(existingExperiences, updateRequestDto, subExperience))
                .toList();

        // groupExperience - experiences mapping
        setMapping(updatedExperiences, groupExperience);

        updatedExperiences.stream()
                .filter(experience -> experience.getStatus().equals(Status.SAVE))
                .forEach(this::setSummaryAndDetailRecruit);

        experienceRepository.saveAll(updatedExperiences);
    }

    private void validateOwner(Member member, GroupExperience groupExperience) {
        if(!groupExperience.getMember().getId().equals(member.getId())){
            throw GeneralException.of(ErrorCode.EXPERIENCE_NOT_EXISTS);
        }
    }

    // 경험 생성에서 사용됨
    private void setMapping(Member member, List<Experience> experiences) {
        GroupExperience groupExperience = GroupExperience.builder().member(member).experiences(experiences).build();
        member.getGroupExperiences().add(groupExperience);

        experiences.forEach(experience -> experience.setGroupExperience(groupExperience));

        groupExperienceRepository.save(groupExperience);
    }

    // 경험 수정에서 사용됨
    private void setMapping(List<Experience> experiences, GroupExperience groupExperience) {
        groupExperience.getExperiences().clear();
        groupExperience.getExperiences().addAll(experiences);

        experiences.forEach(experience -> experience.setGroupExperience(groupExperience));
    }

    public void delete(Long groupExperienceId) {
        Long currentMemberId = securityProvider.getCurrentMemberId();

        GroupExperience groupExperience = groupExperienceRepository.findById(groupExperienceId)
                .orElseThrow(() -> GeneralException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        if(!groupExperience.getMember().getId().equals(currentMemberId)) {
            throw GeneralException.of(ErrorCode.NOT_YOUR_EXPERIENCE);
        }

        groupExperienceRepository.delete(groupExperience);
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
