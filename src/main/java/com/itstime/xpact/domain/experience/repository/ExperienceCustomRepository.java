package com.itstime.xpact.domain.experience.repository;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.member.entity.Member;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ExperienceCustomRepository {

    List<Experience> findAllByMemberAndType(Member member, String order, List<ExperienceType> types);

    List<String> findSummaryByMemberId(Long memberId);

    List<Experience> queryExperience(String query);

    List<Experience> findAllByMember(Member member, Sort sort);
}
