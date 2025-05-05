package com.itstime.xpact.domain.recruit.repository;

import com.itstime.xpact.domain.recruit.entity.CoreSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoreSkillRepository extends JpaRepository<CoreSkill, Long> {
}
