package dev.manuthlakdiw.primebasketbackend.dto.report;

import java.math.BigDecimal;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record SalesRevenueResponse(
        String day,
        BigDecimal revenue
) {
}
