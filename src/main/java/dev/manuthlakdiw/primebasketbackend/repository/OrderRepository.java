package dev.manuthlakdiw.primebasketbackend.repository;

import dev.manuthlakdiw.primebasketbackend.entity.OrderEntity;
import dev.manuthlakdiw.primebasketbackend.entity.types.OrderStatusType;
import dev.manuthlakdiw.primebasketbackend.entity.types.PaymentStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findOrderEntitiesByOrderNumber(String orderNumber);

    List<OrderEntity> findByStatusAndPaymentStatusAndCreatedAtBefore(
            OrderStatusType status,
            PaymentStatusType paymentStatus,
            LocalDateTime timeLimit
    );

    Page<OrderEntity> findByStatus(OrderStatusType status, Pageable pageable);

    @Query("SELECT o FROM OrderEntity o WHERE o.createdAt >= :startDate AND o.status != 'CANCELLED'")
    List<OrderEntity> findValidOrdersSince(@Param("startDate") java.time.LocalDateTime startDate);

    @Query("SELECT o.status, COUNT(o) FROM OrderEntity o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

    @Query("SELECT COALESCE(SUM(o.finalTotal), 0) FROM OrderEntity o WHERE o.status IN ('DELIVERED', 'PROCESSING', 'SHIPPED')")
    BigDecimal calculateTotalSales();

    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.status = 'PENDING'")
    long countPendingOrders();


    Page<OrderEntity> findOrderEntitiesByUser_Email(String userEmail, Pageable pageable);
}
