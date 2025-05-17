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
public class StarForm {

    @Column(name = "situation", length = 512)
    private String situation;
    @Column(name = "task", length = 512)
    private String task;
    @Column(name = "action", length = 512)
    private String action;
    @Column(name = "result", length = 512)
    private String result;
}
