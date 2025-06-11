package com.itstime.xpact.domain.resume.service;

import com.itstime.xpact.domain.experience.dto.response.RecommendExperienceResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.resume.dto.request.AiResumeRequestDto;
import com.itstime.xpact.domain.resume.dto.request.CreateResumeRequestDto;
import com.itstime.xpact.domain.resume.dto.request.RecommendExperienceRequestDto;
import com.itstime.xpact.domain.resume.dto.request.UpdateResumeRequestDto;
import com.itstime.xpact.domain.resume.dto.response.DetailResumeResponseDto;
import com.itstime.xpact.domain.resume.dto.response.ThumbnailResumeResponseDto;
import com.itstime.xpact.domain.resume.entity.Resume;
import com.itstime.xpact.domain.resume.repository.ResumeRepository;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.openai.dto.response.ResumeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final AiResumeService aiResumeService;
    private final SecurityProvider securityProvider;
    private final ExperienceRepository experienceRepository;
    private final ResumeRepository resumeRepository;


    @Transactional
    public void create(CreateResumeRequestDto createResumeRequestDto) {
        System.out.println("experienceIds = " + createResumeRequestDto.getExperienceIds());
        Member member = securityProvider.getCurrentMember();
        List<Experience> experiences = experienceRepository.findAllByIds(createResumeRequestDto.getExperienceIds(), member);
        Resume resume = Resume.builder()
                .title(createResumeRequestDto.getTitle())
                .question(createResumeRequestDto.getQuestion())
                .limit(createResumeRequestDto.getLimit())
                .experiences(experiences)
                .keywords(createResumeRequestDto.getKeywords())
                .structure(createResumeRequestDto.getStructure())
                .content(createResumeRequestDto.getContent())
                .member(member)
                .build();

        member.getResumes().add(resume);
        resumeRepository.save(resume);
    }

    @Transactional
    public void update(Long resumeId, UpdateResumeRequestDto updateResumeRequestDto) {
        Long memberId = securityProvider.getCurrentMemberId();

        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() ->
                GeneralException.of(ErrorCode.RESUME_NOT_EXISTS));

        if(!resume.getMember().getId().equals(memberId))
            throw new GeneralException(ErrorCode.NOT_YOUR_RESUME);

        resume.updateResume(updateResumeRequestDto);
        resumeRepository.save(resume);
    }

    @Transactional
    public void delete(Long resumeId) {
        Long memberId = securityProvider.getCurrentMemberId();
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() ->
                GeneralException.of(ErrorCode.RESUME_NOT_EXISTS));

        if(!resume.getMember().getId().equals(memberId))
            throw new GeneralException(ErrorCode.NOT_YOUR_RESUME);

        resumeRepository.delete(resume);
    }

    @Transactional(readOnly = true)
    public DetailResumeResponseDto read(Long resumeId) {
        Long memberId = securityProvider.getCurrentMemberId();
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() ->
                GeneralException.of(ErrorCode.RESUME_NOT_EXISTS));

        if(!resume.getMember().getId().equals(memberId))
            throw new GeneralException(ErrorCode.NOT_YOUR_RESUME);

        return DetailResumeResponseDto.of(resume);
    }

    @Transactional(readOnly = true)
    public List<ThumbnailResumeResponseDto> readAll() {
        Member member = securityProvider.getCurrentMember();

        return resumeRepository.findAllByMember(member)
                .stream()
                .map(ThumbnailResumeResponseDto::of)
                .toList();
    }

    public List<RecommendExperienceResponseDto> getRecommendExperience(RecommendExperienceRequestDto requestDto) {
        return aiResumeService.getRecommendExperience(requestDto);
    }

    public ResumeResponseDto createResume(AiResumeRequestDto responseDto) {
        return aiResumeService.createResume(responseDto);
    }
}
