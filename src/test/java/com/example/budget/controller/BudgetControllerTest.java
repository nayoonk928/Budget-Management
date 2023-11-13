package com.example.budget.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.budget.controller.common.ControllerTest;
import com.example.budget.dto.req.BudgetCreateReqDto;
import com.example.budget.entity.Budget;
import com.example.budget.entity.Category;
import com.example.budget.exception.ErrorCode;
import com.example.budget.repository.BudgetRepository;
import com.example.budget.repository.CategoryRepository;
import com.example.budget.service.BudgetService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class BudgetControllerTest extends ControllerTest {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private BudgetRepository budgetRepository;

  @Autowired
  private BudgetService budgetService;

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
  @DisplayName("예산 설정")
  class create_budget {

    @Test
    @DisplayName("성공: 예산 설정")
    void success_create_budget() throws Exception {
      //given
      List<BudgetCreateReqDto.BudgetDto> budgetDtos = categories.keySet().stream()
          .map(id -> new BudgetCreateReqDto.BudgetDto(id, (int) (1000 * id))).toList();

      int totalAmount = budgetDtos.stream().mapToInt(BudgetCreateReqDto.BudgetDto::amount).sum();

      BudgetCreateReqDto request = new BudgetCreateReqDto(budgetDtos, totalAmount);

      //when & then
      mockMvc.perform(post("/api/budgets")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.total_amount").value(totalAmount))
          .andDo(print())
          .andReturn();
    }

    @Test
    @DisplayName("성공: 예산 업데이트")
    void success_update_budget() throws Exception {
      //given
      List<BudgetCreateReqDto.BudgetDto> budgetDtos = categories.keySet().stream()
          .map(id -> new BudgetCreateReqDto.BudgetDto(id, (int) (1000 * id))).toList();

      int totalAmount = budgetDtos.stream().mapToInt(BudgetCreateReqDto.BudgetDto::amount).sum();

      BudgetCreateReqDto request = new BudgetCreateReqDto(budgetDtos, totalAmount);

      mockMvc.perform(post("/api/budgets")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.total_amount").value(totalAmount))
          .andDo(print())
          .andReturn();

      // 업데이트 객체
      List<BudgetCreateReqDto.BudgetDto> updatedBudgetDtos = categories.keySet().stream()
          .map(id -> new BudgetCreateReqDto.BudgetDto(id, (int) (1500 * id))).toList();

      int updatedTotalAmount = budgetDtos.stream().mapToInt(BudgetCreateReqDto.BudgetDto::amount).sum();

      BudgetCreateReqDto updatedRequest = new BudgetCreateReqDto(updatedBudgetDtos,
          updatedTotalAmount);

      mockMvc.perform(post("/api/budgets")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updatedRequest)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.total_amount").value(updatedTotalAmount))
          .andDo(print())
          .andReturn();

      //when & then
      List<Budget> updatedBudgets = budgetRepository.findAll();
      for (Budget budget : updatedBudgets) {
        Integer expectedAmount = updatedBudgetDtos.stream()
            .filter(dto -> dto.categoryId() == budget.getCategory().getId())
            .map(BudgetCreateReqDto.BudgetDto::amount)
            .findFirst()
            .orElse(null);

        Assertions.assertNotNull(expectedAmount);

        Assertions.assertEquals(budget.getAmount(), expectedAmount);
      }
    }

    @Test
    @DisplayName("실패: 카테고리 누락")
    void fail_create_budget_category() throws Exception {
      //given
      List<BudgetCreateReqDto.BudgetDto> budgetDtos = new ArrayList<>(categories.keySet().stream()
          .map(id -> new BudgetCreateReqDto.BudgetDto(id, (int) (1000 * id))).toList());
      budgetDtos.remove(0);
      int totalAmount = budgetDtos.stream().mapToInt(BudgetCreateReqDto.BudgetDto::amount).sum();

      BudgetCreateReqDto request = new BudgetCreateReqDto(budgetDtos, totalAmount);

      //when & then
      mockMvc.perform(post("/api/budgets")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error_code").value(ErrorCode.ALL_CATEGORIES_NOT_ROAD.name()))
          .andDo(print())
          .andReturn();
    }

  }

  @Nested
  @DisplayName("예산 조회")
  class get_budgets {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      List<BudgetCreateReqDto.BudgetDto> budgetDtos = categories.keySet().stream()
          .map(id -> new BudgetCreateReqDto.BudgetDto(id, (int) (1000 * id))).toList();

      int totalAmount = budgetDtos.stream().mapToInt(BudgetCreateReqDto.BudgetDto::amount).sum();

      BudgetCreateReqDto request = new BudgetCreateReqDto(budgetDtos, totalAmount);
      budgetService.createBudget(member, request);

      //when & then
      mockMvc.perform(get("/api/budgets")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andDo(print())
          .andReturn();
    }

    @Test
    @DisplayName("성공: 등록된 예산 설정 없음")
    void success_no_budgets() throws Exception {
      //given
      //when & then
      mockMvc.perform(get("/api/budgets")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andDo(print())
          .andReturn();
    }

  }

  @Nested
  @DisplayName("예산 추천")
  class recommend_budget {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      //when & then
      mockMvc.perform(get("/api/budgets/recommend")
              .param("total_amount", "400000")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andDo(print())
          .andReturn();
    }

  }

}