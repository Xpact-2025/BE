package com.itstime.xpact.domain.member.repository;

import com.itstime.xpact.domain.member.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EducationRepository extends JpaRepository<Education, Long> {

    Optional<Education> findByMemberId(Long memberId);
}
