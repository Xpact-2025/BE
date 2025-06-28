package com.itstime.xpact.domain.guide.repository;

import com.itstime.xpact.domain.guide.entity.QScrap;
import com.itstime.xpact.domain.guide.entity.Scrap;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScrapCustomRepositoryImpl implements ScrapCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void deleteScrapWithEndDate(LocalDate date) {
//        QScrap scrap = QScrap.scrap;
//
//        queryFactory
//                .delete(scrap)
//                .where(scrap.endDate.lt(date)) // WHERE end_date < :date
//                .execute();
//
//        entityManager.flush();
//        entityManager.clear();
    }

    @Transactional
    public int saveAllWithIgnore(List<Scrap> scraps) {
        String sql = """
                INSERT IGNORE
                INTO scrap (linkareer_id, scrap_type, title, organizer_name, start_date, end_date, job_category, homepage_url, img_url, benefits, eligibility, on_off_line, enterprise_type, region)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,?)
                """;

        int[] result = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Scrap scrap = scraps.get(i);
                ps.setLong(1, scrap.getLinkareerId());
                ps.setString(2, scrap.getScrapType().name());
                ps.setString(3, scrap.getTitle());
                ps.setString(4, scrap.getOrganizerName());
                ps.setString(5, scrap.getStartDate());
                ps.setString(6, scrap.getEndDate());
                ps.setString(7, scrap.getJobCategory());
                ps.setString(8, scrap.getHomepageUrl());
                ps.setString(9, scrap.getImgUrl());
                ps.setString(10, scrap.getBenefits());
                ps.setString(11, scrap.getEligibility());
                ps.setString(12, scrap.getOnOffLine());
                ps.setString(13, scrap.getEnterpriseType());
                ps.setString(14, scrap.getRegion());
            }

            @Override
            public int getBatchSize() {
                return scraps.size();
            }
        });

        return Arrays.stream(result).sum();
    }
}
