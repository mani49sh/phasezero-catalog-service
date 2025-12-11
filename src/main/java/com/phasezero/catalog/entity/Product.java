package com.phasezero.catalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(columnNames = "partNumber", name = "uk_product_part_number")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Part number is required")
    @Size(min = 3, max = 50, message = "Part number must be between 3 and 50 characters")
    private String partNumber;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Part name is required")
    @Size(min = 2, max = 100, message = "Part name must be between 2 and 100 characters")
    private String partName;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    private String category;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @Column(nullable = false)
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @Column(length = 100)
    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    @Column(length = 500)
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void normalizeData() {
        if (this.partName != null) {
            this.partName = this.partName.toLowerCase().trim();
        }
        if (this.category != null) {
            this.category = this.category.trim();
        }
        if (this.partNumber != null) {
            this.partNumber = this.partNumber.toUpperCase().trim();
        }
    }
}