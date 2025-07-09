package com.itstime.xpact.domain.dashboard.service;

import com.itstime.xpact.domain.dashboard.dto.response.*;
import com.itstime.xpact.domain.dashboard.service.ratio.RatioService;
import com.itstime.xpact.domain.dashboard.service.skillmap.SkillmapService;
import com.itstime.xpact.domain.dashboard.service.time.TimeService;
import com.itstime.xpact.domain.guide.entity.MemberScrap;
import com.itstime.xpact.domain.guide.repository.MemberScrapRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.global.auth.SecurityProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final SecurityProvider securityProvider;

    private final SkillmapService skillmapService;
    private final RatioService ratioService;
    private final TimeService timeService;

    private final MemberScrapRepository memberScrapRepository;

    // 핵심스킬 맵
    public CompletableFuture<?> evaluate() {
        Member member = securityProvider.getCurrentMember();
        return skillmapService.evaluate(member);
    }

    // 직무 비율
    public List<RatioResponseDto> getRecruitCategoryRatio() {
        Long memberId = securityProvider.getCurrentMemberId();
        return ratioService.detailRecruitRatio(memberId);
    }

    // 데이터 Refresh
    public void refreshData() {
        timeService.checkSummaryOfExperience(securityProvider.getCurrentMemberId());
        timeService.checkDetailRecruitOfExperience(securityProvider.getCurrentMemberId());
    }

    // 히스토리 조회
    public HistoryOldResponseDto getOldExperienceHistory(int year, int month) {
        Member member = securityProvider.getCurrentMember();
        return timeService.getOldCountPerDay(year, month, member);
    }

    public HistoryResponseDto getExperienceHistory(int year, int month) {
        Member member = securityProvider.getCurrentMember();
        return timeService.getCountPerDay(year, month, member);
    }


    // 타임라인 조회
    @Transactional(readOnly = true)
    public List<TimelineResponseDto> getExperienceTimeline(
            LocalDate startLine, LocalDate endLine) {

        // Fetch Join을 통한 LazyInitializationException 방지
        Member member = securityProvider.getCurrentMember();

        return timeService.getTimeLine(member, startLine, endLine);
    }

    @Transactional(readOnly = true)
    public List<ScrapResponseDto> getScrapList() {
        Member member = securityProvider.getCurrentMember();

        return memberScrapRepository.findAllByMember(member).stream()
                .map(MemberScrap::getScrap).toList().stream()
                .filter(scrap -> canParseToLocalDate(scrap.getEndDate()))
                .map(scrap -> {
                    String dDay = getDday(scrap.getEndDate());
                    return ScrapResponseDto.of(scrap, dDay);
                }).toList();
    }

    private boolean canParseToLocalDate(String date) {
        try {
            LocalDate parse = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            return parse.isBefore(LocalDate.now());
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    }

    private String getDday(String endDate) {
        try {
            LocalDate localDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            int diff = (int) ChronoUnit.DAYS.between(localDate, LocalDate.now());

            if(diff < 0) return "D-";
            else return "D-" + diff;
        } catch (DateTimeParseException | NullPointerException e) {
            return "D-";
        }
    }

}
