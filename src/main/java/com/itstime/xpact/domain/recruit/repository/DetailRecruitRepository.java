package com.itstime.xpact.domain.recruit.repository;

import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailRecruitRepository extends JpaRepository<DetailRecruit, Long> {

    @Query("SELECT dr FROM DetailRecruit dr JOIN FETCH dr.recruit")
    List<DetailRecruit> findAllWithRecruit();

    @Query("SELECT d.name FROM DetailRecruit d WHERE d.recruit.id = :recruitId")
    List<String> findAllByRecruitId(@Param("recruitId") Long recruitId);

    @Query("SELECT d.name FROM DetailRecruit d WHERE d.recruit.id = :recruitId")
    List<String> findDetailRecruitNamesByRecruitId(@Param("recruitId") Long recruitId);
}
