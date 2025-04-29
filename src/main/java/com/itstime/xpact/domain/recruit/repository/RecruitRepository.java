package com.itstime.xpact.domain.recruit.repository;

import com.itstime.xpact.domain.recruit.entity.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruitRepository extends JpaRepository<Recruit, Long> {

    Optional<Recruit> findByName(String name);
}
