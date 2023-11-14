package com.example.budget.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.budget.config.ExpenseDataInit;
import com.example.budget.controller.common.ControllerTest;
import com.example.budget.dto.req.BudgetCreateReqDto;
import com.example.budget.dto.req.BudgetCreateReqDto.BudgetDto;
import com.example.budget.entity.Category;
import com.example.budget.exception.ErrorCode;
import com.example.budget.service.BudgetService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ConsultingControllerTest extends ControllerTest {

  @Autowired
  private BudgetService budgetService;

  @Autowired
  private ExpenseDataInit expenseDataInit;

  private Map<Long, Category> categories;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    categories = categoryRepository.findAll().stream()
        .collect(Collectors.toMap(Category::getId, category -> category)
        );
  }

  @Nested
  @DisplayName("오늘 지출 추천")
  class recommend_daily_budget {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      createBudgets();
      expenseDataInit.generateTestData(member, 100);

      //when & then
      mockMvc.perform(get("/api/expenses/today/recommend")
              .header("Authorization", "Bearer " + accessToken))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.total_budget").isNumber())
          .andExpect(jsonPath("$.categories[0].budget").isNumber())
          .andExpect(jsonPath("$.message").isString())
          .andDo(print())
          .andReturn();
    }

  }

  @Nested
  @DisplayName("오늘 지출 안내")
  class get_daily_report {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      createBudgets();
      expenseDataInit.generateTestData(member, 100);

      //when & then
      mockMvc.perform(get("/api/expenses/today")
              .header("Authorization", "Bearer " + accessToken))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.recommend_expense").isNumber())
          .andExpect(jsonPath("$.spent_expense").isNumber())
          .andExpect(jsonPath("$.risk").isNumber())
          .andExpect(jsonPath("$.categories").isArray())
          .andDo(print())
          .andReturn();
    }

    @Test
    @DisplayName("실패: 예산 설정 내역 없음")
    void fail_not_found_budget() throws Exception {
      //given
      expenseDataInit.generateTestData(member, 100);

      //when & then
      mockMvc.perform(get("/api/expenses/today")
              .header("Authorization", "Bearer " + accessToken))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.error_code").value(ErrorCode.BUDGET_NOT_FOUND.name()))
          .andDo(print());
    }

  }

  private void createBudgets() {
    List<BudgetDto> budgetDtos = categories.keySet().stream()
        .map(id -> new BudgetCreateReqDto.BudgetDto(id, (int) (10000 * id))).toList();

    int totalAmount = budgetDtos.stream().mapToInt(BudgetCreateReqDto.BudgetDto::amount).sum();

    BudgetCreateReqDto request = new BudgetCreateReqDto(budgetDtos, totalAmount);
    budgetService.createBudget(member, request);
  }

}