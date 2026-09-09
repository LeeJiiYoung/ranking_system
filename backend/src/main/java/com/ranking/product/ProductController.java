package com.ranking.product;

import com.ranking.product.dto.ProductRequestDto;
import com.ranking.product.dto.ProductResponseDto;
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
     * 상품조회
     * @param productId
     * @return
     */
    @GetMapping("/{productId}")
    public ProductResponseDto getProduct(@PathVariable Long productId) {
        Product product = productService.getProduct(productId);
        return new ProductResponseDto(product.getId(), product.getName(), product.getPrice());
    }

}
