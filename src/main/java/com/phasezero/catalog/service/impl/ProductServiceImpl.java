package com.phasezero.catalog.service.impl;

import com.phasezero.catalog.entity.Product;
import com.phasezero.catalog.exception.DuplicateProductException;
import com.phasezero.catalog.exception.ProductNotFoundException; // Changed import
import com.phasezero.catalog.repository.ProductRepository;
import com.phasezero.catalog.requestdto.ProductRequest;
import com.phasezero.catalog.responsedto.InventoryValueResponse;
import com.phasezero.catalog.responsedto.ProductResponse;
import com.phasezero.catalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class
ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByPartNumber(request.getPartNumber())) {
            throw new DuplicateProductException(request.getPartNumber());
        }

        Product product = Product.builder()
                .partNumber(request.getPartNumber())
                .partName(request.getName())
                .category(request.getCategory())
                .price(request.getPrice())
                .stock(request.getStock())
                .brand(request.getBrand())
                .description(request.getDescription())
                .build();

        Product savedProduct = productRepository.save(product);
        return convertToProductResponse(savedProduct);
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::convertToProductResponse);
    }

    @Override
    public List<ProductResponse> searchProductsByName(String name) {
        List<Product> products = productRepository.findByPartNameContainingIgnoreCase(name);

        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found with name containing: " + name);
        }

        return products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> filterProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);

        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found in category: " + category);
        }

        return products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> sortProductsByPriceAsc() {
        List<Product> products = productRepository.findAllByOrderByPriceAsc();
        return products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryValueResponse calculateTotalInventoryValue() {
        BigDecimal totalValue = productRepository.calculateTotalInventoryValue();
        if (totalValue == null) {
            totalValue = BigDecimal.ZERO;
        }

        return new InventoryValueResponse(totalValue);
    }

    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setPartNumber(product.getPartNumber());
        response.setPartName(product.getPartName());
        response.setCategory(product.getCategory());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setBrand(product.getBrand());
        response.setDescription(product.getDescription());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}