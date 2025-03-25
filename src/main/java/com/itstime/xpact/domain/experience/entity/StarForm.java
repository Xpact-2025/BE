package com.itstime.xpact.domain.experience.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STAR_FORM")
public class StarForm extends Experience {

    @Column(name = "situation", nullable = false)
    private String situation;

    @Column(name = "task", nullable = false)
    private String task;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "result", nullable = false)
    private String result;
}
