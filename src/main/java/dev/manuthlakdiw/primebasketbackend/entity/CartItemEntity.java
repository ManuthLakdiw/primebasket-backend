package dev.manuthlakdiw.primebasketbackend.entity;

import jakarta.persistence.*;
import lombok.*;

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
        name = "cart_items",
        indexes = {
                @Index(name = "idx_cart_item_cart_id", columnList = "cart_id"),
                @Index(name = "idx_cart_item_product_id", columnList = "product_id")
        }

)
public class CartItemEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int quantity = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    private CartEntity cart;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductEntity product;

}
