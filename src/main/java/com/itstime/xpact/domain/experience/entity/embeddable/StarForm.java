package com.itstime.xpact.domain.experience.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class StarForm {

    @Column(name = "situation", length = 512)
    private String situation;
    @Column(name = "task", length = 512)
    private String task;
    @Column(name = "action", length = 512)
    private String action;
    @Column(name = "result", length = 512)
    private String result;

    @Override
    public String toString() {
        return "starForm{" +
                "situation='" + situation + '\'' +
                ", task='" + task + '\'' +
                ", action='" + action + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
