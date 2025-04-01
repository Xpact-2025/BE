package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.dto.DetailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.dto.ThumbnailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.exception.ExperienceException;
import com.itstime.xpact.global.exception.MemberException;
import lombok.RequiredArgsConstructor;
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

    public List<ThumbnailExperienceReadResponseDto> readAll() {
        // member 조회
        Long currentMemberId = securityProvider.getCurrentMemberId();
        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_EXISTS));

        return experienceRepository.findAllByMember(member)
                .stream()
                .map(ThumbnailExperienceReadResponseDto::of)
                .toList();
    }

    public DetailExperienceReadResponseDto read(Long experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ExperienceException(ErrorCode.EXPERIENCE_NOT_EXISTS));

        return DetailExperienceReadResponseDto.from(experience);
    }

}
