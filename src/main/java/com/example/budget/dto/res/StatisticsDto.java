package com.example.budget.dto.res;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StatisticsDto(
    Integer lastExpense,
    Integer currentExpense,
    Integer increaseRate,
    List<CategoryResDto> categories
) {

  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public record CategoryResDto(
      String category,
      Integer lastExpense,
      Integer currentExpense,
      Integer increaseRate
  ) {

  }

}
