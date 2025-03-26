package com.itstime.xpact.domain.experience.repository;

import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByMember(Member member);

}
