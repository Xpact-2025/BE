package com.itstime.xpact.domain.guide.entity.embeddable;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Embeddable
public class ScrapEducation {

    @Column(name = "employment")
    private String employment;

    @Column(name = "cost")
    private String cost;

    @Column(name = "on_off_line")
    private String onOffLine;
}
