package com.itstime.xpact.domain.member.repository;

import com.itstime.xpact.domain.member.entity.QSchool;
import com.itstime.xpact.domain.member.entity.School;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SchoolCustomRepositoryImpl implements SchoolCustomRepository {

    private final JPAQueryFactory queryFactory;

    private final SchoolRepository schoolRepository;

    @Override
    public List<String> findAllSchoolNames() {
        QSchool school = QSchool.school;

        return queryFactory
                .select(school.schoolName)
                .from(school)
                .fetch();
    }

    @Override
    public List<String> findMajorBySchoolName(String schoolName) {
        QSchool school = QSchool.school;

        return queryFactory
                .select(school.major)
                .from(school)
                .where(school.schoolName.eq(schoolName))
                .fetch();
    }

    @Override
    public void saveIfNotExist(String schoolName, String major) {
        QSchool school = QSchool.school;

        boolean isExists = queryFactory
                .selectOne()
                .from(school)
                .where(school.schoolName.eq(schoolName))
                .fetchFirst() != null;

        if (!isExists) {
            School newSchool = new School();
            newSchool.setSchoolName(schoolName);
            newSchool.setMajor(major);
            schoolRepository.save(newSchool);
        }
    }
}
