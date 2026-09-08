package com.ranking.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RankingService {

	private static final String RANKING_KEY = "ranking:view:realtime";

	private final RedisTemplate<String, String> redisTemplate;

	public void increaseViewCount(Long productId) {

	}

	public Set<ZSetOperations.TypedTuple<String>> getTopRankings(int limit) {

        return Set.of();
    }

}
