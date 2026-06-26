package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.dto.report.OrderStatusDistributionResponse;
import dev.manuthlakdiw.primebasketbackend.dto.report.SalesRevenueResponse;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface ReportService {

    List<SalesRevenueResponse> getSalesRevenueLast7Days();

    List<OrderStatusDistributionResponse> getOrderStatusDistribution();


}
