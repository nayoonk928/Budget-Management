package com.example.budget.service.impl;

import com.example.budget.dto.req.BudgetCreateReqDto;
import com.example.budget.dto.res.BudgetsResDto;
import com.example.budget.entity.Budget;
import com.example.budget.entity.Category;
import com.example.budget.entity.Member;
import com.example.budget.exception.CustomException;
import com.example.budget.exception.ErrorCode;
import com.example.budget.repository.BudgetRepository;
import com.example.budget.repository.CategoryRepository;
import com.example.budget.service.BudgetService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetServiceImpl implements BudgetService {

  private final BudgetRepository budgetRepository;
  private final CategoryRepository categoryRepository;

  /**
   * 사용자 예산 설정/업데이트
   *
   * @param member  사용자 정보
   * @param request 예산 설정 요청 DTO
   */
  @Override
  @Transactional
  public BudgetsResDto createBudget(Member member, BudgetCreateReqDto request) {
    Map<Long, Category> categories = loadCategories();

    validateCategoryCount(request, categories);

    List<Budget> budgets = budgetRepository.findByMember(member);
    List<Budget> updatedBudgets = new ArrayList<>();

    for (BudgetCreateReqDto.BudgetDto budgetDto : request.budgets()) {
      Long categoryId = budgetDto.categoryId();
      BigDecimal amount = budgetDto.amount();

      Optional<Budget> existingBudget = findBudgetByCategory(budgets, categoryId);

      if (existingBudget.isPresent()) {
        updateExistingBudget(existingBudget.get(), amount);
        updatedBudgets.add(existingBudget.get());
      } else {
        createNewBudget(updatedBudgets, member, categories, categoryId, amount);
      }
    }

    validateTotalBudgetAmount(request, budgets);
    List<Budget> savedBudgets = budgetRepository.saveAll(updatedBudgets);
    List<BudgetsResDto.BudgetDto> updatedBudgetDtos = mapToBudgetDtos(savedBudgets);

    return new BudgetsResDto(updatedBudgetDtos, request.totalAmount());
  }

  @Override
  public BudgetsResDto getBudgets(Member member) {
    List<Budget> budgets = budgetRepository.findAllByMember(member);

    if (budgets.isEmpty()) {
      return createEmptyBudgetsResDto();
    }

    List<BudgetsResDto.BudgetDto> budgetDtos = budgets.stream()
        .map(budget -> new BudgetsResDto.BudgetDto(budget.getCategory().getId(),
            budget.getCategory().getName(), budget.getAmount()))
        .collect(Collectors.toList());

    BigDecimal totalAmount = calculateTotalAmount(budgetDtos);

    return new BudgetsResDto(budgetDtos, totalAmount);
  }

  private BudgetsResDto recommendBudgets(Member member, BigDecimal totalAmount) {
    return null;
  }

  // ------ createBudgets() 관련 메서드 ------

  private Map<Long, Category> loadCategories() {
    return categoryRepository.findAll()
        .stream().collect(Collectors.toMap(Category::getId, category -> category));
  }

  private void validateCategoryCount(BudgetCreateReqDto request, Map<Long, Category> categories) {
    if (request.budgets().size() < categories.size()) {
      throw new CustomException(ErrorCode.ALL_CATEGORIES_NOT_ROAD);
    }
  }

  private Optional<Budget> findBudgetByCategory(List<Budget> budgets, Long categoryId) {
    return budgets.stream()
        .filter(budget -> budget.getCategory().getId().equals(categoryId))
        .findFirst();
  }

  private void updateExistingBudget(Budget budget, BigDecimal amount) {
    budget.updateAmount(amount);
  }

  private void createNewBudget(List<Budget> updatedBudgets, Member member,
      Map<Long, Category> categories, Long categoryId, BigDecimal amount) {
    Category category = categories.get(categoryId);
    if (category != null) {
      Budget newBudget = Budget.builder()
          .member(member)
          .category(category)
          .amount(amount)
          .build();
      updatedBudgets.add(newBudget);
    }
  }

  private void validateTotalBudgetAmount(BudgetCreateReqDto request, List<Budget> budgets) {
    BigDecimal categoriesTotalAmount = budgets.stream()
        .map(Budget::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (!request.totalAmount().equals(categoriesTotalAmount)) {
      throw new CustomException(ErrorCode.INVALID_TOTAL_BUDGET_AMOUNT);
    }
  }

  private List<BudgetsResDto.BudgetDto> mapToBudgetDtos(List<Budget> savedBudgets) {
    return savedBudgets.stream()
        .map(budget -> new BudgetsResDto.BudgetDto(
            budget.getCategory().getId(),
            budget.getCategory().getName(),
            budget.getAmount()
        ))
        .collect(Collectors.toList());
  }

  // ------ getBudgets() 관련 메서드 ------
  private BudgetsResDto createEmptyBudgetsResDto() {
    Map<Long, Category> categories = loadCategories();
    List<BudgetsResDto.BudgetDto> budgetDtos = categories.values().stream()
        .map(category -> new BudgetsResDto.BudgetDto(category.getId(), category.getName(),
            BigDecimal.ZERO))
        .collect(Collectors.toList());

    return new BudgetsResDto(budgetDtos, BigDecimal.ZERO);
  }

  private BigDecimal calculateTotalAmount(List<BudgetsResDto.BudgetDto> budgetDtos) {
    return budgetDtos.stream()
        .map(BudgetsResDto.BudgetDto::amount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

}
