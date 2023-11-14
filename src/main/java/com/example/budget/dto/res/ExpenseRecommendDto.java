package com.example.budget.dto.res;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ExpenseRecommendDto(
    Integer TotalBudget,
    String message,
    List<CategoryResDto> categories
) {

  public record CategoryResDto(
      String category,
      Integer budget
  ) {
  }

}
