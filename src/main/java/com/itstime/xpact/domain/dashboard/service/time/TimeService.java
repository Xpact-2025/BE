package com.itstime.xpact.domain.dashboard.service.time;

import com.itstime.xpact.domain.dashboard.dto.response.HistoryResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.TimelineResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeService {

    private final OpenAiService openAiService;

    private final ExperienceRepository experienceRepository;

    @Transactional(readOnly = true)
    public List<TimelineResponseDto> getTimeLine(
            Member member, LocalDate startLine, LocalDate endLine) {
        return member.getExperiences().stream()
                .filter(experience ->
                {
                    LocalDate expStart = experience.getStartDate();
                    LocalDate expEnd = experience.getEndDate();

                    if (expStart == null) return false; // 시작일자 없는 것은 제외

                    return (expEnd == null || !expEnd.isBefore(startLine)) && (!expStart.isAfter(endLine) && (expStart.isAfter(startLine)));
                })
                .sorted(Comparator.comparing(Experience::getStartDate))
                .map(Experience::toTimeLineDto)
                .collect(Collectors.toList());
    }

    public void checkSummaryOfExperience(Long memberId) {
        List<Experience> experiences = experienceRepository.findAllWithKeywordByMemberId(memberId);

        experiences.stream()
                .filter(e -> e.getSummary() == null || e.getSummary().isEmpty())
                .forEach(openAiService::summarizeExperience);
    }

    public void checkDetailRecruitOfExperience(Long memberId) {
        List<Experience> experiences = experienceRepository.findAllWithKeywordByMemberId(memberId);
        experiences.stream()
                .filter(e -> e.getDetailRecruit() == null)
                .forEach(openAiService::getDetailRecruitFromExperience);
    }

    public HistoryResponseDto getCountPerDay(int year, int month, Member member) {
        validateDate(year, month);
        List<HistoryResponseDto.DateCount> results = experienceRepository.countByDay(year, month, member).stream()
                .map(object ->
                        HistoryResponseDto.DateCount.builder()
                                .date(object[0].toString())
                                .count(Integer.parseInt(object[1].toString()))
                                .build())
                .toList();

        return HistoryResponseDto.of(results);
    }

    private void validateDate(int year, int month) {
        if(year < 1 || month < 1 || month > 12) throw CustomException.of(ErrorCode.INVALID_DATE);
    }
}
