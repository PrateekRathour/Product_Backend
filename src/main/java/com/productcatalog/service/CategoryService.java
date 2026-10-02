package com.productcatalog.service;

import com.productcatalog.dto.category.CategoryRequestDto;
import com.productcatalog.dto.category.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    List<CategoryResponseDto> getAllCategories(boolean onlyActive);

    CategoryResponseDto getCategoryById(Long id);

    CategoryResponseDto getCategoryBySlug(String slug);

    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequestDto);

    void deleteCategory(Long id);
}
