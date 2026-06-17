package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.dto.category.CategoryRequest;
import dev.manuthlakdiw.primebasketbackend.dto.category.CategoryResponse;
import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.projection.CategoryDropdownProjection;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(long id, CategoryRequest request);

    String deleteCategory(long id);

    CategoryResponse getCategoryById(long id);

    PageResponse<CategoryResponse> getAllCategories(int page, int size);

    List<CategoryDropdownProjection> getCategoriesForDropdown();
}
