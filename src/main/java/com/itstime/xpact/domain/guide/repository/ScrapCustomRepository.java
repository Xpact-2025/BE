package com.itstime.xpact.domain.guide.repository;

import com.itstime.xpact.domain.guide.entity.Scrap;

import java.time.LocalDate;
import java.util.List;

public interface ScrapCustomRepository {

    int saveAllWithIgnore(List<Scrap> scraps);
}
