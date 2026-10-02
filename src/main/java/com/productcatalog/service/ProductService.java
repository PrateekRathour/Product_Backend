package com.productcatalog.service;

import com.productcatalog.dto.PagedResponse;
import com.productcatalog.dto.product.ProductRequestDto;
import com.productcatalog.dto.product.ProductResponseDto;
import com.productcatalog.model.InventoryStatus;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    PagedResponse<ProductResponseDto> getProducts(
            String keyword,
            Long categoryId,
            String categorySlug,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            InventoryStatus inventoryStatus,
            String brand,
            Boolean featured,
            Boolean active,
            int page,
            int size,
            String sortBy,
            String sortDir);

    ProductResponseDto getProductById(Long id);

    ProductResponseDto getProductBySlug(String slug);

    ProductResponseDto createProduct(ProductRequestDto productRequestDto);

    ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto);

    ProductResponseDto toggleProductActive(Long id);

    void deleteProduct(Long id);

    List<ProductResponseDto> getFeaturedProducts();
}
