package com.productcatalog.service;

import com.productcatalog.dto.PagedResponse;
import com.productcatalog.dto.category.CategoryResponseDto;
import com.productcatalog.dto.inventory.InventoryResponseDto;
import com.productcatalog.dto.product.ProductRequestDto;
import com.productcatalog.dto.product.ProductResponseDto;
import com.productcatalog.exception.BadRequestException;
import com.productcatalog.exception.ResourceNotFoundException;
import com.productcatalog.model.*;
import com.productcatalog.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryAuditLogRepository auditLogRepository;

    private String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    public ProductResponseDto mapToDto(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setSku(product.getSku());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscountPrice(product.getDiscountPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setBrand(product.getBrand());
        dto.setFeatured(product.getFeatured());
        dto.setActive(product.getActive());
        dto.setRating(product.getRating());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        if (product.getCategory() != null) {
            Category cat = product.getCategory();
            CategoryResponseDto catDto = new CategoryResponseDto();
            catDto.setId(cat.getId());
            catDto.setName(cat.getName());
            catDto.setSlug(cat.getSlug());
            catDto.setDescription(cat.getDescription());
            catDto.setIcon(cat.getIcon());
            catDto.setBannerUrl(cat.getBannerUrl());
            catDto.setActive(cat.getActive());
            dto.setCategory(catDto);
        }

        if (product.getInventory() != null) {
            Inventory inv = product.getInventory();
            InventoryResponseDto invDto = new InventoryResponseDto();
            invDto.setId(inv.getId());
            invDto.setSku(inv.getSku());
            invDto.setStockQuantity(inv.getStockQuantity());
            invDto.setReservedQuantity(inv.getReservedQuantity());
            invDto.setAvailableQuantity(inv.getStockQuantity() - inv.getReservedQuantity());
            invDto.setLowStockThreshold(inv.getLowStockThreshold());
            invDto.setStatus(inv.getStatus());
            invDto.setWarehouseLocation(inv.getWarehouseLocation());
            invDto.setLastRestockedAt(inv.getLastRestockedAt());
            invDto.setUpdatedAt(inv.getUpdatedAt());
            dto.setInventory(invDto);
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponseDto> getProducts(
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
            String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = ProductSpecification.filter(
                keyword, categoryId, categorySlug, minPrice, maxPrice, inventoryStatus, brand, featured, active);

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<ProductResponseDto> content = productPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
        return mapToDto(product);
    }

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto dto) {
        if (productRepository.existsBySku(dto.getSku())) {
            throw new BadRequestException("Product with SKU '" + dto.getSku() + "' already exists");
        }

        String slug = dto.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = toSlug(dto.getName());
        }

        if (productRepository.existsBySlug(slug)) {
            slug = slug + "-" + (System.currentTimeMillis() % 10000);
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));

        Product product = new Product();
        product.setName(dto.getName());
        product.setSlug(slug);
        product.setSku(dto.getSku().toUpperCase().trim());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setDiscountPrice(dto.getDiscountPrice());
        product.setImageUrl(dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty() ?
                dto.getImageUrl() : "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=800&auto=format&fit=crop&q=80");
        product.setBrand(dto.getBrand() != null ? dto.getBrand() : "Brand");
        product.setFeatured(dto.getFeatured() != null ? dto.getFeatured() : false);
        product.setActive(dto.getActive() != null ? dto.getActive() : true);
        product.setRating(dto.getRating() != null ? dto.getRating() : BigDecimal.valueOf(5.0));
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        // Auto create associated inventory record
        Inventory inventory = new Inventory();
        inventory.setSku(savedProduct.getSku());
        inventory.setStockQuantity(dto.getInitialStock() != null ? dto.getInitialStock() : 0);
        inventory.setReservedQuantity(0);
        inventory.setLowStockThreshold(dto.getLowStockThreshold() != null ? dto.getLowStockThreshold() : 10);
        inventory.setWarehouseLocation(dto.getWarehouseLocation() != null ? dto.getWarehouseLocation() : "Main Warehouse (WH-A1)");
        inventory.setProduct(savedProduct);
        inventory.recalculateStatus();

        Inventory savedInventory = inventoryRepository.save(inventory);
        savedProduct.setInventory(savedInventory);

        // Record Initial Audit Log
        if (inventory.getStockQuantity() > 0) {
            InventoryAuditLog log = new InventoryAuditLog(
                    savedProduct.getId(),
                    savedProduct.getName(),
                    savedProduct.getSku(),
                    inventory.getStockQuantity(),
                    0,
                    inventory.getStockQuantity(),
                    "Initial product catalog intake",
                    "System Admin"
            );
            auditLogRepository.save(log);
        }

        return mapToDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (!product.getSku().equalsIgnoreCase(dto.getSku()) && productRepository.existsBySku(dto.getSku())) {
            throw new BadRequestException("Product with SKU '" + dto.getSku() + "' already exists");
        }

        if (dto.getCategoryId() != null && !product.getCategory().getId().equals(dto.getCategoryId())) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));
            product.setCategory(category);
        }

        product.setName(dto.getName());
        if (dto.getSlug() != null && !dto.getSlug().trim().isEmpty()) {
            product.setSlug(toSlug(dto.getSlug()));
        }
        product.setSku(dto.getSku().toUpperCase().trim());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setDiscountPrice(dto.getDiscountPrice());
        if (dto.getImageUrl() != null) product.setImageUrl(dto.getImageUrl());
        if (dto.getBrand() != null) product.setBrand(dto.getBrand());
        if (dto.getFeatured() != null) product.setFeatured(dto.getFeatured());
        if (dto.getActive() != null) product.setActive(dto.getActive());
        if (dto.getRating() != null) product.setRating(dto.getRating());

        // Update SKU in inventory if changed
        if (product.getInventory() != null && !product.getInventory().getSku().equals(product.getSku())) {
            product.getInventory().setSku(product.getSku());
        }

        Product updatedProduct = productRepository.save(product);
        return mapToDto(updatedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDto toggleProductActive(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setActive(!product.getActive());
        Product updated = productRepository.save(product);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndActiveTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
