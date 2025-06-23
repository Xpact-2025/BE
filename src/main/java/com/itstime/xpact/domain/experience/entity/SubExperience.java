package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.dto.request.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.entity.embeddable.SimpleForm;
import com.itstime.xpact.domain.experience.entity.embeddable.StarForm;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static com.itstime.xpact.domain.experience.common.ExperienceType.IS_QUALIFICATION;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sub_experience")
public class SubExperience extends BaseEntity {

    @Id
    @Column(name = "sub_experience_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "form_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FormType formType;

    @Column(name = "tab_name")
    private String tabName;

    @Column(name = "sub_title")
    private String subTitle;

    @Embedded
    private StarForm starForm;

    @Embedded
    private SimpleForm simpleForm;

    @Column(name = "simple_description", length = 512)
    private String simpleDescription;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experience_id")
    private Experience experience;

    @Builder.Default
    @OneToMany(mappedBy = "subExperience", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Keyword> keywords = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "subExperience", cascade =  CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    public void setKeywords(List<Keyword> keywords) {
        this.keywords.clear();
        this.keywords.addAll(keywords);
    }

    public void setFiles(List<File> files) {
        this.files.clear();
        this.files.addAll(files);
    }

    public void updateSubFields(ExperienceUpdateRequestDto.SubExperienceRequestDto targetExperience, ExperienceType experienceType) {
        if(IS_QUALIFICATION.contains(experienceType)) {
            this.subTitle = targetExperience.getSubTitle();
            this.formType = FormType.valueOf(targetExperience.getFormType());
            this.simpleDescription = targetExperience.getSimpleDescription();
        } else {
            switch (FormType.valueOf(targetExperience.getFormType())) {
                case STAR_FORM -> {
                    this.subTitle = targetExperience.getSubTitle();
                    this.formType = FormType.valueOf(targetExperience.getFormType());
                    this.starForm = StarForm.builder()
                            .situation(targetExperience.getSituation())
                            .task(targetExperience.getTask())
                            .action(targetExperience.getAction())
                            .result(targetExperience.getResult())
                            .build();
                }
                case SIMPLE_FORM -> {
                    this.subTitle = targetExperience.getSubTitle();
                    this.formType = FormType.valueOf(targetExperience.getFormType());
                    this.simpleForm = SimpleForm.builder()
                            .perform(targetExperience.getPerform())
                            .role(targetExperience.getRole())
                            .build();
                }
            }
        }
    }
}
