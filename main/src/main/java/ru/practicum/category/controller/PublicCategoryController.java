package ru.practicum.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.util.Constants;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(
            @RequestParam(name = Constants.REQ_PARAM_FROM, defaultValue = Constants.DEFAULT_PAGE_FROM)
            @PositiveOrZero Integer from,
            @RequestParam(name = Constants.REQ_PARAM_SIZE, defaultValue = Constants.DEFAULT_PAGE_SIZE)
            @Positive Integer size
    ) {
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable long catId) {
        return categoryService.getCategoryById(catId);
    }

}