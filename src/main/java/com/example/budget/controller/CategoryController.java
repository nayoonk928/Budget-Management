package com.example.budget.controller;

import com.example.budget.dto.res.CategoriesResDto;
import com.example.budget.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<CategoriesResDto> getCategories() {
        return ResponseEntity.ok().body(categoryService.getCategories());
    }

}
