package com.itstime.xpact.domain.dashboard.service;

import com.itstime.xpact.domain.dashboard.dto.response.*;
import com.itstime.xpact.domain.dashboard.service.ratio.RatioService;
import com.itstime.xpact.domain.dashboard.service.skillmap.SkillmapService;
import com.itstime.xpact.domain.dashboard.service.summary.SummaryService;
import com.itstime.xpact.domain.dashboard.service.time.TimeService;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final SecurityProvider securityProvider;
    private final MemberRepository memberRepository;

    private final SkillmapService skillmapService;
    private final RatioService ratioService;
    private final TimeService timeService;
    private final SummaryService summaryService;

    // 핵심스킬 맵 점수
    public Long startSkillEvaluation() {
        Member member = securityProvider.getCurrentMember();
        return skillmapService.evaluateAsync(member);
    }

    public Optional<MapResponseDto> getEvaluationResult(Long memberId) {
        return skillmapService.getEvaluationResult(memberId);
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
    public HistoryResponseDto getExperienceHistory(int year, int month) {
        return timeService.getCountPerDay(year, month);
    }

    // 타임라인 조회
    @Transactional(readOnly = true)
    public List<TimelineResponseDto> getExperienceTimeline(
            LocalDate startLine, LocalDate endLine) {

        Long memberId = securityProvider.getCurrentMemberId();

        // Fetch Join을 통한 LazyInitializationException 방지
        Member member = memberRepository.findByIdWithExperiences(memberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

        return timeService.getTimeLine(member, startLine, endLine);
    }

    public Slice<SummaryResponseDto> getSliceSummary(int page, int size) {
        Member member = securityProvider.getCurrentMember();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
        return summaryService.getSliceSummary(member, pageable);
    }
}
