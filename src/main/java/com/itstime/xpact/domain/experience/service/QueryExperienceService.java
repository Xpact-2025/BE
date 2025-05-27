package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.dto.response.DetailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.dto.response.ThumbnailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.entity.GroupExperience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.experience.repository.GroupExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryExperienceService {

    private final GroupExperienceRepository groupExperienceRepository;
    private final ExperienceRepository experienceRepository;
    private final SecurityProvider securityProvider;

    private static final String LATEST = "LATEST";
    private static final String OLDEST = "OLDEST";
    private static final String MODIFIED = "modifiedTime";

    public List<ThumbnailExperienceReadResponseDto> readAll(List<String> types, String order) throws GeneralException {
        // member 조회
        Member member = securityProvider.getCurrentMember();

        if(types.get(0).equalsIgnoreCase("all")) {
            Sort sort = null;
            switch (order) {
                case OLDEST -> sort = Sort.by(Sort.Direction.ASC, MODIFIED);
                case LATEST -> sort = Sort.by(Sort.Direction.DESC, MODIFIED);
            }

            return experienceRepository.findAllByMember(member, sort)
                    .stream()
                    .map(ThumbnailExperienceReadResponseDto::of)
                    .toList();
        } else {
            List<ExperienceType> experienceTypes = types.stream()
                    .map(type -> ExperienceType.valueOf(type.toUpperCase()))
                    .toList();

            return experienceRepository.findAllByMemberAndType(member, order, experienceTypes)
                    .stream()
                    .map(ThumbnailExperienceReadResponseDto::of)
                    .toList();
        }
    }

    public DetailExperienceReadResponseDto read(Long groupId) throws GeneralException {
        GroupExperience groupExperience = groupExperienceRepository.findById(groupId).orElseThrow(() ->
                GeneralException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        Member member = securityProvider.getCurrentMember();
        if(!groupExperience.getMember().equals(member)) {
            throw new GeneralException(ErrorCode.NOT_YOUR_EXPERIENCE);
        }

        List<Experience> experiences = groupExperience.getExperiences();

        return DetailExperienceReadResponseDto.of(experiences, groupId);
    }

    public List<ThumbnailExperienceReadResponseDto> query(String query) {
        return experienceRepository.queryExperience(query).stream()
                .map(ThumbnailExperienceReadResponseDto::of)
                .toList();
    }
}
