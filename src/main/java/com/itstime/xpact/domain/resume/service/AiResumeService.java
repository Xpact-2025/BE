package com.itstime.xpact.domain.resume.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.domain.experience.dto.response.RecommendExperienceResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.resume.dto.request.AiResumeRequestDto;
import com.itstime.xpact.domain.resume.dto.request.RecommendExperienceRequestDto;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.openai.dto.response.ResumeResponseDto;
import com.itstime.xpact.global.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiResumeService {

    private final OpenAiService openAiService;
    private final ExperienceRepository experienceRepository;
    private final ObjectMapper objectMapper;

    // 제목 + 문항으로 k개의 경험 선택 (similaritySearch)
    // 선택한 경험을 프롬프트 인풋으로 사용하여 자기소개서에 쓰일 추천 경험을 리턴
    public List<RecommendExperienceResponseDto> getRecommendExperience(RecommendExperienceRequestDto requestDto, Member member) {
        String title = requestDto.getTitle();
        String question = requestDto.getQuestion();

//        List<Document> documents = chromaRepository.similaritySearch(title.concat(question), member.getId());
//        List<Long> experienceIds = documents.stream()
//                .map(document -> ((Number) document.getMetadata().get("experienceId")).longValue())
//                .toList();

        List<Experience> experiences = experienceRepository.findAllWithSubExperiencesByMember(member);
        if(experiences.isEmpty()){
            throw GeneralException.of(ErrorCode.NOT_EXISTS_RECOMMENDABLE);
        }

        String result = openAiService.getRecommendExperience(requestDto, experiences);

        List<RecommendExperienceResponseDto> responseDtos;
        try {
             responseDtos = objectMapper.readValue(result, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw GeneralException.of(ErrorCode.FAILED_OPENAI_PARSING);
        }
        return responseDtos;
    }

    // requestDto의 필드 값으로 문항 생성
    public ResumeResponseDto createResume(AiResumeRequestDto requestDto, Member member) {
        List<Experience> experiences = experienceRepository.findAllByIds(requestDto.getExperienceIds(), member);

        String result = openAiService.createResume(requestDto, experiences);

        ResumeResponseDto responseDto;
        try {
            responseDto = objectMapper.readValue(result, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw GeneralException.of(ErrorCode.FAILED_OPENAI_PARSING);
        }
        return responseDto;
    }
}
