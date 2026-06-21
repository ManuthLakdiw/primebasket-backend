package dev.manuthlakdiw.primebasketbackend.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record ProductRequest(
        @NotBlank(message = "SKU is required")
        String sku,

        @NotBlank(message = "Product name is required")
        String name,

        String description,

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price cannot be negative")
        BigDecimal price,

        @Min(value = 0, message = "Stock quantity cannot be negative")
        int stockQuantity,

        BigDecimal salePrice,
        LocalDateTime saleStartDate,
        LocalDateTime saleEndDate,

        List<String> images,
        Map<String, String> attributes,

        boolean isFeatured,
        boolean isActive,

        @NotNull(message = "Category ID is required")
        Long categoryId
) {
    public ProductRequest {
        sku = sku != null ? sku.trim() : null;
        name = name != null ? name.trim() : null;
        description = description != null ? description.trim() : null;

        images = images != null ? images : List.of();
        attributes = attributes != null ? attributes : Map.of();
    }
}
