package com.ranking.product;

import com.ranking.ranking.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RankingService rankingService;

    /**
     * 상품 조회 후 조회수 ++
     *
     * @param productId 상품ID
     * @return
     */
    public Product getProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        rankingService.increaseViewCount(productId);
        return product;
    }
}
