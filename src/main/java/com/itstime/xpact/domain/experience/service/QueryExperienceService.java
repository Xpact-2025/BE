package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.dto.response.DetailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.dto.response.ThumbnailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
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

    private final ExperienceRepository experienceRepository;
    private final SecurityProvider securityProvider;
    private final MemberRepository memberRepository;

    private static final String LATEST = "LATEST";
    private static final String OLDEST = "OLDEST";
    private static final String MODIFIED = "modifiedTime";

    public List<ThumbnailExperienceReadResponseDto> readAll(List<String> types, String order) throws GeneralException {

        // member 조회
        Member member = memberRepository.findById(securityProvider.getCurrentMemberId()).orElseThrow(() ->
                GeneralException.of(ErrorCode.MEMBER_NOT_EXISTS));

        Sort sort;
        if(order.equals(LATEST)) {
            sort = Sort.by(Sort.Direction.DESC, MODIFIED);
        } else if(order.equals(OLDEST)) {
            sort = Sort.by(Sort.Direction.ASC, MODIFIED);
        } else {
            throw GeneralException.of(ErrorCode.INVALID_ORDER);
        }

        if(types.get(0).equalsIgnoreCase("all")) {
            return experienceRepository.findAllByMember(member, sort)
                    .stream()
                    .map(ThumbnailExperienceReadResponseDto::of)
                    .toList();
        } else {
            List<ExperienceType> experienceTypes = types.stream()
                    .map(type -> ExperienceType.valueOf(type.toUpperCase()))
                    .toList();

            return experienceRepository.findAllByMemberIdAndType(member.getId(), order, experienceTypes)
                    .stream()
                    .map(ThumbnailExperienceReadResponseDto::of)
                    .toList();
        }
    }

    public DetailExperienceReadResponseDto read(Long experienceId) throws GeneralException {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> GeneralException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        Long memberId = securityProvider.getCurrentMemberId();
        if(!experience.getMember().getId().equals(memberId))
            throw new GeneralException(ErrorCode.NOT_YOUR_EXPERIENCE);

        return DetailExperienceReadResponseDto.of(experience);
    }

    public List<ThumbnailExperienceReadResponseDto> query(String query) {
        return experienceRepository.queryExperience(query).stream()
                .map(ThumbnailExperienceReadResponseDto::of)
                .toList();
    }
}
