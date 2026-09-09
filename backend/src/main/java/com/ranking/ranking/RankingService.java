package com.ranking.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RankingService {

	// ZADD key score member
//	key: "ranking:view:realtime"
//		  └─ member "1" - score 15.0
//		  └─ member "2" - score 8.0
//			└─ member "3" - score 23.0

	private static final String RANKING_KEY = "ranking:view:realtime";

	private final RedisTemplate<String, String> redisTemplate;

	/**
	 * productId 를 받아서 상품 조회수를 1 올림
	 * @param productId
	 */
	public void increaseViewCount(Long productId) {
		ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
		zSetOps.incrementScore(RANKING_KEY, String.valueOf(productId), 1);
	}

	/**
	 * limit 수 받아서 조회수 내림차순으로 limit개까지 조회
	 * @param limit ex)10
	 * @return
	 */
	public Set<ZSetOperations.TypedTuple<String>> getTopRankings(int limit) {
		ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
		return zSetOps.reverseRangeWithScores(RANKING_KEY, 0, limit - 1);
    }
}
