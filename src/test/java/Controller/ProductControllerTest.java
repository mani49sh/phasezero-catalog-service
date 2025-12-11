package Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phasezero.catalog.controller.ProductController;
import com.phasezero.catalog.requestdto.ProductRequest;
import com.phasezero.catalog.responsedto.InventoryValueResponse;
import com.phasezero.catalog.responsedto.ProductResponse;
import com.phasezero.catalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;
    private ProductResponse productResponse;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()) // FIX FOR Pageable
                .build();

        objectMapper = new ObjectMapper();

        productResponse = ProductResponse.builder()
                .id(1L)
                .partNumber("ABC123")
                .PartName("engine part")
                .category("Automotive")
                .price(BigDecimal.valueOf(2500))
                .stock(5)
                .brand("Bosch")
                .description("High quality engine component")
                .build();
    }

    // -----------------------------------------------------------
    // 1. CREATE PRODUCT
    // -----------------------------------------------------------
    @Test
    void testCreateProduct_success() throws Exception {

        ProductRequest request = new ProductRequest(
                "ABC123",
                "engine part",
                "Automotive",
                BigDecimal.valueOf(2500),
                5,
                "Bosch",
                "High quality engine component"
        );

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(productResponse);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("created"))
                .andExpect(jsonPath("$.data.partNumber").value("ABC123"));

        verify(productService, times(1)).createProduct(any());
    }

    // -----------------------------------------------------------
    // 2. GET ALL PRODUCTS (Paginated)
    // -----------------------------------------------------------
    @Test
    void testGetAllProducts_success() throws Exception {

        Pageable pageable = PageRequest.of(0, 20);
        Page<ProductResponse> page = new PageImpl<>(List.of(productResponse), pageable, 1);

        when(productService.getAllProducts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0") // optional but recommended
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].partNumber").value("ABC123"));

        verify(productService).getAllProducts(any());
    }

    // -----------------------------------------------------------
    // 3. SEARCH PRODUCTS BY NAME
    // -----------------------------------------------------------
    @Test
    void testSearchProductsByName_success() throws Exception {

        when(productService.searchProductsByName("engine"))
                .thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/v1/products/search")
                        .param("name", "engine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].partNumber").value("ABC123"));
    }

    // -----------------------------------------------------------
    // 4. FILTER BY CATEGORY
    // -----------------------------------------------------------
    @Test
    void testFilterProductsByCategory_success() throws Exception {

        when(productService.filterProductsByCategory("Automotive"))
                .thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/v1/products/filter")
                        .param("category", "Automotive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].category").value("Automotive"));
    }

    // -----------------------------------------------------------
    // 5. SORT PRODUCTS BY PRICE ASC
    // -----------------------------------------------------------
    @Test
    void testSortProductsByPrice_success() throws Exception {

        when(productService.sortProductsByPriceAsc())
                .thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/v1/products/sort"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].partNumber").value("ABC123"));
    }

    // -----------------------------------------------------------
    // 6. INVENTORY VALUE API
    // -----------------------------------------------------------
    @Test
    void testInventoryValue_success() throws Exception {

        InventoryValueResponse response = new InventoryValueResponse(BigDecimal.valueOf(12500));

        when(productService.calculateTotalInventoryValue()).thenReturn(response);

        mockMvc.perform(get("/api/v1/products/inventory/value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalInventoryValue").value(12500));
    }
}
