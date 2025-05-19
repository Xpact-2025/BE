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
public class Qualification {

    @Column(name = "qualification")
    private String qualification;
    @Column(name = "publisher")
    private String publisher;
    @Column(name = "simple_description", length = 512)
    private String simpleDescription;
}
