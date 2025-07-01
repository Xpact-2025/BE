package com.itstime.xpact.domain.guide.repository;

import com.itstime.xpact.domain.guide.entity.MemberScrap;
import com.itstime.xpact.domain.guide.entity.Scrap;
import com.itstime.xpact.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberScrapRepository extends JpaRepository<MemberScrap, Long> {

    Boolean existsByScrapAndMember(Scrap scrap, Member member);
}
