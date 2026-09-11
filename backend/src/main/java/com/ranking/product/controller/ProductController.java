package com.ranking.product.controller;

import com.ranking.product.repository.ProductRepository;
import com.ranking.product.service.ProductService;
import com.ranking.product.dto.ProductRequestDto;
import com.ranking.product.dto.ProductResponseDto;
import com.ranking.product.entity.Product;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    /**
     * 상품 등록
     * @param requestDto
     * @return
     */
    @PostMapping
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto requestDto) {
        Product product = new Product(requestDto.name(), requestDto.price());
        Product savedProduct = productRepository.save(product);
        return new ProductResponseDto(savedProduct.getId(), savedProduct.getName(), savedProduct.getPrice());
    }

    /**
     * 상품조회 (ttl만큼 중복조회 불가)
     * @param productId
     * @return
     */
    @GetMapping("/{productId}")
    public ProductResponseDto getProduct(@PathVariable Long productId, HttpServletRequest request) {
        Product product = productService.getProduct(productId, resolveViewerId(request));
        return new ProductResponseDto(product.getId(), product.getName(), product.getPrice());
    }

    /**
     * 조회자 IP 구하기
     * @param request
     * @return
     */
    private String resolveViewerId(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
