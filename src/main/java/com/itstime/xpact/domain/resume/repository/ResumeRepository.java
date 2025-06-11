package com.itstime.xpact.domain.resume.repository;

import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findAllByMember(Member member);
}
