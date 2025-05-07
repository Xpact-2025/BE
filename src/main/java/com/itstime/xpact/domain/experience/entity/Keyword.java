package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "name", length = 10, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experience_id")
    private Experience experience;

    public static void validateKeyword(List<String> keywords) {
        if(keywords.size() > 5) {
            throw CustomException.of(ErrorCode.KEYWORD_EXCEEDED);
        }

        keywords.forEach(keyword -> {
            if(keyword.length() > 10) {
                throw CustomException.of(ErrorCode.KEYWORD_TOO_LONG);
            }
        });
    }
}
