package com.productcatalog.service;

import com.productcatalog.dto.category.CategoryRequestDto;
import com.productcatalog.dto.category.CategoryResponseDto;
import com.productcatalog.exception.BadRequestException;
import com.productcatalog.exception.ResourceNotFoundException;
import com.productcatalog.model.Category;
import com.productcatalog.repository.CategoryRepository;
import com.productcatalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    private CategoryResponseDto mapToDto(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setIcon(category.getIcon());
        dto.setBannerUrl(category.getBannerUrl());
        dto.setActive(category.getActive());
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setProductCount(productRepository.countByCategoryId(category.getId()));
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories(boolean onlyActive) {
        List<Category> categories = onlyActive
                ? categoryRepository.findByActiveTrueOrderByDisplayOrderAsc()
                : categoryRepository.findAll();

        return categories.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
        return mapToDto(category);
    }

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        if (categoryRepository.existsByName(categoryRequestDto.getName())) {
            throw new BadRequestException("Category with name '" + categoryRequestDto.getName() + "' already exists");
        }

        String slug = categoryRequestDto.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = toSlug(categoryRequestDto.getName());
        }

        if (categoryRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis() % 1000;
        }

        Category category = new Category();
        category.setName(categoryRequestDto.getName());
        category.setSlug(slug);
        category.setDescription(categoryRequestDto.getDescription());
        category.setIcon(categoryRequestDto.getIcon() != null ? categoryRequestDto.getIcon() : "folder");
        category.setBannerUrl(categoryRequestDto.getBannerUrl());
        category.setActive(categoryRequestDto.getActive() != null ? categoryRequestDto.getActive() : true);
        category.setDisplayOrder(categoryRequestDto.getDisplayOrder() != null ? categoryRequestDto.getDisplayOrder() : 0);

        Category saved = categoryRepository.save(category);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (!category.getName().equalsIgnoreCase(categoryRequestDto.getName()) &&
                categoryRepository.existsByName(categoryRequestDto.getName())) {
            throw new BadRequestException("Category with name '" + categoryRequestDto.getName() + "' already exists");
        }

        category.setName(categoryRequestDto.getName());
        if (categoryRequestDto.getSlug() != null && !categoryRequestDto.getSlug().trim().isEmpty()) {
            category.setSlug(toSlug(categoryRequestDto.getSlug()));
        }
        category.setDescription(categoryRequestDto.getDescription());
        if (categoryRequestDto.getIcon() != null) category.setIcon(categoryRequestDto.getIcon());
        if (categoryRequestDto.getBannerUrl() != null) category.setBannerUrl(categoryRequestDto.getBannerUrl());
        if (categoryRequestDto.getActive() != null) category.setActive(categoryRequestDto.getActive());
        if (categoryRequestDto.getDisplayOrder() != null) category.setDisplayOrder(categoryRequestDto.getDisplayOrder());

        Category updated = categoryRepository.save(category);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        long count = productRepository.countByCategoryId(id);
        if (count > 0) {
            throw new BadRequestException("Cannot delete category '" + category.getName() + "' because it contains " + count + " active products. Please reassign or delete the products first.");
        }

        categoryRepository.delete(category);
    }
}
