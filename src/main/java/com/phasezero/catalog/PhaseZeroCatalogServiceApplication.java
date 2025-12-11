package com.phasezero.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PhaseZeroCatalogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhaseZeroCatalogServiceApplication.class, args);
	}
}