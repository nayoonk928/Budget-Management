package com.example.budget.dto.res;

import java.math.BigDecimal;
import java.util.List;

public record CategoriesResDto(
    List<CategoryResDto> categories
) {

    public record CategoryResDto(
        Long id,
        String name,
        BigDecimal averageRate
    ) {
    }

}
