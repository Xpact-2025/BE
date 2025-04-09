package com.itstime.xpact.domain.recruit.repository;

import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailRecruitRepository extends JpaRepository<DetailRecruit, Long> {
}
