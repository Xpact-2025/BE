package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.dto.response.DetailExperienceResponseDto;
import com.itstime.xpact.domain.experience.dto.response.ThumbnailExperienceResponseDto;
import com.itstime.xpact.domain.experience.dto.response.ThumbnailExperienceWithkeywordResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.entity.SubExperience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
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

    private final ExperienceRepository experienceRepository;
    private final SecurityProvider securityProvider;

    private static final String LATEST = "LATEST";
    private static final String OLDEST = "OLDEST";
    private static final String MODIFIED = "modifiedTime";

    public List<ThumbnailExperienceWithkeywordResponseDto> readAll(List<String> types, String order) throws GeneralException {
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
                    .map(ThumbnailExperienceWithkeywordResponseDto::of)
                    .toList();
        } else {
            List<ExperienceType> experienceTypes = types.stream()
                    .map(type -> ExperienceType.valueOf(type.toUpperCase()))
                    .toList();

            return experienceRepository.findAllByMemberAndType(member, order, experienceTypes)
                    .stream()
                    .map(ThumbnailExperienceWithkeywordResponseDto::of)
                    .toList();
        }
    }

    public DetailExperienceResponseDto read(Long experienceId) throws GeneralException {
        Experience experience = experienceRepository.findById(experienceId).orElseThrow(() ->
                GeneralException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        Member member = securityProvider.getCurrentMember();
        if(!experience.getMember().equals(member)) {
            throw new GeneralException(ErrorCode.NOT_YOUR_EXPERIENCE);
        }

        List<SubExperience> subExperiences = experience.getSubExperiences();

        return DetailExperienceResponseDto.of(subExperiences, experience);
    }

    public List<ThumbnailExperienceResponseDto> query(String query) {
        Member member = securityProvider.getCurrentMember();

        return experienceRepository.queryExperience(member, query).stream()
                .map(ThumbnailExperienceResponseDto::of)
                .toList();
    }
}
