package com.itstime.xpact.domain.guide.repository;

import com.itstime.xpact.domain.guide.entity.MemberScrap;
import com.itstime.xpact.domain.guide.entity.Scrap;
import com.itstime.xpact.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberScrapRepository extends JpaRepository<MemberScrap, Long> {

    Optional<MemberScrap> findByMemberAndScrap(Member member, Scrap scrap);

    @Query("SELECT ms FROM MemberScrap ms WHERE ms.member = :member AND ms.scrap.id IN :scrapIdList ")
    List<MemberScrap> findByMemberAndScrapIds(Member member, List<Long> scrapIdList);

    List<MemberScrap> findAllByMember(Member member);
}
