package com.ranking.config;

import com.ranking.product.entity.Product;
import com.ranking.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 앱 켰을때 상품 자동으로 넣어주기
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        List<Product> sampleProducts = List.of(
                new Product("아메리카노", 4500),
                new Product("카페라떼", 5000),
                new Product("카푸치노", 5000),
                new Product("바닐라라떼", 5500),
                new Product("카라멜마키아토", 5800),
                new Product("콜드브루", 5000),
                new Product("아이스티", 4000),
                new Product("자몽에이드", 5500),
                new Product("레몬에이드", 5500),
                new Product("딸기스무디", 6000),
                new Product("망고스무디", 6000),
                new Product("초코라떼", 5500),
                new Product("그린티라떼", 5500),
                new Product("얼그레이티", 4500),
                new Product("유자차", 5000),
                new Product("크로플", 6500),
                new Product("티라미수", 6800),
                new Product("치즈케이크", 6500),
                new Product("크루아상", 4000),
                new Product("베이글", 4500)
        );

        productRepository.saveAll(sampleProducts);
    }

}
