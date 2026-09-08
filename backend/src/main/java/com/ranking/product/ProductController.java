package com.ranking.product;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

	private final ProductRepository productRepository;
	private final ProductService productService;

	@PostMapping("/products")
	public Product createProduct(@RequestBody Product product) {

        return product;
    }

	@GetMapping("/products/{productId}")
	public Product getProduct(@PathVariable Long productId) {

        return null;
    }

}
