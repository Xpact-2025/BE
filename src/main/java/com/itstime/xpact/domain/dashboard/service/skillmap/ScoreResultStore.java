package com.itstime.xpact.domain.dashboard.service.skillmap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreResultStore {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveSkillMap(Long memberId, String result) {
        String key = "coreSkillMap:" + memberId;
        redisTemplate.opsForValue().set(key, result);
    }

    public String getSkillMap(Long memberId) {
        String key = "coreSkillMap:" + memberId;
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void saveStrengthFeedback(Long memberId, String result) {
        String key = "strengthFeedback:" + memberId;
        redisTemplate.opsForValue().set(key, result);
    }

    public void saveWeaknessFeedback(Long memberId, String result) {
        String key = "weaknessFeedback:" + memberId;
        redisTemplate.opsForValue().set(key, result);
    }
}
