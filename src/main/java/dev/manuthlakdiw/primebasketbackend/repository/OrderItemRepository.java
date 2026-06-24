package dev.manuthlakdiw.primebasketbackend.repository;

import dev.manuthlakdiw.primebasketbackend.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}
