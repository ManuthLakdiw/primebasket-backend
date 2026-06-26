package dev.manuthlakdiw.primebasketbackend.dto.report;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record OrderStatusDistributionResponse(
        String status,
        long count
) {
}
