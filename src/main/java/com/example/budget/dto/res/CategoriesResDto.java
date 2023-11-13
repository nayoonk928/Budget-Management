package com.example.budget.dto.res;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

public record CategoriesResDto(
    List<CategoryResDto> categories
) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record CategoryResDto(
        Long id,
        String name,
        Integer averageRate
    ) {
    }

}
