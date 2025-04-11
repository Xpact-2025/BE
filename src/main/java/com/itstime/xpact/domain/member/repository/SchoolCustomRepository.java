package com.itstime.xpact.domain.member.repository;

import java.util.List;

public interface SchoolCustomRepository {

    List<String> findAllSchoolNames();
    List<String> findMajorBySchoolName(String schoolName);
    void saveIfNotExist(String schoolName, String major);
}
