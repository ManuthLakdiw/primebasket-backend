package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.dto.report.OrderStatusDistributionResponse;
import dev.manuthlakdiw.primebasketbackend.dto.report.SalesRevenueResponse;
import dev.manuthlakdiw.primebasketbackend.entity.OrderEntity;
import dev.manuthlakdiw.primebasketbackend.repository.OrderRepository;
import dev.manuthlakdiw.primebasketbackend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;

    @Override
    @Cacheable(value = "reports", key = "'salesRevenue'")
    public List<SalesRevenueResponse> getSalesRevenueLast7Days() {

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(6).withHour(0).withMinute(0).withSecond(0);
        List<OrderEntity> recentOrders = orderRepository.findValidOrdersSince(sevenDaysAgo);

        Map<LocalDate, BigDecimal> dailyRevenue = new HashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            dailyRevenue.put(today.minusDays(i), BigDecimal.ZERO);
        }

        for (OrderEntity order : recentOrders) {
            LocalDate orderDate = order.getCreatedAt().toLocalDate();
            if (dailyRevenue.containsKey(orderDate)) {
                dailyRevenue.put(orderDate, dailyRevenue.get(orderDate).add(order.getFinalTotal()));
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);

        return dailyRevenue.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new SalesRevenueResponse(entry.getKey().format(formatter), entry.getValue()))
                .toList();
    }

    @Override
    @Cacheable(value = "reports", key = "'statusDistribution'")
    public List<OrderStatusDistributionResponse> getOrderStatusDistribution() {
        return orderRepository.countOrdersByStatus().stream()
                .map(obj -> new OrderStatusDistributionResponse(obj[0].toString(), (Long) obj[1]))
                .toList();
    }
}
