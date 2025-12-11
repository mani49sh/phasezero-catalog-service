package com.phasezero.catalog.repository;

import com.phasezero.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Check for duplicate part number
    Optional<Product> findByPartNumber(String partNumber);
    boolean existsByPartNumber(String partNumber);

    // Search by name (case-insensitive)
    List<Product> findByPartNameContainingIgnoreCase(String partName);

    // Filter by category
    List<Product> findByCategory(String category);

    // Sort by price ascending
    List<Product> findAllByOrderByPriceAsc();

    // **ONLY THIS METHOD IS NEEDED** - Calculate total inventory value
    @Query("SELECT SUM(p.price * p.stock) FROM Product p")
    BigDecimal calculateTotalInventoryValue();

    // For pagination - Spring Data JPA provides this automatically
    Page<Product> findAll(Pageable pageable);
}