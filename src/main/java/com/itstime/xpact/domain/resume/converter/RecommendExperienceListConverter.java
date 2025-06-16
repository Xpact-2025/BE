package com.itstime.xpact.domain.resume.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.domain.resume.entity.embeddable.RecommendExperience;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.exception.GeneralException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Converter
@RequiredArgsConstructor
public class RecommendExperienceListConverter implements AttributeConverter<List<RecommendExperience>, String> {

    private final ObjectMapper objectMapper;

    public String convertToDatabaseColumn(List<RecommendExperience> recommendExperiences) {
        try {
            return objectMapper.writeValueAsString(recommendExperiences);
        } catch (JsonProcessingException e) {
            throw GeneralException.of(ErrorCode.PARSING_ERROR);
        }
    }

    public List<RecommendExperience> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw GeneralException.of(ErrorCode.PARSING_ERROR);
        }
    }
}
