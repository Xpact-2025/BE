package com.itstime.xpact.domain.guide.service;

import com.itstime.xpact.domain.guide.entity.MemberScrap;
import com.itstime.xpact.domain.guide.entity.Scrap;
import com.itstime.xpact.domain.guide.repository.MemberScrapRepository;
import com.itstime.xpact.domain.guide.repository.ScrapRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScrapService {

    private final MemberScrapRepository memberScrapRepository;
    private final ScrapRepository scrapRepository;
    private final SecurityProvider securityProvider;

    // member가 scrap에 대해 스크랩을 진행하면 MemberScrap 엔티티가 repository에 저장
    // repository에 {member, scrap} 쌍이 이미 존재한다면 DataIntegrityViolationException 발생
    public void onScrap(Long scrapId) {
        Member member = securityProvider.getCurrentMember();
        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(() ->
                GeneralException.of(ErrorCode.SCRAP_NOT_EXISTS));

        MemberScrap memberScrap = MemberScrap.builder().scrap(scrap).member(member).build();

        try {
            memberScrapRepository.save(memberScrap);
        } catch (DataIntegrityViolationException e) {
            throw GeneralException.of(ErrorCode.ALREADY_SCRAPPED);
        }
    }

    // member가 scrap에 대해 스크랩을 진행하면 repository에서 해당 엔티티 삭제
    // 만약 엔티티가 존재하지 않는다면, 예외 throw
    public void offScrap(Long scrapId) {
        Member member = securityProvider.getCurrentMember();
        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(() ->
                GeneralException.of(ErrorCode.SCRAP_NOT_EXISTS));

        MemberScrap memberScrap = memberScrapRepository.findByMemberAndScrap(member, scrap).orElseThrow(() ->
                GeneralException.of(ErrorCode.ALREADY_UNSCRAPPED));

        memberScrapRepository.delete(memberScrap);
    }
}
