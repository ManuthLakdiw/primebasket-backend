package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.dto.dashboard.DashboardSummaryResponse;
import dev.manuthlakdiw.primebasketbackend.repository.OrderRepository;
import dev.manuthlakdiw.primebasketbackend.repository.ProductRepository;
import dev.manuthlakdiw.primebasketbackend.repository.UserRepository;
import dev.manuthlakdiw.primebasketbackend.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class DashBoardServiceImpl implements DashBoardService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dashboardSummary", key = "'admin-summary'")
    public DashboardSummaryResponse getDashboardSummary() {
        return new DashboardSummaryResponse(
                orderRepository.calculateTotalSales(),
                userRepository.countActiveCustomers(),
                productRepository.countTotalProducts(),
                orderRepository.countPendingOrders()
        );
    }

}
