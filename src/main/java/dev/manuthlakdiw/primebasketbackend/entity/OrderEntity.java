package dev.manuthlakdiw.primebasketbackend.entity;

import dev.manuthlakdiw.primebasketbackend.entity.types.Address;
import dev.manuthlakdiw.primebasketbackend.entity.types.OrderStatusType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal itemsTotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal finalTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatusType status = OrderStatusType.PENDING;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Address shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItems;

}
