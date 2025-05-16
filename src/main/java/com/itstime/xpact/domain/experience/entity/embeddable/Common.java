package com.itstime.xpact.domain.experience.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Common {

    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "warrant")
    private String warrant;
}
