package com.itstime.xpact.domain.dashboard.entity;

import com.itstime.xpact.domain.dashboard.dto.response.SkillMapResponseDto;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@RedisHash(value = "ratio", timeToLive = 60)
public class CoreSkillMap {
    @Id
    private Long id;
    private SkillMapResponseDto skillMapDto;
}
