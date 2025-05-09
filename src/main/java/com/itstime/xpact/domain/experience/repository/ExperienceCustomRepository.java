package com.itstime.xpact.domain.experience.repository;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.entity.Experience;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ExperienceCustomRepository {

    List<Experience> findAllByMemberIdAndType(Long memberId, String order, List<ExperienceType> types);
}
