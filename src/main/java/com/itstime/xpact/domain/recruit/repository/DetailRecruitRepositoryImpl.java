package com.itstime.xpact.domain.recruit.repository;

import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.entity.QDetailRecruit;
import com.itstime.xpact.domain.recruit.entity.QRecruit;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DetailRecruitRepositoryImpl implements DetailRecruitCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<DetailRecruit> findAllByRecruitName(String recruitName) {
        QDetailRecruit detailRecruit = QDetailRecruit.detailRecruit;
        QRecruit recruit = QRecruit.recruit;

        return jpaQueryFactory
                .selectFrom(detailRecruit)
                .join(detailRecruit.recruit, recruit)
                .where(recruit.name.eq(recruitName))
                .fetch();
    }
}
