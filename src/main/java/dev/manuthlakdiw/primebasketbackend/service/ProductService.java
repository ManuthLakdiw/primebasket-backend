package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.dto.product.ProductRequest;
import dev.manuthlakdiw.primebasketbackend.dto.product.ProductResponse;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    PageResponse<ProductResponse> getAllProducts(int page, int size);

    ProductResponse updateProduct(Long id, ProductRequest request);

    String deleteProduct(Long id);

    ProductResponse getProductById(Long id);

    void updateProductActiveStatus(Long id, boolean isActive);

    void setProductAsFeatured(Long id, boolean isFeatured);

    PageResponse<ProductResponse> getProductsByCategory(Long categoryId, String keyword, int page, int size);

    PageResponse<ProductResponse> getFeaturedProducts(String keyword, int page, int size);

    List<ProductResponse> getTopFeaturedProducts(int limit);

    List<ProductResponse> getTopOnSaleProducts(int limit);

    PageResponse<ProductResponse> getOnSaleProducts(String keyword, int page, int size);

}
