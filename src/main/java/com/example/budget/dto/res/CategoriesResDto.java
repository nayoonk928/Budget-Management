package com.example.budget.dto.res;

import java.util.List;

public record CategoriesResDto(
    List<CategoryResDto> categories
) {

    public record CategoryResDto(
        Long id,
        String name
    ) {
    }

}
