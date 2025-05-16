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

    @Column(name = "situation")
    private String situation;
    @Column(name = "task")
    private String task;
    @Column(name = "action")
    private String action;
    @Column(name = "result")
    private String result;
}
