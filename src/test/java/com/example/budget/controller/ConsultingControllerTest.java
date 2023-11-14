package com.example.budget.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.budget.controller.common.ControllerTest;
import com.example.budget.dto.req.BudgetCreateReqDto;
import com.example.budget.dto.req.BudgetCreateReqDto.BudgetDto;
import com.example.budget.dto.req.ExpenseCreateReqDto;
import com.example.budget.entity.Category;
import com.example.budget.service.BudgetService;
import com.example.budget.service.ExpenseService;
import java.time.LocalDate;
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
  private ExpenseService expenseService;

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
      createExpenses();

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

  private void createBudgets() {
    List<BudgetDto> budgetDtos = categories.keySet().stream()
        .map(id -> new BudgetCreateReqDto.BudgetDto(id, (int) (10000 * id))).toList();

    int totalAmount = budgetDtos.stream().mapToInt(BudgetCreateReqDto.BudgetDto::amount).sum();

    BudgetCreateReqDto request = new BudgetCreateReqDto(budgetDtos, totalAmount);
    budgetService.createBudget(member, request);
  }

  private void createExpenses() {
    expenseService.createExpense(member,
        ExpenseCreateReqDto.builder()
            .expendedAt(LocalDate.of(2023, 11, 13))
            .amount(10000)
            .category("식비")
            .isExcludedSum(false)
            .description("서브웨이 먹음")
            .build());
    expenseService.createExpense(member,
        ExpenseCreateReqDto.builder()
            .expendedAt(LocalDate.of(2023, 11, 1))
            .amount(4000)
            .category("교통")
            .isExcludedSum(false)
            .description("지하철")
            .build());
    expenseService.createExpense(member,
        ExpenseCreateReqDto.builder()
            .expendedAt(LocalDate.of(2023, 10, 30))
            .amount(10000)
            .category("쇼핑")
            .isExcludedSum(false)
            .description("잠옷 삼")
            .build());
    expenseService.createExpense(member,
        ExpenseCreateReqDto.builder()
            .expendedAt(LocalDate.of(2023, 11, 10))
            .amount(30000)
            .category("의료/건강")
            .isExcludedSum(false)
            .description("영양제")
            .build());
    expenseService.createExpense(member,
        ExpenseCreateReqDto.builder()
            .expendedAt(LocalDate.of(2023, 11, 2))
            .amount(3000)
            .category("생활")
            .isExcludedSum(false)
            .description("다이소 욕실화")
            .build());
    expenseService.createExpense(member,
        ExpenseCreateReqDto.builder()
            .expendedAt(LocalDate.of(2023, 11, 9))
            .amount(300000)
            .category("기타")
            .isExcludedSum(false)
            .description("용돈")
            .build());
    expenseService.createExpense(member,
        ExpenseCreateReqDto.builder()
            .expendedAt(LocalDate.of(2023, 11, 5))
            .amount(10000)
            .category("식비")
            .isExcludedSum(false)
            .description("서브웨이 먹음")
            .build());
    expenseService.createExpense(member,
        ExpenseCreateReqDto.builder()
            .expendedAt(LocalDate.of(2023, 11, 13))
            .amount(40000)
            .category("교통")
            .isExcludedSum(false)
            .description("왕복 시외버스 예매")
            .build());
    expenseService.createExpense(member,
        ExpenseCreateReqDto.builder()
            .expendedAt(LocalDate.now())
            .amount(10400)
            .category("여가")
            .isExcludedSum(false)
            .description("유튜브 프리미엄")
            .build());
  }

}