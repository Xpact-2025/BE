package com.itstime.xpact.domain.experience.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SIMPLE_FORM")
public class SimpleForm extends Experience {

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "perform", nullable = false)
    private String perform;
}
