package com.itstime.xpact.domain.member.repository;

import com.itstime.xpact.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m JOIN FETCH m.experiences WHERE m.id = :id")
    Optional<Member> findByIdWithExperiences(@Param("id") Long memberId);
}
