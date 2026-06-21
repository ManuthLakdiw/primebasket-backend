package dev.manuthlakdiw.primebasketbackend.repository;

import dev.manuthlakdiw.primebasketbackend.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
