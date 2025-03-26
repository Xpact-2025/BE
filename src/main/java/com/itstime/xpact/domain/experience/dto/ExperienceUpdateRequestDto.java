package com.itstime.xpact.domain.experience.dto;

import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.entity.ExperienceCategory;
import com.itstime.xpact.domain.recruit.entity.ExperienceKeyword;
import lombok.Getter;

import java.util.List;

@Getter
public class ExperienceUpdateRequestDto {

    // common (공통 부분)
    private Status status;
    private List<ExperienceCategory> experienceCategories;
    private FormType formType;
    private String title;
    private String startDate;
    private String endDate;
    private List<ExperienceKeyword> experienceKeywords;

    // STAR_FORM
    private String situation;
    private String task;
    private String action;
    private String result;

    // SIMPLE_FORM
    private String role;
    private String perform;

}
