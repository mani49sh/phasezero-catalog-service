package com.phasezero.catalog.controller;

import com.phasezero.catalog.requestdto.ProductRequest;
import com.phasezero.catalog.responsedto.RestApiResponse;
import com.phasezero.catalog.responsedto.InventoryValueResponse;
import com.phasezero.catalog.responsedto.ProductResponse;
import com.phasezero.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Catalog", description = "APIs for managing product catalog")
public class ProductController {

    private final ProductService productService;

    // 1. Add new product
    @PostMapping
    @Operation(summary = "Create a new product",
            description = "Create a new product with validation of business rules")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Duplicate part number")
    })
    public ResponseEntity<RestApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);

        RestApiResponse<ProductResponse> apiResponse = RestApiResponse.<ProductResponse>builder()
                .status("created")
                .message("Resource created successfully")
                .data(response)
                .path("/api/v1/products")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    // 2. List all products (WITH PAGINATION)
    @GetMapping
    @Operation(summary = "Get all products with pagination",
            description = "Retrieve all products with pagination support")
    public ResponseEntity<RestApiResponse<Page<ProductResponse>>> getAllProducts(
            @Parameter(description = "Pagination parameters (page, size, sort)")
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getAllProducts(pageable);

        RestApiResponse<Page<ProductResponse>> apiResponse = RestApiResponse.<Page<ProductResponse>>builder()
                .status("success")
                .message("Products retrieved successfully")
                .data(products)
                .path("/api/v1/products")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 3. Search by name
    @GetMapping("/search")
    @Operation(summary = "Search products by name",
            description = "Search products whose name contains the given text (case-insensitive)")
    public ResponseEntity<RestApiResponse<List<ProductResponse>>> searchProductsByName(
            @Parameter(description = "Search term for product name", required = true)
            @RequestParam String name) {
        List<ProductResponse> products = productService.searchProductsByName(name);

        RestApiResponse<List<ProductResponse>> apiResponse = RestApiResponse.<List<ProductResponse>>builder()
                .status("success")
                .message("Products found")
                .data(products)
                .path("/api/v1/products/search")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 4. Filter by category
    @GetMapping("/filter")
    @Operation(summary = "Filter products by category",
            description = "Get all products belonging to a specific category")
    public ResponseEntity<RestApiResponse<List<ProductResponse>>> filterProductsByCategory(
            @Parameter(description = "Category name", required = true)
            @RequestParam String category) {
        List<ProductResponse> products = productService.filterProductsByCategory(category);

        RestApiResponse<List<ProductResponse>> apiResponse = RestApiResponse.<List<ProductResponse>>builder()
                .status("success")
                .message("Products found")
                .data(products)
                .path("/api/v1/products/filter")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 5. Sort products by price
    @GetMapping("/sort")
    @Operation(summary = "Sort products by price ascending",
            description = "Get all products sorted by price in ascending order")
    public ResponseEntity<RestApiResponse<List<ProductResponse>>> sortProductsByPrice() {
        List<ProductResponse> products = productService.sortProductsByPriceAsc();

        RestApiResponse<List<ProductResponse>> apiResponse = RestApiResponse.<List<ProductResponse>>builder()
                .status("success")
                .message("Products sorted by price ascending")
                .data(products)
                .path("/api/v1/products/sort")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 6. Return total inventory value
    @GetMapping("/inventory/value")
    @Operation(summary = "Calculate total inventory value",
            description = "Calculate the total inventory value (sum of price * stock for all products)")
    public ResponseEntity<RestApiResponse<InventoryValueResponse>> calculateTotalInventoryValue() {
        InventoryValueResponse response = productService.calculateTotalInventoryValue();

        RestApiResponse<InventoryValueResponse> apiResponse = RestApiResponse.<InventoryValueResponse>builder()
                .status("success")
                .message("Inventory value calculated")
                .data(response)
                .path("/api/v1/products/inventory/value")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}