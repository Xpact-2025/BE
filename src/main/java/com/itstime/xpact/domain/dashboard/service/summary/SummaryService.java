package com.itstime.xpact.domain.dashboard.service.summary;

import com.itstime.xpact.domain.dashboard.dto.response.SummaryResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final ExperienceRepository experienceRepository;

    public Slice<SummaryResponseDto> getSliceSummary(Member member, Pageable pageable) {
        Slice<Experience> experiences = experienceRepository.findExperienceByMember(member, pageable);
        List<SummaryResponseDto> lists =  experiences.stream()
                .filter(experience -> experience.getSummary() != null)
                .map(experience -> SummaryResponseDto.builder()
                        .title(experience.getTitle() != null ? experience.getTitle() : experience.getQualification().getQualification())
                        .summary(experience.getSummary())
                        .experienceType(experience.getMetaData().getExperienceType().toString())
                        .build())
                .toList();

        return new SliceImpl<>(lists, pageable, experiences.hasNext());
    }
}
