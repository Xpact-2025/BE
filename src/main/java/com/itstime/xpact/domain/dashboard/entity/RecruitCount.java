package com.itstime.xpact.domain.dashboard.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import java.util.Map;

@Getter
@Builder
@RedisHash(value = "ratio", timeToLive = 60)
public class RecruitCount {

    @Id
    private Long id;
    private Map<String, Integer> recruitCount;
}
