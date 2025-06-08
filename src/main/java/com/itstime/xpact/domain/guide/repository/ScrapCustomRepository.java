package com.itstime.xpact.domain.guide.repository;

import java.time.LocalDate;

public interface ScrapCustomRepository {

    void deleteScrapWithEndDate(LocalDate date);

}
