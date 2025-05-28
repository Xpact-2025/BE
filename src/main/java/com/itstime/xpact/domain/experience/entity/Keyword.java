package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Entity
@Builder
@Table(name = "keyword")
@NoArgsConstructor
@AllArgsConstructor
public class Keyword extends BaseEntity {

    @Id
    @Column(name = "keyword_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_experience_id")
    private SubExperience subExperience;

    public static void validateKeyword(List<String> keywords) {
        if(keywords == null || keywords.isEmpty()) return;

        if(keywords.size() > 5) {
            throw GeneralException.of(ErrorCode.KEYWORD_EXCEEDED);
        }

        keywords.forEach(keyword -> {
            if(keyword.length() > 20) {
                throw GeneralException.of(ErrorCode.KEYWORD_TOO_LONG);
            }
        });
    }
}
