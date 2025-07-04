package com.itstime.xpact.domain.guide.repository;

import com.itstime.xpact.domain.guide.entity.Scrap;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ScrapCustomRepository {

    int saveAllWithIgnore(List<Scrap> scraps);
    Slice<Scrap> findByTitleContainingKeywords(List<String> keywords, Pageable pageable);
}
