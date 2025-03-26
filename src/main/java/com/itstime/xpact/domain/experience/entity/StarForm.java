package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Entity
@SuperBuilder
@DiscriminatorValue("STAR_FORM")
@AllArgsConstructor
@NoArgsConstructor
public class StarForm extends Experience {

    @Column(name = "situation", nullable = false)
    private String situation;

    @Column(name = "task", nullable = false)
    private String task;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "result", nullable = false)
    private String result;

    // createRequestDto로 StarForm형식의 Experience생성
    public static Experience from(ExperienceCreateRequestDto createRequestDto) {

        // TODO member, experienceCategory 넣어야함
        return StarForm.builder()
                // common 부분
                .status(createRequestDto.getStatus())
                .title(createRequestDto.getTitle())
                .startDate(createRequestDto.getStartDate())
                .endDate(createRequestDto.getEndDate())
                .isEnded(createRequestDto.getEndDate().isBefore(LocalDate.now()))
                // StarForm 부분
                .situation(createRequestDto.getSituation())
                .task(createRequestDto.getTask())
                .action(createRequestDto.getAction())
                .result(createRequestDto.getResult())

                .build();
    }
}
