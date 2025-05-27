package com.itstime.xpact.domain.recruit.service;

import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.util.TrieUtil;
import com.itstime.xpact.domain.recruit.dto.request.DesiredRecruitRequestDto;
import com.itstime.xpact.domain.recruit.dto.response.DesiredRecruitResponseDto;
import com.itstime.xpact.domain.recruit.entity.Recruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.domain.recruit.repository.RecruitRepository;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.infra.lambda.LambdaUtil;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class RecruitService {

    private final SecurityProvider securityProvider;
    private final TrieUtil trieUtil;
    private final LambdaUtil lambdaUtil;

    private final RecruitRepository recruitRepository;
    private final DetailRecruitRepository detailRecruitRepository;


    // 산업 전체 조회
    @Transactional(readOnly = true)
    public List<String> readAllRecruit() throws GeneralException {

        securityProvider.getCurrentMemberId();

        return recruitRepository.findAllNames();
    }

    // 상세 직군 전체 조회
    @Transactional(readOnly = true)
    public List<String> readAllDetailRecruits(String recruitName) throws GeneralException {

        Recruit recruit = recruitRepository.findByName(recruitName)
                .orElseThrow(() -> GeneralException.of(ErrorCode.RECRUIT_NOT_FOUND));
        Long recruitId = recruit.getId();

        return detailRecruitRepository.findDetailRecruitNamesByRecruitId(recruitId);
    }

    // 희망 상세 직군 검색 자동완성
    @Transactional(readOnly = true)
    public List<String> autocompleteDetail(String recruitName, String term) throws GeneralException {

        try {
            securityProvider.getCurrentMemberId();

            Recruit recruit = recruitRepository.findByName(recruitName) // Recruit name Unique
                    .orElseThrow(() -> GeneralException.of(ErrorCode.RECRUIT_NOT_FOUND));

            Long recruitId = recruit.getId();

            List<String> detailRecruitNames = detailRecruitRepository.findDetailRecruitNamesByRecruitId(recruitId);
            trieUtil.loadDatasIntoTrie(detailRecruitNames);

            // prefix와 일치 반환
            List<String> results = trieUtil.autocomplete(term);
            if (results.isEmpty()) {
                throw CustomException.of(ErrorCode.NO_SEARCH_RESULT);
            }
            return results;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("autocompleteDetail error: {}", e.getMessage());
            throw GeneralException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            trieUtil.deleteAutocompleteKeyword(term);
        }
    }

    // 희망 직군 저장
    @Transactional
    public DesiredRecruitResponseDto updateDesiredRecruit(DesiredRecruitRequestDto requestDto) throws GeneralException {

        // 실제 DB에서 영속 상태의 Member 조회
        Member member = securityProvider.getCurrentMember();

        if (requestDto.detailRecruitName() == null || requestDto.detailRecruitName().isBlank()) {
            throw new GeneralException(ErrorCode.EMPTY_DESIRED_RECRUIT);
        }

        member.setDesiredRecruit(requestDto.detailRecruitName());

        return DesiredRecruitResponseDto.builder()
                .recruitName(requestDto.recruitName())
                .detailRecruitName(requestDto.detailRecruitName())
                .build();
    }

    public boolean recruitExists() {
        return recruitRepository.count() > 0;
    }

    public void saveRecruit() {
        List<Recruit> recruits = lambdaUtil.invokeLambda(lambdaUtil.lambdaClient(), "recruitCrawler")
                .stream()
                .map(recruitName -> Recruit.builder()
                        .name(recruitName.getGroupName())
                        .build())
                .toList();

        recruitRepository.saveAll(recruits);
        log.info("Saved {} rows of recruits", recruits.size());
    }
}
