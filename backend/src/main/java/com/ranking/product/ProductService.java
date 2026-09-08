package com.ranking.product;

import com.ranking.ranking.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final RankingService rankingService;

	public Product getProduct(Long productId) {

        return null;
    }

}
