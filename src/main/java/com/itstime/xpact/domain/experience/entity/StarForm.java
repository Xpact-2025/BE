package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StarForm extends Experience {

    @Column(name = "situation")
    private String situation;

    @Column(name = "task")
    private String task;

    @Column(name = "action")
    private String action;

    @Column(name = "result")
    private String result;

    // createRequestDto로 StarForm형식의 Experience생성
    public static Experience from(ExperienceCreateRequestDto createRequestDto) {
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

    public static Experience from(ExperienceUpdateRequestDto updateRequestDto ) {
        return StarForm.builder()
                // common 부분
                .status(updateRequestDto.getStatus())
                .title(updateRequestDto.getTitle())
                .startDate(updateRequestDto.getStartDate())
                .endDate(updateRequestDto.getEndDate())
                .isEnded(updateRequestDto.getEndDate().isBefore(LocalDate.now()))
                // StarForm 부분
                .situation(updateRequestDto.getSituation())
                .task(updateRequestDto.getTask())
                .action(updateRequestDto.getAction())
                .result(updateRequestDto.getResult())

                .build();
    }


    /**
     * StarForm형식에 맞게 update 진행 (유형이 바뀌지 않은 experience 객체일 때 사용)
     */
    @Override
    public void update(ExperienceUpdateRequestDto dto) {
        this.status = dto.getStatus();
        this.title = dto.getTitle();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.isEnded = dto.getEndDate().isBefore(LocalDate.now());

        this.situation = dto.getSituation();
        this.task = dto.getTask();
        this.action = dto.getAction();
        this.result = dto.getResult();
    }
}
