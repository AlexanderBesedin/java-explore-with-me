package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryDto categoryDto);

    CategoryDto patch(long catId, CategoryDto categoryDto);

    List<CategoryDto> getAll(int from, int size);

    CategoryDto get(long catId);

    void delete(long catId);
}
