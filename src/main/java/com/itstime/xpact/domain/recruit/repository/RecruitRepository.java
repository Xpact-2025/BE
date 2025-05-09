package com.itstime.xpact.domain.recruit.repository;

import com.itstime.xpact.domain.recruit.entity.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruitRepository extends JpaRepository<Recruit, Long> {

    @Query("SELECT r FROM Recruit r WHERE r.name = :name")
    Optional<Recruit> findByName(@Param("name") String name);

    @Query("SELECT r.name FROM Recruit r")
    List<String> findAllNames();
}
