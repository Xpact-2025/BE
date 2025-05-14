package com.itstime.xpact.domain.recruit.service;

import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.domain.member.util.TrieUtil;
import com.itstime.xpact.domain.recruit.dto.request.DesiredRecruitRequestDto;
import com.itstime.xpact.domain.recruit.dto.response.DesiredRecruitResponseDto;
import com.itstime.xpact.domain.recruit.entity.Recruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.domain.recruit.repository.RecruitRepository;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.infra.lambda.LambdaUtil;
import com.itstime.xpact.global.exception.CustomException;
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
    private final MemberRepository memberRepository;


    // 산업 전체 조회
    @Transactional(readOnly = true)
    public List<String> readAllRecruit() throws CustomException {

        securityProvider.getCurrentMemberId();

        return recruitRepository.findAllNames();
    }

    // 희망 산업 분야 검색 자동완성
    @Transactional(readOnly = true)
    public List<String> autocompleteName(String term) throws CustomException {

        try {
            securityProvider.getCurrentMemberId();

            // Trie에 검색어 입력 내용 넣기
            trieUtil.addAutocompleteKeyword(term);

            // DB에 있는 Recruit name 전체 load
            List<String> recruitNames = recruitRepository.findAllNames();
            trieUtil.loadDatasIntoTrie(recruitNames);

            // 일치 반환
            return trieUtil.autocomplete(term);
        } catch (Exception e) {
            log.error("autocompleteName error: {}", e.getMessage());
            throw CustomException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            trieUtil.deleteAutocompleteKeyword(term);
        }
    }

    // 상세 직군 전체 조회
    @Transactional(readOnly = true)
    public List<String> readAllDetailRecruits(String recruitName) throws CustomException {

        Recruit recruit = recruitRepository.findByName(recruitName)
                .orElseThrow(() -> CustomException.of(ErrorCode.RECRUIT_NOT_FOUND));
        Long recruitId = recruit.getId();

        return detailRecruitRepository.findDetailRecruitNamesByRecruitId(recruitId);
    }

    // 희망 상세 직군 검색 자동완성
    @Transactional(readOnly = true)
    public List<String> autocompleteDetail(String recruitName, String term) throws CustomException {

        try {
            securityProvider.getCurrentMemberId();

            trieUtil.addAutocompleteKeyword(term);

            Recruit recruit = recruitRepository.findByName(recruitName) // Recruit name Unique
                    .orElseThrow(() -> CustomException.of(ErrorCode.RECRUIT_NOT_FOUND));

            Long recruitId = recruit.getId();

            List<String> detailRecruitNames = detailRecruitRepository.findDetailRecruitNamesByRecruitId(recruitId);
            trieUtil.loadDatasIntoTrie(detailRecruitNames);

            return trieUtil.autocomplete(term);
        } catch (Exception e) {
            log.error("autocompleteDetail error: {}", e.getMessage());
            throw CustomException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            trieUtil.deleteAutocompleteKeyword(term);
        }
    }

    // 희망 직군 저장
    @Transactional
    public DesiredRecruitResponseDto updateDesiredRecruit(DesiredRecruitRequestDto requestDto) throws CustomException {

        Long memberId = securityProvider.getCurrentMemberId();

        // 실제 DB에서 영속 상태의 Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_EXISTS));

        if (requestDto.detailRecruitName() == null || requestDto.detailRecruitName().isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_DESIRED_RECRUIT);
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
