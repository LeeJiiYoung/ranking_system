package com.ranking.rank.service;

import com.ranking.cache.RankingItem;
import com.ranking.product.entity.Product;
import com.ranking.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingQueryService {
    private static final String RANKING_KEY = "ranking:view:realtime"; // 조회수 누적 Sorted Set 키 (RankingService와 동일 키로 통일)
    //100개로 고정한 이유: 호출개수가 달라질때마다 각각다른 스냅샷이 생기기 때문
    private static final int TOP_N = 100;

    private final RedisTemplate<String, String> redisTemplate;
    private final ProductRepository productRepository;

    /**
     * 상위 랭킹 아이템 구하기
     * @return
     */
    public List<RankingItem> calculateTopRanking() {
        Set<ZSetOperations.TypedTuple<String>> topEntries =
                redisTemplate.opsForZSet()
                        .reverseRangeWithScores(RANKING_KEY, 0, TOP_N - 1);

        if (topEntries == null || topEntries.isEmpty()) {
            return List.of();
        }

        return topEntries.stream()
                .map(this::toRankingItem)
                .collect(Collectors.toList());
    }

    /**
     * 상품명 찾기
     * @param entry
     * @return
     */
    private RankingItem toRankingItem(ZSetOperations.TypedTuple<String> entry) {
        Long productId = Long.valueOf(entry.getValue());   // Sorted Set 멤버값이 productId 문자열이라고 가정
        long viewCount = entry.getScore().longValue();

        String productName = productRepository.findById(productId)
                .map(Product::getName)
                .orElse("삭제된 상품");

        return new RankingItem(productId, productName, viewCount);
    }
}
