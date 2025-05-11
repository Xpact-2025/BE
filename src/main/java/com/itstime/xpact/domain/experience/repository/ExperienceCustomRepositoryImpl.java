package com.itstime.xpact.domain.experience.repository;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.entity.QExperience;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExperienceCustomRepositoryImpl implements ExperienceCustomRepository {

    private final JPAQueryFactory queryFactory;

    public List<Experience> findAllByMemberIdAndType(Long memberId, String order, List<ExperienceType> types) {
        QExperience experience = QExperience.experience;

        OrderSpecifier<?> orderSpecifier = null;
        if(order.equals("LATEST")) {
            orderSpecifier = new OrderSpecifier<>(Order.DESC, experience.modifiedTime);
        } else if(order.equals("OLDEST")) {
            orderSpecifier = new OrderSpecifier<>(Order.ASC, experience.modifiedTime);
        }

        BooleanBuilder builder = new BooleanBuilder();
        for (ExperienceType type : types) {
            builder.or(experience.experienceType.eq(type));
        }

        return queryFactory.selectFrom(experience)
                .where(experience.member.id.eq(memberId))
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();
    }

    public List<String> findSummaryByMemberId(Long memberId) {
        QExperience experience = QExperience.experience;

        return queryFactory.select(experience.summary)
                .from(experience)
                .where(experience.status.eq(Status.SAVE)
                        .and(experience.summary.isNotEmpty()))
                .fetch();
    }

}
