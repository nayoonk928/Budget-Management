package com.example.budget.service.impl;

import com.example.budget.dto.req.ExpenseCreateReqDto;
import com.example.budget.dto.res.ExpenseDetailResDto;
import com.example.budget.dto.res.ExpensesResDto;
import com.example.budget.dto.res.ExpensesResDto.ExpenseResDto;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    Expense expense = Expense.builder()
        .member(member)
        .category(category)
        .expendedAt(request.expendedAt())
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

    expense.update(category, request.expendedAt(), request.amount(), request.isExcludedSum(),
        request.description());
    return getExpenseDetailDto(expense);
  }

  @Override
  public ExpenseDetailResDto getExpenseDetail(Member member, Long expenseId) {
    Expense expense = expenseFindByIdAndMember(expenseId, member);
    return getExpenseDetailDto(expense);
  }

  @Override
  public ExpensesResDto getExpenses(Member member, LocalDate startDate, LocalDate endDate,
      Integer minAmount, Integer maxAmount, String categoryType, Pageable pageable) {

    List<Expense> filteredExpenses = expenseRepository.filterExpenses(member, startDate,
        endDate, minAmount, maxAmount, categoryType, pageable);

    int totalAmount = 0;
    List<ExpensesResDto.CategoryResDto> categoryResDtos = new ArrayList<>();
    List<ExpensesResDto.ExpenseResDto> expenseResDtos = new ArrayList<>();
    Map<String, Integer> categoryAmountMap = new HashMap<>();

    for (Expense expense : filteredExpenses) {
      expenseResDtos.add(mapExpenseToDto(expense));
      totalAmount += expense.getAmount();
      String category = expense.getCategory().getName();
      categoryAmountMap.merge(category, expense.getAmount(), Integer::sum);
    }

    for (Map.Entry<String, Integer> entry : categoryAmountMap.entrySet()) {
      categoryResDtos.add(new ExpensesResDto.CategoryResDto(entry.getKey(), entry.getValue()));
    }

    return new ExpensesResDto(totalAmount, categoryResDtos, expenseResDtos);
  }

  @Override
  @Transactional
  public void deleteExpense(Member member, Long expenseId) {
    Expense expense = expenseFindByIdAndMember(expenseId, member);
    expenseRepository.delete(expense);
  }

  private ExpensesResDto.ExpenseResDto mapExpenseToDto(Expense expense) {
      StringBuilder description = new StringBuilder();
      if (expense.getDescription().length() > 15) {
        description.append(expense.getDescription(), 0, 15).append("...");
      } else {
        description.append(expense.getDescription());
      }

      return ExpenseResDto.builder()
          .expenseId(expense.getId())
          .expendedAt(setDateFormat(expense.getExpendedAt()))
          .amount(expense.getAmount())
          .category(expense.getCategory().getName())
          .description(description.toString())
          .build();
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
        .expendedAt(setDateFormat(expense.getExpendedAt()))
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
