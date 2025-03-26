package com.itstime.xpact.domain.member.repository;

import com.itstime.xpact.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

}
