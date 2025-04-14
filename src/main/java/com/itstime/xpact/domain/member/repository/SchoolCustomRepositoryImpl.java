package com.itstime.xpact.domain.member.repository;

import com.itstime.xpact.domain.member.entity.QSchool;
import com.itstime.xpact.domain.member.entity.School;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public School saveIfNotExist(String schoolName, String major) {

        return findBySchoolNameAndMajor(schoolName, major)
                .orElseGet(() -> schoolRepository.save(
                        School.builder()
                                .schoolName(schoolName)
                                .major(major)
                                .build()
                ));
    }

    @Override
    public Optional<School> findBySchoolNameAndMajor(String schoolName, String major) {
        QSchool school = QSchool.school;

        School result = queryFactory
                .selectFrom(school)
                .where(
                        school.schoolName.eq(schoolName),
                        school.major.eq(major))
                .fetchOne();
        return Optional.ofNullable(result);
    }
}
