package com.ranking.rank.controller;

import com.ranking.cache.RankingItem;
import com.ranking.product.entity.Product;
import com.ranking.product.repository.ProductRepository;
import com.ranking.rank.dto.RankingResponseDto;
import com.ranking.rank.service.RankingCacheService;
import com.ranking.rank.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;
    private final RankingCacheService rankingCacheService;
    private final ProductRepository productRepository;

    /**
     * 랭킹 조회하기 (상품명 포함)
     * @param limit
     * @return
     */
    @GetMapping("/deprecated-rankings")
    public List<RankingResponseDto> getRankingsDeprecated(@RequestParam(defaultValue = "10") int limit) {
        Set<ZSetOperations.TypedTuple<String>> tuples = rankingService.getTopRankings(limit);

        List<Long> productIds = tuples.stream()
                .map(tuple -> Long.valueOf(tuple.getValue()))
                .toList();

        Map<Long, String> productNameById = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Product::getName));

        return tuples.stream()
                .map(tuple -> {
                    Long productId = Long.valueOf(tuple.getValue());
                    String productName = productNameById.get(productId);
                    return new RankingResponseDto(productId, productName, tuple.getScore());
                })
                .toList();
    }

    /**
     * 랭킹 조회하기 (상품명 포함, 캐시 + 스탬피드 방지 적용)
     * @param limit
     * @return
     */
    @GetMapping("/rankings")
    public List<RankingResponseDto> getRankings(@RequestParam(defaultValue = "10") int limit) {
        List<RankingItem> topRanking = rankingCacheService.getTopRanking();

        return topRanking.stream()
                .limit(limit)
                .map(item -> new RankingResponseDto(item.productId(), item.productName(), item.viewCount()))
                .toList();
    }
}
