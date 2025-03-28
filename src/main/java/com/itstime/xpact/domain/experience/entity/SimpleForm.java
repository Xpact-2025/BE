package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Entity
@SuperBuilder
@DiscriminatorValue("SIMPLE_FORM")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SimpleForm extends Experience {

    @Column(name = "role")
    private String role;

    @Column(name = "perform")
    private String perform;

    // createRequestDto로 SimpleForm형식의 Experience생성
    public static Experience from(ExperienceCreateRequestDto createRequestDto) {
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

    public static Experience from(ExperienceUpdateRequestDto updateRequestDto) {
        return SimpleForm.builder()
                // common 부분
                .status(updateRequestDto.getStatus())
                .title(updateRequestDto.getTitle())
                .startDate(updateRequestDto.getStartDate())
                .endDate(updateRequestDto.getEndDate())
                .isEnded(updateRequestDto.getEndDate().isBefore(LocalDate.now()))
                // SimpleForm 부분
                .role(updateRequestDto.getRole())
                .perform(updateRequestDto.getPerform())

                .build();
    }

    /**
     * SimpleForm형식에 맞게 update 진행 (유형이 바뀌지 않은 experience 객체일 때 사용)
     */
    @Override
    public void update(ExperienceUpdateRequestDto dto) {
        this.status = dto.getStatus();
        this.title = dto.getTitle();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.isEnded = dto.getEndDate().isBefore(LocalDate.now());

        this.perform = dto.getPerform();
        this.role = dto.getRole();
    }
}
