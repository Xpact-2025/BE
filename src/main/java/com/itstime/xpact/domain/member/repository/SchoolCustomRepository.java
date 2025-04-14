package com.itstime.xpact.domain.member.repository;

import com.itstime.xpact.domain.member.entity.School;

import java.util.List;
import java.util.Optional;

public interface SchoolCustomRepository {

    List<String> findAllSchoolNames();
    List<String> findMajorBySchoolName(String schoolName);
    School saveIfNotExist(String schoolName, String major);
    Optional<School> findBySchoolNameAndMajor(String schoolName, String major);
}
