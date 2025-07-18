package com.itstime.xpact.domain.dashboard.service.time;

import com.itstime.xpact.domain.dashboard.dto.response.HistoryOldResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.HistoryResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.TimelineResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeService {

    private final OpenAiService openAiService;

    private final ExperienceRepository experienceRepository;

    @Transactional(readOnly = true)
    public List<TimelineResponseDto> getTimeLine(
            Member member, LocalDate startLine, LocalDate endLine) {

        // member -> experience 가 아닌, member -> groupExperience -> experience의 흐름이므로
        // member에 대한 experiences를 조회하는 로직 수정
        return member.getExperiences().stream()
                .filter(experience ->
                {
                    LocalDate expStart = experience.getStartDate();
                    LocalDate expEnd = experience.getEndDate();

                    if (expStart == null) return false; // 시작일자 없는 것은 제외

                    LocalDate effectedEnd = expEnd != null ? expEnd : LocalDate.MAX; // 진행 중일 경우 고려
                    return !expStart.isAfter(endLine) && !effectedEnd.isBefore(startLine);
                })
                .sorted(Comparator.comparing(Experience::getStartDate))
                .map(Experience::toTimeLineDto)
                .collect(Collectors.toList());
    }

    public void checkSummaryOfExperience(Long memberId) {
        List<Experience> experiences = experienceRepository.findAllWithKeywordByMemberId(memberId);

        experiences.stream()
                .filter(e -> e.getSummary() == null || e.getSummary().isEmpty())
                .forEach(e -> openAiService.summarizeExperience(e, e.getSubExperiences()));
    }

    public void checkDetailRecruitOfExperience(Long memberId) {
        List<Experience> experiences = experienceRepository.findAllWithKeywordByMemberId(memberId);
        experiences.stream()
                .filter(e -> e.getDetailRecruit() == null)
                .forEach(openAiService::getDetailRecruitFromExperience);
    }

    public HistoryOldResponseDto getOldCountPerDay(int year, int month, Member member) {
        validateDate(year, month);
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month + 1, 1, 0, 0, 0);

        List<HistoryOldResponseDto.DateCount> results = experienceRepository.countOldByDay(startDate, endDate, member).stream()
                .map(object ->
                        HistoryOldResponseDto.DateCount.builder()
                                .date(object[0].toString())
                                .count(Integer.parseInt(object[1].toString()))
                                .build())
                .toList();

        return HistoryOldResponseDto.of(results);
    }

    public HistoryResponseDto getCountPerDay(int year, int month, Member member) {
        validateDate(year, month);
        LocalDateTime now = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime startDate = now.withDayOfMonth(1).minusMonths(1);
        LocalDateTime endDate = now.withDayOfMonth(1).plusMonths(2);

        // 현월일 때
        if(isNow(year, month)) {
            startDate = now.withDayOfMonth(1).minusMonths(2);
            endDate = now.withDayOfMonth(1).plusMonths(1);
        }

        List<HistoryResponseDto.DateCount> results = experienceRepository.countByDay(startDate, endDate, member).stream()
                .map(object ->
                        HistoryResponseDto.DateCount.builder()
                                .date(object[0].toString())
                                .count(Integer.parseInt(object[1].toString()))
                                .build())
                .toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<Integer, List<HistoryResponseDto.DateCount>> groupByMonth = results.stream()
                .collect(Collectors.groupingBy(dateCount ->
                        YearMonth.from(LocalDate.parse(dateCount.getDate(), formatter)).getMonthValue()));

        return HistoryResponseDto.of(groupByMonth);
    }

    private boolean isNow(int year, int month) {
        return (LocalDate.now().getYear() == year && LocalDate.now().getMonth().getValue() == month);
    }

    private void validateDate(int year, int month) {
        if(year < 1 || month < 1 || month > 12) throw GeneralException.of(ErrorCode.INVALID_DATE);
    }
}
