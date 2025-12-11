package com.phasezero.catalog.config;

import com.phasezero.catalog.entity.Product;
import com.phasezero.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                log.info("Initializing sample product data...");

                Product product1 = Product.builder()
                        .partNumber("PN-1001")
                        .partName("hydraulic filter")
                        .category("Filters")
                        .price(new BigDecimal("99.99"))
                        .stock(100)
                        .brand("Bosch")
                        .description("High-quality hydraulic filter for industrial use")
                        .build();

                Product product2 = Product.builder()
                        .partNumber("PN-1002")
                        .partName("engine oil")
                        .category("Lubricants")
                        .price(new BigDecimal("29.99"))
                        .stock(200)
                        .brand("Mobil")
                        .description("Synthetic engine oil 5W-30")
                        .build();

                Product product3 = Product.builder()
                        .partNumber("PN-1003")
                        .partName("brake pads")
                        .category("Brakes")
                        .price(new BigDecimal("79.99"))
                        .stock(50)
                        .brand("Brembo")
                        .description("Ceramic brake pads for passenger cars")
                        .build();

                Product product4 = Product.builder()
                        .partNumber("PN-1004")
                        .partName("air filter")
                        .category("Filters")
                        .price(new BigDecimal("24.99"))
                        .stock(150)
                        .brand("MANN")
                        .description("Premium air filter for improved engine performance")
                        .build();

                Product product5 = Product.builder()
                        .partNumber("PN-1005")
                        .partName("spark plug")
                        .category("Ignition")
                        .price(new BigDecimal("12.99"))
                        .stock(300)
                        .brand("NGK")
                        .description("Iridium spark plug for better fuel efficiency")
                        .build();

                productRepository.save(product1);
                productRepository.save(product2);
                productRepository.save(product3);
                productRepository.save(product4);
                productRepository.save(product5);

                log.info("Sample data initialized with {} products", productRepository.count());
            }
        };
    }
}

//We are using H2 database (in-memory database) for data persistence.
// The DataInitializer is just populating initial data into the H2 database.