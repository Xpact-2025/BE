package com.itstime.xpact.domain.experience.repository;

import com.itstime.xpact.domain.experience.entity.GroupExperience;
import com.itstime.xpact.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupExperienceRepository extends JpaRepository<GroupExperience, Long> {
    void deleteByMember(Member member);
}
