package dev.manuthlakdiw.primebasketbackend.repository;

import dev.manuthlakdiw.primebasketbackend.entity.OrderEntity;
import dev.manuthlakdiw.primebasketbackend.entity.types.OrderStatusType;
import dev.manuthlakdiw.primebasketbackend.entity.types.PaymentStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}
