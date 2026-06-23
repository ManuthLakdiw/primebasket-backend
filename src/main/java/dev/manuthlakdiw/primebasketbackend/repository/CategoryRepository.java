package dev.manuthlakdiw.primebasketbackend.repository;

import dev.manuthlakdiw.primebasketbackend.dto.category.CategoryDropdownResponse;
import dev.manuthlakdiw.primebasketbackend.dto.category.CategoryPublicResponse;
import dev.manuthlakdiw.primebasketbackend.dto.category.CategoryResponse;
import dev.manuthlakdiw.primebasketbackend.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {


    boolean existsByName(String name);

    Optional<CategoryEntity> findByIsDefaultTrue();

    @Query("SELECT new dev.manuthlakdiw.primebasketbackend.dto.category.CategoryResponse(" +
            "c.id, c.name, c.description, COUNT(p)) " +
            "FROM CategoryEntity c LEFT JOIN c.products p WITH p.isDeleted = false " +
            "GROUP BY c.id, c.name, c.description")
    Page<CategoryResponse> findAllWithProductCount(Pageable pageable);

    @Query("SELECT new dev.manuthlakdiw.primebasketbackend.dto.category.CategoryDropdownResponse(c.id, c.name) " +
            "FROM CategoryEntity c ORDER BY c.name ASC")
    List<CategoryDropdownResponse> findAllForDropdown();

    @Query("SELECT new dev.manuthlakdiw.primebasketbackend.dto.category.CategoryPublicResponse(" +
            "c.id, c.name, c.description, COUNT(p.id)) " +
            "FROM CategoryEntity c LEFT JOIN c.products p ON p.isDeleted = false AND p.isActive = true " +
            "GROUP BY c.id, c.name, c.description " +
            "ORDER BY c.name ASC")
    List<CategoryPublicResponse> findAllPublicCategoriesWithProductCount();

}
