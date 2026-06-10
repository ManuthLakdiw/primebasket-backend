package dev.manuthlakdiw.primebasketbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_name", columnList = "name"),
                @Index(name = "idx_product_featured_active", columnList = "isFeatured, isActive")
        }
)
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String sku;

    private String name;

    @Lob
    @Column(columnDefinition = "text")
    private String description;

    @Column(
            name = "unit_price",
            precision = 10,
            scale = 2
    )
    private BigDecimal price = BigDecimal.ZERO;

    private int stockQuantity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> images;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> attributes;

    private boolean isFeatured = false;

    private boolean isActive = true;

    @OneToMany(
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            mappedBy = "product"
    )
    private List<CartItemEntity> cartItems;

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY
    )
    private List<OrderItemEntity> orderItems;

}
