package com.phasezero.catalog.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String partNumber;
    private String PartName;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private String brand;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Note: No totalValue field as per requirements
}