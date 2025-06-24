package com.itstime.xpact.domain.guide.entity.embeddable;

import jakarta.persistence.*;
import lombok.Getter;

@Embeddable
@Getter
public class ScrapIntern {

    @Column(name = "enterprise_type")
    private String enterpriseType;
}
