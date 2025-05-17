package com.itstime.xpact.domain.experience.entity.embeddable;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MetaData {
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "form_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FormType formType;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExperienceType experienceType;

}
