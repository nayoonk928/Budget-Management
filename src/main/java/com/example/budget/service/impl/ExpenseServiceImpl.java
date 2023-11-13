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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
  public ExpenseDetailResDto createExpense(Member member, ExpenseCreateReqDto request) {
    Category category = categoryFindByName(request.category());
    String formattedDate = setDateFormat(request.expendedAt());

    Expense expense = Expense.builder()
        .member(member)
        .category(category)
        .expendedAt(formattedDate)
        .amount(request.amount())
        .isExcludedSum(request.isExcludedSum())
        .description(request.description())
        .build();
    expenseRepository.save(expense);
    return getExpenseDetailDto(expense);
  }

  @Override
  @Transactional
  public ExpenseDetailResDto updateExpense(Member member, Long expenseId,
      ExpenseCreateReqDto request) {
    Expense expense = expenseFindByIdAndMember(expenseId, member);
    Category category = categoryFindByName(request.category());
    String formattedDate = setDateFormat(request.expendedAt());

    expense.update(category, formattedDate, request.amount(), request.isExcludedSum(),
        request.description());
    return getExpenseDetailDto(expense);
  }

  @Override
  public ExpenseDetailResDto getExpenseDetail(Member member, Long expenseId) {
    Expense expense = expenseFindByIdAndMember(expenseId, member);
    return getExpenseDetailDto(expense);
  }

  private Expense expenseFindByIdAndMember(Long expenseId, Member member) {
    return expenseRepository.findByIdAndMember(expenseId, member)
        .orElseThrow(() -> new CustomException(ErrorCode.EXPENSE_NOT_FOUND));
  }

  private Category categoryFindByName(String name) {
    return categoryRepository.findByName(name)
        .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
  }

  private ExpenseDetailResDto getExpenseDetailDto(Expense expense) {
    return ExpenseDetailResDto.builder()
        .expenseId(expense.getId())
        .expendedAt(expense.getExpendedAt())
        .amount(expense.getAmount())
        .category(expense.getCategory().getName())
        .isExcludedSum(expense.getIsExcludedSum())
        .description(expense.getDescription())
        .build();
  }

  private String setDateFormat(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return date.format(formatter);
  }

}
