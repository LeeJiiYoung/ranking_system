package com.ranking.rank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ranking.cache.RankingItem;
import com.ranking.cache.RankingSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RankingCacheService {
    static final String SNAPSHOT_KEY = "ranking:top:snapshot";
    static final String LOCK_KEY = "ranking:top:lock";
    static final Duration LOGICAL_TTL = Duration.ofSeconds(10);
    static final Duration LOCK_TTL = Duration.ofSeconds(5);
    static final Duration PHYSICAL_TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final RankingRefresher rankingRefresher;

    /**
     * 상위 랭킹 상품 조회
     * @return
     */
    public List<RankingItem> getTopRanking() {
        RankingSnapshot cached = readSnapshot();

        if (cached == null) {
            return rankingRefresher.refreshNow().items();
        }
        if (!cached.isExpired()) {
            return cached.items();
        }

        String lockToken = UUID.randomUUID().toString();
        //LOCK_KEY 키를 잡아보고 성공하면 true
        boolean lockAcquired = Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, lockToken, LOCK_TTL)
        );

        if (lockAcquired) {
            rankingRefresher.refreshAsync(lockToken);
        }
        return cached.items();
    }

    /**
     * redis에 있는 랭킹값으로 조회
     * @return
     */
    private RankingSnapshot readSnapshot() {
        String json = redisTemplate.opsForValue().get(SNAPSHOT_KEY);
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, RankingSnapshot.class);
        } catch (Exception e) {
            return null;
        }
    }
}
