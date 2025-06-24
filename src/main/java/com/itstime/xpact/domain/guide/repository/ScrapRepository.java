package com.itstime.xpact.domain.guide.repository;

import com.itstime.xpact.domain.guide.entity.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long>, ScrapCustomRepository {

}
