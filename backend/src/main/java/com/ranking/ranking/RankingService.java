package com.ranking.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
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
	private static final String VIEWED_KEY_PREFIX = "viewed:";
	private static final long DEDUP_TTL_SECONDS = 60; //60초

	private final RedisTemplate<String, String> redisTemplate;
	private final RedisScript<String> increaseViewCountScript;

	/**
	 * productId 를 받아서 상품 조회수를 1 올림 (Lua 스크립트로 원자적 처리 + 중복 조회 필터링)
	 * @param productId
	 * @param viewerId 조회자IP
	 */
	public void increaseViewCount(Long productId, String viewerId) {
		String viewedKey = VIEWED_KEY_PREFIX + productId + ":" + viewerId;
		redisTemplate.execute(
				increaseViewCountScript,
				Arrays.asList(RANKING_KEY, viewedKey),
				String.valueOf(productId),
				"1",
				String.valueOf(DEDUP_TTL_SECONDS)
		);
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
