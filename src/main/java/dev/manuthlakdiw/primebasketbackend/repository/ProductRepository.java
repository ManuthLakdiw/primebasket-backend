package dev.manuthlakdiw.primebasketbackend.repository;

import dev.manuthlakdiw.primebasketbackend.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsBySkuAndIsDeletedFalse(String sku);

    boolean existsByNameAndIsDeletedFalse(String name);

    Page<ProductEntity> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.category.id = :categoryId AND p.isDeleted = false AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ProductEntity> findProductsByCategoryAndKeyword(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable);


    @Query("SELECT p FROM ProductEntity p WHERE p.isFeatured = true AND p.isDeleted = false AND p.stockQuantity > 0 ORDER BY p.id DESC")
    Page<ProductEntity> findFeaturedProducts(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.isFeatured = true AND p.isDeleted = false AND p.stockQuantity > 0 AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ProductEntity> findFeaturedProductsWithKeyword(
            @Param("keyword") String keyword,
            Pageable pageable);


    @Query("SELECT p FROM ProductEntity p WHERE p.salePrice IS NOT NULL AND p.salePrice > 0 AND p.salePrice < p.price " +
            "AND (p.saleStartDate IS NULL OR p.saleStartDate <= CURRENT_TIMESTAMP) " +
            "AND (p.saleEndDate IS NULL OR p.saleEndDate >= CURRENT_TIMESTAMP) " +
            "AND p.isDeleted = false ORDER BY p.id DESC")
    Page<ProductEntity> findTopOnSaleProducts(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.salePrice IS NOT NULL AND p.salePrice > 0 AND p.salePrice < p.price " +
            "AND (p.saleStartDate IS NULL OR p.saleStartDate <= CURRENT_TIMESTAMP) " +
            "AND (p.saleEndDate IS NULL OR p.saleEndDate >= CURRENT_TIMESTAMP) " +
            "AND p.isDeleted = false AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ProductEntity> findOnSaleProductsWithKeyword(
            @Param("keyword") String keyword,
            Pageable pageable);


    @Query("SELECT p FROM ProductEntity p WHERE p.isDeleted = false AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ProductEntity> searchAllActiveProducts(@Param("keyword") String keyword, Pageable pageable);


}
