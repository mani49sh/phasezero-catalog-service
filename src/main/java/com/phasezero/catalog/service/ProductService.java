package com.phasezero.catalog.service;

import com.phasezero.catalog.requestdto.ProductRequest;
import com.phasezero.catalog.responsedto.InventoryValueResponse;
import com.phasezero.catalog.responsedto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    Page<ProductResponse> getAllProducts(Pageable pageable);
    List<ProductResponse> searchProductsByName(String name);
    List<ProductResponse> filterProductsByCategory(String category);
    List<ProductResponse> sortProductsByPriceAsc();

    // **ONLY returns total inventory value** (sum of price * stock)
    InventoryValueResponse calculateTotalInventoryValue();
}