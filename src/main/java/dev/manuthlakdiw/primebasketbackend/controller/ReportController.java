package dev.manuthlakdiw.primebasketbackend.controller;

import dev.manuthlakdiw.primebasketbackend.annotation.ApiController;
import dev.manuthlakdiw.primebasketbackend.dto.report.OrderStatusDistributionResponse;
import dev.manuthlakdiw.primebasketbackend.dto.report.SalesRevenueResponse;
import dev.manuthlakdiw.primebasketbackend.service.impl.ReportServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@ApiController("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {
    private final ReportServiceImpl reportService;

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/sales-revenue")
    public List<SalesRevenueResponse> getSalesRevenue() {
        return reportService.getSalesRevenueLast7Days();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/status-distribution")
    public List<OrderStatusDistributionResponse> getStatusDistribution() {
        return reportService.getOrderStatusDistribution();
    }


}
