package com.example.budget.dto.res;

import com.example.budget.entity.Budget;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BudgetsResDto(
    List<BudgetDto> budgets,
    Integer totalAmount
) {

  @Builder
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public record BudgetDto(
      Long categoryId,
      String categoryName,
      Integer amount
  ) {

    public static List<BudgetDto> mapToBudgetDtos(List<Budget> budgets) {
      return budgets.stream()
          .map(budget -> new BudgetsResDto.BudgetDto(
              budget.getCategory().getId(),
              budget.getCategory().getName(),
              budget.getAmount()
          ))
          .collect(Collectors.toList());
    }

  }

  public static int getTotalAmount(List<BudgetDto> budgets) {
    return budgets.stream().mapToInt(BudgetsResDto.BudgetDto::amount).sum();
  }

}
