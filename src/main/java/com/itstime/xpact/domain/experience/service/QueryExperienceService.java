package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.dto.DetailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.dto.ThumbnailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.CustomException;
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
    private final MemberRepository memberRepository;
    private final SecurityProvider securityProvider;

    private static final String LATEST = "LATEST";
    private static final String OLDEST = "OLDEST";
    private static final String MODIFIED = "modifiedTime";

    public List<ThumbnailExperienceReadResponseDto> readAll(List<String> types, String order) throws CustomException {

        // member 조회
        Long currentMemberId = securityProvider.getCurrentMemberId();
        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

        Sort sort;
        if(order.equals(LATEST)) {
            sort = Sort.by(Sort.Direction.DESC, MODIFIED);
        } else if(order.equals(OLDEST)) {
            sort = Sort.by(Sort.Direction.ASC, MODIFIED);
        } else {
            throw CustomException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if(types.get(0).equalsIgnoreCase("all")) {
            return experienceRepository.findAllByMember(member, sort)
                    .stream()
                    .map(ThumbnailExperienceReadResponseDto::of)
                    .toList();
        } else {
            List<ExperienceType> experienceTypes = types.stream()
                    .map(type -> {
                        type = type.toUpperCase();
                        return ExperienceType.valueOf(type);
                    })
                    .toList();

            return experienceRepository.findAllByMemberIdAndType(member.getId(), order, experienceTypes)
                    .stream()
                    .map(ThumbnailExperienceReadResponseDto::of)
                    .toList();
        }
    }

    public DetailExperienceReadResponseDto read(Long experienceId) throws CustomException {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> CustomException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));

        Long memberId = securityProvider.getCurrentMemberId();
        if(!experience.getMember().getId().equals(memberId))
            throw new CustomException(ErrorCode.NOT_YOUR_EXPERIENCE);

        return DetailExperienceReadResponseDto.of(experience);
    }
}
