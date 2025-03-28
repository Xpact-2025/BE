package com.itstime.xpact.domain.experience.repository;

import com.itstime.xpact.domain.experience.entity.ExperienceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperienceCategoryRepository extends JpaRepository<ExperienceCategory, Long> {

}
