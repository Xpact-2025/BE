package com.itstime.xpact.domain.experience.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Builder
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SimpleForm {

    @Column(name = "role", length = 512)
    private String role;
    @Column(name = "perform", length = 512)
    private String perform;
}
