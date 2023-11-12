package com.example.budget.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.budget.repository.BudgetRepository;
import com.example.budget.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

  @InjectMocks
  private BudgetServiceImpl budgetService;

  @Mock
  private BudgetRepository budgetRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Nested
  @DisplayName("예산 생성 및 업데이트")
  class budget_create_or_update {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      //when
      //then
    }

  }

}