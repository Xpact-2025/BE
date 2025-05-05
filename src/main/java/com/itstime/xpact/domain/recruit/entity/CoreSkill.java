package com.itstime.xpact.domain.recruit.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Table(name = "core_skill")
@NoArgsConstructor
public class CoreSkill extends BaseEntity {

    @Id
    @Column(name = "core_skill_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skill_name_1")
    private String skillName1;

    @Column(name = "skill_name_2")
    private String skillName2;

    @Column(name = "skill_name_3")
    private String skillName3;

    @Column(name = "skill_name_4")
    private String skillName4;

    @Column(name = "skill_name_5")
    private String skillName5;

    public CoreSkill(List<String> skills) {
        this.skillName1 = skills.get(0);
        this.skillName2 = skills.get(1);
        this.skillName3 = skills.get(2);
        this.skillName4 = skills.get(3);
        this.skillName5 = skills.get(4);
    }

    public List<String> getCoreSKills() {
        return List.of(skillName1, skillName2, skillName3, skillName4, skillName5);
    }

    @Override
    public String toString() {
        return this.getCoreSKills().toString();
    }
}
