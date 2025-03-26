package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Entity
@SuperBuilder
@DiscriminatorValue("SIMPLE_FORM")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SimpleForm extends Experience {

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "perform", nullable = false)
    private String perform;

    // createRequestDto로 SimpleForm형식의 Experience생성
    public static Experience from(ExperienceCreateRequestDto createRequestDto) {

        // TODO member, experienceCategory 넣어야함
        return SimpleForm.builder()
                // common 부분
                .status(createRequestDto.getStatus())
                .title(createRequestDto.getTitle())
                .startDate(createRequestDto.getStartDate())
                .endDate(createRequestDto.getEndDate())
                .isEnded(createRequestDto.getEndDate().isBefore(LocalDate.now()))
                // SimpleForm 부분
                .role(createRequestDto.getRole())
                .perform(createRequestDto.getPerform())

                .build();
    }
}
