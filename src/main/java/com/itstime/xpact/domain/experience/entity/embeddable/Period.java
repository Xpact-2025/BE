package com.itstime.xpact.domain.experience.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Period {

    @Column(name = "is_ended", nullable = false)
    private Boolean isEnded;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
}
