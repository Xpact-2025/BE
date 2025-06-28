package com.itstime.xpact.domain.guide.repository;

import com.itstime.xpact.domain.guide.entity.Scrap;

import java.time.LocalDate;
import java.util.List;

public interface ScrapCustomRepository {

    void deleteScrapWithEndDate(LocalDate date);

    int saveAllWithIgnore(List<Scrap> scraps);
}
