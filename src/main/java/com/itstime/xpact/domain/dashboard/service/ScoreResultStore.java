package com.itstime.xpact.domain.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoreResultStore {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(Long memberId, String result) {
        String key = "coreSkillMap:" + memberId;
        redisTemplate.opsForValue().set(key, result);
    }

    public String get(Long memberId) {
        String key = "coreSkillMap:" + memberId;
        return (String) redisTemplate.opsForValue().get(key);
    }
}
