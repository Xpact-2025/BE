package com.itstime.xpact.domain.guide.repository;

import com.itstime.xpact.domain.guide.entity.Weakness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeaknessRepository extends JpaRepository<Weakness, Long> {

    @Query("SELECT w FROM Weakness w JOIN FETCH w.member m WHERE m.id = :memberId")
    List<Weakness> findByMemberId(@Param("memberId") Long memberId);

    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
