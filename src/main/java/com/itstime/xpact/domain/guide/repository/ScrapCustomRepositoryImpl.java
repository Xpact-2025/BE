package com.itstime.xpact.domain.guide.repository;

import com.itstime.xpact.domain.guide.entity.QScrap;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class ScrapCustomRepositoryImpl implements ScrapCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void deleteScrapWithEndDate(LocalDate date) {
        QScrap scrap = QScrap.scrap;

        queryFactory
                .delete(scrap)
                .where(scrap.endDate.lt(date)) // WHERE end_date < :date
                .execute();

        entityManager.flush();
        entityManager.clear();
    }
}
