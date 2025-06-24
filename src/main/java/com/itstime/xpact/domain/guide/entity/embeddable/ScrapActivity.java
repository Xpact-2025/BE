package com.itstime.xpact.domain.guide.entity.embeddable;

import jakarta.persistence.*;
import lombok.Getter;

@Embeddable
@Getter
public class ScrapActivity {

    @Column(name = "preferred_skills")
    private String preferredSkills;

    @Column(name = "participant")
    private String participant;
}
