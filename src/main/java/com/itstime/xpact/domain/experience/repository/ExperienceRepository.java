package com.itstime.xpact.domain.experience.repository;

import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long>, ExperienceCustomRepository {

    @Query("SELECT e FROM Experience e JOIN FETCH e.detailRecruit WHERE e.member.id = :memberId AND e.detailRecruit is not null ")
    List<Experience> findAllWithDetailRecruitByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT e FROM Experience e JOIN FETCH e.subExperiences se JOIN FETCH se.keywords WHERE e.member.id = :memberId")
    List<Experience> findAllWithKeywordByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT FUNCTION('DATE_FORMAT', e.createdTime, '%Y-%m-%d') AS DATE, count(*) " +
            "FROM Experience e " +
            "WHERE e.createdTime >= :startDate AND e.createdTime < :endDate AND e.member = :member " +
            "GROUP BY DATE ")
    List<Object[]> countOldByDay(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Member member);

    @Query("SELECT FUNCTION('DATE_FORMAT', e.createdTime, '%Y-%m-%d') AS DATE, count(*) " +
            "FROM Experience e " +
            "WHERE e.createdTime >= :startDate AND e.createdTime < :endDate AND e.member = :member " +
            "GROUP BY DATE ")
    List<Object[]> countByDay(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Member member);

    void deleteAllByMember(Member member);

    @Query("SELECT e FROM Experience e LEFT JOIN FETCH e.subExperiences")
    List<Experience> findAllWithSubExperiences();

    List<Experience> findByMember(Member member);
}
