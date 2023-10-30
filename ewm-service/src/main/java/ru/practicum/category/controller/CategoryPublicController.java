package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable long catId) {
        log.debug("Received category with ID = {}", catId);
        return categoryService.getById(catId);
    }

    @GetMapping
    public List<CategoryDto> getAll(@Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                    @Valid @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("All categories received");
        return categoryService.getAll(from, size);
    }
}
