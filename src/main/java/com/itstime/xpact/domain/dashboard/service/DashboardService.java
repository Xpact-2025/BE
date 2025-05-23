package com.itstime.xpact.domain.dashboard.service;

import com.itstime.xpact.domain.dashboard.dto.response.*;
import com.itstime.xpact.domain.dashboard.controller.HistoryOldResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.HistoryResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.MapResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.RatioResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.TimelineResponseDto;
import com.itstime.xpact.domain.dashboard.service.ratio.RatioService;
import com.itstime.xpact.domain.dashboard.service.skillmap.SkillmapService;
import com.itstime.xpact.domain.dashboard.service.time.TimeService;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.global.auth.SecurityProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    // 핵심스킬 맵 점수
    public CompletableFuture<MapResponseDto> evaluateScore() {
        Member member = securityProvider.getCurrentMember();
        return skillmapService.performEvaluation(member);
    }

    // 직무 비율
    public RatioResponseDto getRecruitCategoryRatio() {
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

    // 피드백 부분 - 강점
    public CompletableFuture<FeedbackResponseDto> getStrengthFeedback() {

        Member member = securityProvider.getCurrentMember();

        return skillmapService.getFeedbackStrength(member);
    }

    // 피드백 부분 - 단점
    public CompletableFuture<FeedbackResponseDto> getWeaknessFeedback() {

        Member member = securityProvider.getCurrentMember();

        return skillmapService.getFeedbackWeakness(member);
    }
}
