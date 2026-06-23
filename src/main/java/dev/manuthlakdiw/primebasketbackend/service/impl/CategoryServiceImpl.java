package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.dto.category.CategoryDropdownResponse;
import dev.manuthlakdiw.primebasketbackend.dto.category.CategoryPublicResponse;
import dev.manuthlakdiw.primebasketbackend.dto.category.CategoryRequest;
import dev.manuthlakdiw.primebasketbackend.dto.category.CategoryResponse;
import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.entity.CategoryEntity;
import dev.manuthlakdiw.primebasketbackend.entity.ProductEntity;
import dev.manuthlakdiw.primebasketbackend.projection.PublicCategoryProjection;
import dev.manuthlakdiw.primebasketbackend.repository.CategoryRepository;
import dev.manuthlakdiw.primebasketbackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())){
            throw new RuntimeException("Category already exists");
        }

        CategoryEntity.CategoryEntityBuilder builder = CategoryEntity.builder()
                .name(request.name());

        if (request.description() != null && !request.description().isEmpty()) {
            builder.description(request.description());
        }

        CategoryEntity category = builder.build();

        CategoryEntity savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    @Override
    @CachePut(value = "category", key = "#id")
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse updateCategory(long id, CategoryRequest request) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        if (!category.getName().equals(request.name()) && categoryRepository.existsByName(request.name())) {
            throw new RuntimeException("Category name already exists!");
        }

        category.setName(request.name());

        if (request.description() != null && !request.description().trim().isEmpty()) {
            category.setDescription(request.description());
        }

        CategoryEntity updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"category", "categories"}, allEntries = true)
    public String deleteCategory(long id) {
        CategoryEntity categoryToDelete = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        if (categoryToDelete.isDefault()) {
            throw new RuntimeException("Cannot delete the default category!");
        }

        List<ProductEntity> products = categoryToDelete.getProducts();

        if (products != null && !products.isEmpty()) {
            CategoryEntity defaultCategory = categoryRepository.findByIsDefaultTrue()
                    .orElseThrow(() -> new RuntimeException("No default category found to move products!"));

            for (ProductEntity product : products) {
                product.setCategory(defaultCategory);
            }
        }

        categoryRepository.deleteById(id);

        return "Category deleted successfully!";

    }

    @Override
    @Cacheable(value = "category",
            key = "#id",
            unless = "#result == null"
    )
    public CategoryResponse getCategoryById(long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found!"));
        return mapToResponse(category);
    }

    @Override
    @Cacheable(value = "categories",
            key = "#page + '-' + #size",
            unless = "#result == null"
    )
    public PageResponse<CategoryResponse> getAllCategories(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<CategoryResponse> categoryPage = categoryRepository.findAllWithProductCount(pageRequest);

        return PageResponse.from(categoryPage);
    }

    @Override
    @Cacheable(value = "categories", key = "'dropdown-list'", unless = "#result == null or #result.isEmpty()")
    public List<CategoryDropdownResponse> getCategoriesForDropdown() {
        return categoryRepository.findAllForDropdown();
    }

    @Override
    @Cacheable(value = "categories", key = "'public-list'", unless = "#result == null or #result.isEmpty()")
    public List<CategoryPublicResponse> getPublicCategories() {
        List<PublicCategoryProjection> projections = categoryRepository.findAllPublicCategoriesWithProductCount();

        return projections.stream()
                .map(proj -> new CategoryPublicResponse(
                        proj.getId(),
                        proj.getName(),
                        proj.getDescription(),
                        proj.getProductCount() != null ? proj.getProductCount() : 0L
                ))
                .toList();
    }

    private CategoryResponse mapToResponse(CategoryEntity entity) {
        return new CategoryResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }
}
