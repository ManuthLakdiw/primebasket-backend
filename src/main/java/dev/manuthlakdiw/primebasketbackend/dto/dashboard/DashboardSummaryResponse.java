package dev.manuthlakdiw.primebasketbackend.dto.dashboard;

import java.math.BigDecimal;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record DashboardSummaryResponse(
        BigDecimal totalSales,
        long activeUsers,
        long totalProducts,
        long pendingOrders
) {
}
