package dev.manuthlakdiw.primebasketbackend.dto.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record ProductResponse(
        long id,
        String sku,
        String name,
        String description,

        BigDecimal originalPrice,
        BigDecimal sellingPrice,
        boolean isOnSale,

        int stockQuantity,
        String stockStatus,

        List<String> images,
        Map<String, String> attributes,

        boolean isFeatured,
        boolean isActive,

        ProductCategoryResponse category
) {
    public record ProductCategoryResponse(
            long id,
            String name
    ) {}
}
