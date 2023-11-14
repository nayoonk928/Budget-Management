package com.example.budget.service.impl;

import com.example.budget.dto.res.CategoriesResDto;
import com.example.budget.repository.CategoryRepository;
import com.example.budget.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoriesResDto getCategories() {
        List<CategoriesResDto.CategoryResDto> categories = categoryRepository.findAll()
                .stream()
                .map(c -> new CategoriesResDto.CategoryResDto(c.getId(), c.getName()))
                .toList();

        return new CategoriesResDto(categories);
    }

}
