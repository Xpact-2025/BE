package com.itstime.xpact.global.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itstime.xpact.domain.experience.dto.request.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoDataService {

    private final ExperienceService experienceService;

    @Value("${json.exp-data}")
    private String jsonExpData;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveExp() throws IOException {
        experienceService.deleteAll();
        objectMapper.registerModule(new JavaTimeModule());

        List<ExperienceCreateRequestDto> experienceCreateRequestDtos = objectMapper.readValue(
                new File(jsonExpData),
                new TypeReference<>() {
                });

        experienceService.deleteAll();
        experienceCreateRequestDtos.forEach(experienceService::create);
    }
}
