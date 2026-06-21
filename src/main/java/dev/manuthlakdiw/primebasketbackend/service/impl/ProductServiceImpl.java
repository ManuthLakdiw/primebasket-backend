package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.dto.product.ProductRequest;
import dev.manuthlakdiw.primebasketbackend.dto.product.ProductResponse;
import dev.manuthlakdiw.primebasketbackend.entity.CategoryEntity;
import dev.manuthlakdiw.primebasketbackend.entity.ProductEntity;
import dev.manuthlakdiw.primebasketbackend.repository.CategoryRepository;
import dev.manuthlakdiw.primebasketbackend.repository.ProductRepository;
import dev.manuthlakdiw.primebasketbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsBySkuAndIsDeletedFalse(request.sku())) {
            throw new RuntimeException("Product SKU already exists!");
        }
        if (productRepository.existsByNameAndIsDeletedFalse(request.name())) {
            throw new RuntimeException("Product name already exists!");
        }

        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.categoryId()));


        ProductEntity product = ProductEntity.builder()
                .sku(request.sku())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .salePrice(request.salePrice())
                .saleStartDate(request.saleStartDate())
                .saleEndDate(request.saleEndDate())
                .images(request.images())
                .attributes(request.attributes())
                .isFeatured(request.isFeatured())
                .isActive(request.isActive())
                .isDeleted(false)
                .category(category)
                .build();

        ProductEntity savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    @Override
    @Cacheable(value = "products", key = "#page + '-' + #size", unless = "#result == null")
    public PageResponse<ProductResponse> getAllProducts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<ProductEntity> productPage = productRepository.findByIsDeletedFalse(pageRequest);
        Page<ProductResponse> responsePage = productPage.map(this::mapToResponse);
        return PageResponse.from(responsePage);
    }

    @Transactional
    @Override
    @CachePut(value = "product", key = "#id")
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        String incomingSku = request.sku().trim();
        if (!existingProduct.getSku().equalsIgnoreCase(incomingSku) &&
                productRepository.existsBySkuAndIsDeletedFalse(incomingSku)) {
            throw new RuntimeException("Product SKU already exists!");
        }

        String incomingName = request.name().trim();
        if (!existingProduct.getName().equalsIgnoreCase(incomingName) &&
                productRepository.existsByNameAndIsDeletedFalse(incomingName)) {
            throw new RuntimeException("Product name already exists!");
        }

        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.categoryId()));

        existingProduct.setSku(request.sku());
        existingProduct.setName(request.name());
        existingProduct.setDescription(request.description());
        existingProduct.setPrice(request.price());
        existingProduct.setStockQuantity(request.stockQuantity());
        existingProduct.setSalePrice(request.salePrice());
        existingProduct.setSaleStartDate(request.saleStartDate());
        existingProduct.setSaleEndDate(request.saleEndDate());
        existingProduct.setImages(request.images());
        existingProduct.setAttributes(request.attributes());
        existingProduct.setFeatured(request.isFeatured());
        existingProduct.setActive(request.isActive());
        existingProduct.setDeleted(false);
        existingProduct.setCategory(category);

        return mapToResponse(existingProduct);
    }

    @Transactional
    @Override
    @CacheEvict(value = {"product", "products"}, allEntries = true)
    public String deleteProduct(Long id) {
        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        if (existingProduct.isDeleted()) {
            throw new RuntimeException("Product is already deleted!");
        }

        existingProduct.setDeleted(true);
        existingProduct.setActive(false);
        existingProduct.setFeatured(false);

        return "Product deleted successfully!";
    }

    @Override
    @Cacheable(value = "product", key = "#id", unless = "#result == null")
    public ProductResponse getProductById(Long id) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        if (productEntity.isDeleted()) {
            throw new RuntimeException("Cannot retrieve deleted product! contact support to restore it.");
        }

        return mapToResponse(productEntity);
    }

    @Transactional
    @Override
    @CacheEvict(value = {"product", "products"}, allEntries = true)
    public void updateProductActiveStatus(Long id, boolean isActive) {
        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        if (existingProduct.isDeleted()) {
            throw new RuntimeException("Cannot update status of a deleted product!");
        }

        existingProduct.setActive(isActive);
    }

    @Transactional
    @Override
    @CacheEvict(value = {"product", "products"}, allEntries = true)
    public void setProductAsFeatured(Long id, boolean isFeatured) {
        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        if (existingProduct.isDeleted()) {
            throw new RuntimeException("Cannot update status of a deleted product!");
        }

        existingProduct.setFeatured(isFeatured);
    }


    private ProductResponse mapToResponse(ProductEntity entity) {
        String rawStockStatus = entity.getStockQuantity() <= 0 ? "OUT_OF_STOCK" :
                (entity.getStockQuantity() < 5 ? "LOW_STOCK" : "IN_STOCK");
        String optimizedStockStatus = rawStockStatus.intern();

        String categoryName = entity.getCategory().getName();
        String optimizedCategoryName = (categoryName != null) ? categoryName.intern() : null;

        return new ProductResponse(
                entity.getId(),
                entity.getSku(),
                entity.getName(),
                entity.getDescription(),

                entity.getPrice(),
                entity.getSellingPrice(),
                entity.isOnSale(),

                entity.getStockQuantity(),
                optimizedStockStatus,

                entity.getImages(),
                entity.getAttributes(),
                entity.isFeatured(),
                entity.isActive(),

                new ProductResponse.ProductCategoryResponse(
                        entity.getCategory().getId(),
                        optimizedCategoryName
                )
        );
    }

}
