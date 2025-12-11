package Service;

import com.phasezero.catalog.entity.Product;
import com.phasezero.catalog.exception.DuplicateProductException;
import com.phasezero.catalog.exception.ProductNotFoundException;
import com.phasezero.catalog.repository.ProductRepository;
import com.phasezero.catalog.requestdto.ProductRequest;
import com.phasezero.catalog.responsedto.InventoryValueResponse;
import com.phasezero.catalog.responsedto.ProductResponse;
import com.phasezero.catalog.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        product = Product.builder()
                .id(1L)
                .partNumber("ABC123")
                .partName("test product")
                .category("Electronics")
                .price(BigDecimal.valueOf(100))
                .stock(5)
                .build();
    }

    // -------------------------------
    // CREATE PRODUCT
    // -------------------------------
    @Test
    void testCreateProduct_success() {
        ProductRequest request = new ProductRequest(
                "ABC123", "test product", "Electronics",
                BigDecimal.valueOf(100), 5, "Sony", "Nice"
        );

        when(productRepository.existsByPartNumber("ABC123")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.createProduct(request);

        assertEquals("ABC123", response.getPartNumber());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testCreateProduct_duplicatePartNumber() {
        ProductRequest request = new ProductRequest(
                "ABC123", "test", "Electronics",
                BigDecimal.valueOf(100), 5, null, null
        );

        when(productRepository.existsByPartNumber("ABC123")).thenReturn(true);

        assertThrows(DuplicateProductException.class,
                () -> productService.createProduct(request));

        verify(productRepository, never()).save(any());
    }

    // -------------------------------
    // GET ALL PAGINATED
    // -------------------------------
    @Test
    void testGetAllProducts_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<ProductResponse> response = productService.getAllProducts(pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("ABC123", response.getContent().get(0).getPartNumber());
    }

    // -------------------------------
    // SEARCH
    // -------------------------------
    @Test
    void testSearchProductsByName_success() {
        when(productRepository.findByPartNameContainingIgnoreCase("test"))
                .thenReturn(List.of(product));

        List<ProductResponse> response = productService.searchProductsByName("test");

        assertEquals(1, response.size());
    }

    @Test
    void testSearchProductsByName_notFound() {
        when(productRepository.findByPartNameContainingIgnoreCase("xyz"))
                .thenReturn(List.of());

        assertThrows(ProductNotFoundException.class,
                () -> productService.searchProductsByName("xyz"));
    }

    // -------------------------------
    // CATEGORY FILTER
    // -------------------------------
    @Test
    void testFilterProductsByCategory_success() {
        when(productRepository.findByCategory("Electronics")).thenReturn(List.of(product));

        List<ProductResponse> response = productService.filterProductsByCategory("Electronics");

        assertEquals(1, response.size());
    }

    @Test
    void testFilterProductsByCategory_notFound() {
        when(productRepository.findByCategory("Foo")).thenReturn(List.of());

        assertThrows(ProductNotFoundException.class,
                () -> productService.filterProductsByCategory("Foo"));
    }

    // -------------------------------
    // SORT ASC
    // -------------------------------
    @Test
    void testSortProductsByPriceAsc_success() {
        when(productRepository.findAllByOrderByPriceAsc())
                .thenReturn(List.of(product));

        List<ProductResponse> response = productService.sortProductsByPriceAsc();

        assertEquals(1, response.size());
    }

    // -------------------------------
    // INVENTORY VALUE
    // -------------------------------
    @Test
    void testInventoryValue_success() {
        when(productRepository.calculateTotalInventoryValue()).thenReturn(BigDecimal.valueOf(500));

        InventoryValueResponse response = productService.calculateTotalInventoryValue();

        assertEquals(BigDecimal.valueOf(500), response.getTotalInventoryValue());
    }

    @Test
    void testInventoryValue_nullCase() {
        when(productRepository.calculateTotalInventoryValue()).thenReturn(null);

        InventoryValueResponse response = productService.calculateTotalInventoryValue();

        assertEquals(BigDecimal.ZERO, response.getTotalInventoryValue());
    }
}
