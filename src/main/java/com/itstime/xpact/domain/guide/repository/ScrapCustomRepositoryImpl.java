package com.itstime.xpact.domain.guide.repository;

import com.itstime.xpact.domain.guide.entity.QScrap;
import com.itstime.xpact.domain.guide.entity.Scrap;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScrapCustomRepositoryImpl implements ScrapCustomRepository {

    private final JdbcTemplate jdbcTemplate;

    private final JPAQueryFactory jpaQueryFactory;
    private final QScrap scrap = QScrap.scrap;

    @Transactional
    public int saveAllWithIgnore(List<Scrap> scraps) {
        String sql = """
                INSERT IGNORE
                INTO scrap (linkareer_id, scrap_type, title, organizer_name, start_date, end_date, job_category, homepage_url, img_url, benefits, eligibility, on_off_line, enterprise_type, region, created_time, modified_time)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,?, ?, ?)
                """;

        int[] result = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());

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
                ps.setTimestamp(15, now);
                ps.setTimestamp(16, now);
            }

            @Override
            public int getBatchSize() {
                return scraps.size();
            }
        });

        return Arrays.stream(result).sum();
    }

    @Override
    public Slice<Scrap> findByTitleContainingKeywords(List<String> keywords, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        for (String keyword: keywords) {
            builder.or(scrap.title.containsIgnoreCase(keyword)); // or로 중복 방지
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate now = LocalDate.now();

        builder.and(scrap.endDate.gt(formatter.format(now)))
                .or(scrap.endDate.contains("마감"))
                .or(scrap.endDate.contains("채용"));

        JPAQuery<Scrap> query = jpaQueryFactory
                .selectFrom(scrap)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1); // limit + 1 for slice

        List<Scrap> results = query.fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(results.size() -1);
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
