package dev.manuthlakdiw.primebasketbackend.repository;

import dev.manuthlakdiw.primebasketbackend.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsBySkuAndIsDeletedFalse(String sku);

    boolean existsByNameAndIsDeletedFalse(String name);

    Page<ProductEntity> findByIsDeletedFalse(Pageable pageable);

    @Query(value = """
            SELECT p.* FROM products p 
            WHERE p.category_id = :categoryId 
            AND p.is_deleted = false 
            AND (:keyword IS NULL OR :keyword = '' OR 
                 p.name % :keyword OR 
                 p.sku ILIKE CONCAT('%', :keyword, '%') OR
                 similarity(p.name, :keyword) > 0.2)
            ORDER BY similarity(p.name, :keyword) DESC NULLS LAST, p.id DESC
            """,
            countQuery = """
            SELECT count(*) FROM products p 
            WHERE p.category_id = :categoryId 
            AND p.is_deleted = false 
            AND (:keyword IS NULL OR :keyword = '' OR 
                 p.name % :keyword OR 
                 p.sku ILIKE CONCAT('%', :keyword, '%') OR
                 similarity(p.name, :keyword) > 0.2)
            """,
            nativeQuery = true)
    Page<ProductEntity> findProductsByCategoryAndKeyword(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.isFeatured = true AND p.isDeleted = false AND p.stockQuantity > 0 ORDER BY p.id DESC")
    Page<ProductEntity> findFeaturedProducts(Pageable pageable);

    @Query(value = """
            SELECT p.* FROM products p 
            WHERE p.is_featured = true 
            AND p.is_deleted = false 
            AND p.stock_quantity > 0 
            AND (:keyword IS NULL OR :keyword = '' OR 
                 p.name % :keyword OR 
                 p.sku ILIKE CONCAT('%', :keyword, '%') OR
                 similarity(p.name, :keyword) > 0.2)
            ORDER BY similarity(p.name, :keyword) DESC NULLS LAST, p.id DESC
            """,
            countQuery = """
            SELECT count(*) FROM products p 
            WHERE p.is_featured = true 
            AND p.is_deleted = false 
            AND p.stock_quantity > 0 
            AND (:keyword IS NULL OR :keyword = '' OR 
                 p.name % :keyword OR 
                 p.sku ILIKE CONCAT('%', :keyword, '%') OR
                 similarity(p.name, :keyword) > 0.2)
            """,
            nativeQuery = true)
    Page<ProductEntity> findFeaturedProductsWithKeyword(
            @Param("keyword") String keyword,
            Pageable pageable);


    @Query("SELECT p FROM ProductEntity p WHERE p.salePrice IS NOT NULL AND p.salePrice > 0 AND p.salePrice < p.price " +
            "AND (p.saleStartDate IS NULL OR p.saleStartDate <= CURRENT_TIMESTAMP) " +
            "AND (p.saleEndDate IS NULL OR p.saleEndDate >= CURRENT_TIMESTAMP) " +
            "AND p.isDeleted = false ORDER BY p.id DESC")
    Page<ProductEntity> findTopOnSaleProducts(Pageable pageable);

    @Query(value = """
            SELECT p.* FROM products p 
            WHERE p.sale_price IS NOT NULL AND p.sale_price > 0 AND p.sale_price < p.unit_price 
            AND (p.sale_start_date IS NULL OR p.sale_start_date <= CURRENT_TIMESTAMP) 
            AND (p.sale_end_date IS NULL OR p.sale_end_date >= CURRENT_TIMESTAMP) 
            AND p.is_deleted = false 
            AND (:keyword IS NULL OR :keyword = '' OR 
                 p.name % :keyword OR 
                 p.sku ILIKE CONCAT('%', :keyword, '%') OR
                 similarity(p.name, :keyword) > 0.2)
            ORDER BY similarity(p.name, :keyword) DESC NULLS LAST, p.id DESC
            """,
            countQuery = """
            SELECT count(*) FROM products p 
            WHERE p.sale_price IS NOT NULL AND p.sale_price > 0 AND p.sale_price < p.unit_price 
            AND (p.sale_start_date IS NULL OR p.sale_start_date <= CURRENT_TIMESTAMP) 
            AND (p.sale_end_date IS NULL OR p.sale_end_date >= CURRENT_TIMESTAMP) 
            AND p.is_deleted = false 
            AND (:keyword IS NULL OR :keyword = '' OR 
                 p.name % :keyword OR 
                 p.sku ILIKE CONCAT('%', :keyword, '%') OR
                 similarity(p.name, :keyword) > 0.2)
            """,
            nativeQuery = true)
    Page<ProductEntity> findOnSaleProductsWithKeyword(
            @Param("keyword") String keyword,
            Pageable pageable);


    @Query(value = """
            SELECT p.* FROM products p 
            WHERE p.is_deleted = false 
            AND (:keyword IS NULL OR :keyword = '' OR 
                 p.name % :keyword OR 
                 p.sku ILIKE CONCAT('%', :keyword, '%') OR
                 similarity(p.name, :keyword) > 0.2)
            ORDER BY similarity(p.name, :keyword) DESC NULLS LAST, p.id DESC
            """,
            countQuery = """
            SELECT count(*) FROM products p 
            WHERE p.is_deleted = false 
            AND (:keyword IS NULL OR :keyword = '' OR 
                 p.name % :keyword OR 
                 p.sku ILIKE CONCAT('%', :keyword, '%') OR
                 similarity(p.name, :keyword) > 0.2)
            """,
            nativeQuery = true)
    Page<ProductEntity> searchAllActiveProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) FROM ProductEntity p WHERE p.isDeleted = false")
    long countTotalProducts();
    

}