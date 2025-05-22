package com.itstime.xpact.domain.dashboard.service.skillmap;

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

    public void saveStrength(Long memberId, String strength) {
        String key = "coreStrength:" + memberId;
        redisTemplate.opsForValue().set(key, strength);
    }

    public void saveWeakness(Long memberId, String weakness) {
        String key = "coreWeakness:" + memberId;
        redisTemplate.opsForValue().set(key, weakness);
    }

    public String getStrength(Long memberId) {
        String key = "coreStrength:" + memberId;
        return (String) redisTemplate.opsForValue().get(key);
    }

    public String getWeakness(Long memberId) {
        String key = "coreWeakness:" + memberId;
        return (String) redisTemplate.opsForValue().get(key);
    }
}
