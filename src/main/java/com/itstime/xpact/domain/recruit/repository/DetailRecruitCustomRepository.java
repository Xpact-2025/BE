package com.itstime.xpact.domain.recruit.repository;

import com.itstime.xpact.domain.recruit.entity.DetailRecruit;

import java.util.List;

public interface DetailRecruitCustomRepository {
    List<DetailRecruit> findAllByRecruitName(String recruitName);
}
