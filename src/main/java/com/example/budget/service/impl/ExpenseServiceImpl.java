package com.example.budget.service.impl;

import com.example.budget.dto.req.ExpenseCreateReqDto;
import com.example.budget.dto.res.ExpenseDetailResDto;
import com.example.budget.entity.Category;
import com.example.budget.entity.Expense;
import com.example.budget.entity.Member;
import com.example.budget.exception.CustomException;
import com.example.budget.exception.ErrorCode;
import com.example.budget.repository.CategoryRepository;
import com.example.budget.repository.ExpenseRepository;
import com.example.budget.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

  private final CategoryRepository categoryRepository;
  private final ExpenseRepository expenseRepository;

  @Override
  @Transactional
  public void createExpense(Member member, ExpenseCreateReqDto request) {
    Category category = categoryRepository.findByName(request.category())
        .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

    Expense expense = Expense.builder()
        .member(member)
        .category(category)
        .expendedAt(request.expendedAt())
        .amount(request.amount())
        .isExcludedSum(request.isExcludedSum())
        .description(request.description())
        .build();

    expenseRepository.save(expense);
  }

  @Override
  public ExpenseDetailResDto getExpenseDetail(Long expenseId) {
    return null;
  }

}
