package com.itstime.xpact.domain.experience.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SimpleForm {

    @Column(name = "role", length = 512)
    private String role;
    @Column(name = "perform", length = 512)
    private String perform;

    @Override
    public String toString() {
        return "SimpleForm{" +
                "role='" + role + '\'' +
                ", perform='" + perform + '\'' +
                '}';
    }
}
