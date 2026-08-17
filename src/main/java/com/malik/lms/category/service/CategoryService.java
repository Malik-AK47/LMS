package com.malik.lms.category.service;

import com.malik.lms.category.dto.request.CreateCategoryRequest;
import com.malik.lms.category.dto.request.UpdateCategoryRequest;
import com.malik.lms.category.dto.response.CategoryResponse;
import com.malik.lms.category.entity.Category;
import com.malik.lms.category.repository.CategoryRepository;
import com.malik.lms.course.repository.CourseRepository;
import com.malik.lms.exception.ConflictException;
import com.malik.lms.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public CategoryService(CategoryRepository categoryRepository, CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ConflictException("Category already exists");
        }
        Category category = new Category();
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription().trim());

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(savedCategory.getId(), savedCategory.getName(), savedCategory.getDescription());
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryResponse(category.getId(), category.getName(), category.getDescription()))
                .toList();
    }

    @Transactional
    public CategoryResponse updateCategory(Long categoryId, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (categoryRepository.existsByNameIgnoreCase(request.getName()) && !category.getName().equalsIgnoreCase(request.getName().trim())) {
            throw new ConflictException("Category already exists");
        }

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription().trim());

        Category updatedCategory = categoryRepository.save(category);

        return new CategoryResponse(updatedCategory.getId(), updatedCategory.getName(), updatedCategory.getDescription());
    }

    @Transactional
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (courseRepository.existsByCategoryId(categoryId)) {
            throw new ConflictException("Category cannot be deleted because it is being used by courses");
        }

        categoryRepository.delete(category);

        return "Category deleted successfully";
    }
}