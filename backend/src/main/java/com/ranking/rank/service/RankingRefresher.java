package com.ranking.rank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ranking.cache.RankingItem;
import com.ranking.cache.RankingSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingRefresher {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final RankingQueryService rankingQueryService;
    private final DefaultRedisScript<Long> unlockScript;

    /**
     * 동기적으로 즉시 재계산 (콜드 스타트일 때 호출하는 쪽에서 결과를 바로 써야 하므로 동기)
     * DB에서 다시 조회
     */
    public RankingSnapshot refreshNow() {
        List<RankingItem> items = rankingQueryService.calculateTopRanking();
        RankingSnapshot snapshot = new RankingSnapshot(
                items,
                System.currentTimeMillis() + RankingCacheService.LOGICAL_TTL.toMillis()
        );
        writeSnapshot(snapshot);
        return snapshot;
    }

    /**
     * 비동기로 재계산하고, 끝나면 락 해제 (스탬피드 방지 경로)
     */
    @Async("rankingRefreshExecutor")
    public void refreshAsync(String lockToken) {
        try {
            refreshNow();
        } finally {
            unlock(lockToken);
        }
    }

    private void writeSnapshot(RankingSnapshot snapshot) {
        try {
            String json = objectMapper.writeValueAsString(snapshot);
            redisTemplate.opsForValue().set(
                    RankingCacheService.SNAPSHOT_KEY, json, RankingCacheService.PHYSICAL_TTL
            );
        } catch (Exception e) {
            throw new IllegalStateException("랭킹 스냅샷 직렬화 실패", e);
        }
    }

    private void unlock(String lockToken) {
        redisTemplate.execute(unlockScript, List.of(RankingCacheService.LOCK_KEY), lockToken);
    }
}
