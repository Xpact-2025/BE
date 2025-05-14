package com.itstime.xpact.domain.recruit.repository;

import com.itstime.xpact.domain.recruit.entity.CoreSkill;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetailRecruitRepository extends JpaRepository<DetailRecruit, Long>, DetailRecruitCustomRepository {

    @Query("SELECT dr FROM DetailRecruit dr WHERE dr.name = :name")
    Optional<DetailRecruit> findByName(@Param("name") String name);

    @Query("SELECT dr FROM DetailRecruit dr JOIN FETCH dr.recruit")
    List<DetailRecruit> findAllWithRecruit();

    @Query("SELECT d.name FROM DetailRecruit d WHERE d.recruit.id = :recruitId")
    List<String> findDetailRecruitNamesByRecruitId(@Param("recruitId") Long recruitId);

    @Query("SELECT dr.coreSkill FROM DetailRecruit dr WHERE dr.id = :detailRecruitId")
    Optional<CoreSkill> findCoreSkillById(@Param("detailRecruitId") Long detailRecruitId);


}
