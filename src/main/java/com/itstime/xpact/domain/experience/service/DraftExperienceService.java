package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DraftExperienceService {

    private final ExperienceRepository experienceRepository;


    public void save(ExperienceCreateRequestDto createRequestDto) {

    }

    public void update(Long experienceId, ExperienceUpdateRequestDto updateRequestDto) {

    }

    public void delete(Long experienceId) {

    }
}
