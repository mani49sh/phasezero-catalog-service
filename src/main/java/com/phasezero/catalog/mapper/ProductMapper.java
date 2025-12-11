package com.phasezero.catalog.mapper;

import com.phasezero.catalog.entity.Product;
import com.phasezero.catalog.requestdto.ProductRequest;
import com.phasezero.catalog.responsedto.ProductResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }

        return Product.builder()
                .partNumber(request.getPartNumber())
                .partName(request.getName())
                .category(request.getCategory())
                .price(request.getPrice())
                .stock(request.getStock())
                .brand(request.getBrand())
                .description(request.getDescription())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        BigDecimal totalValue = product.getPrice()
                .multiply(BigDecimal.valueOf(product.getStock()))
                .setScale(2, RoundingMode.HALF_UP);

        return ProductResponse.builder()
                .id(product.getId())
                .partNumber(product.getPartNumber())
                .PartName(product.getPartName())
                .category(product.getCategory())
                .price(product.getPrice())
                .stock(product.getStock())
                .brand(product.getBrand())
                .description(product.getDescription())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public void updateEntity(ProductRequest request, Product product) {
        if (request == null || product == null) {
            return;
        }

        product.setPartName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());
    }
}