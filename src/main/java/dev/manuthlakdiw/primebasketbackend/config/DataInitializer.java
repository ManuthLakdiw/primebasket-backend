package dev.manuthlakdiw.primebasketbackend.config;

import dev.manuthlakdiw.primebasketbackend.entity.CategoryEntity;
import dev.manuthlakdiw.primebasketbackend.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.findByIsDefaultTrue().isEmpty()) {
                CategoryEntity defaultCategory = CategoryEntity.builder()
                        .name("General")
                        .description("Default category for products")
                        .isDefault(true)
                        .build();
                categoryRepository.save(defaultCategory);
                System.out.println("Default category created successfully!");
            }
        };
    }
}
