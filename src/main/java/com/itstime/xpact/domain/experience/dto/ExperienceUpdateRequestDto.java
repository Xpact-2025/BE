package com.itstime.xpact.domain.experience.dto;

import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ExperienceUpdateRequestDto {

    // common (공통 부분)
    private Status status;
    private List<String> experienceCategories;
    private FormType formType;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;

    // STAR_FORM
    private String situation;
    private String task;
    private String action;
    private String result;

    // SIMPLE_FORM
    private String role;
    private String perform;

}
