package com.itstime.xpact.domain.experience.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SimpleForm {

    @Column(name = "role", length = 1024)
    private String role;
    @Column(name = "perform", length = 1024)
    private String perform;
}
