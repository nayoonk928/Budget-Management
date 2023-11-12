package com.example.budget.service.impl;

import com.example.budget.dto.req.BudgetCreateReqDto;
import com.example.budget.dto.res.BudgetsResDto;
import com.example.budget.dto.res.BudgetsResDto.BudgetDto;
import com.example.budget.entity.Budget;
import com.example.budget.entity.Category;
import com.example.budget.entity.Member;
import com.example.budget.exception.CustomException;
import com.example.budget.exception.ErrorCode;
import com.example.budget.repository.BudgetRepository;
import com.example.budget.repository.CategoryRepository;
import com.example.budget.service.BudgetService;
import com.example.budget.type.CategoryType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetServiceImpl implements BudgetService {

  private final BudgetRepository budgetRepository;
  private final CategoryRepository categoryRepository;

  /**
   * 사용자 예산 설정
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

    List<Budget> savedBudgets = budgetRepository.saveAll(updatedBudgets);
    List<BudgetsResDto.BudgetDto> updatedBudgetDtos = mapToBudgetDtos(savedBudgets);

    // 카테고리 평균 비율 업데이트
    updateCategoryAverageRate(categories, budgets, member);

    return new BudgetsResDto(updatedBudgetDtos, request.totalAmount());
  }

  /**
   * 사용자 예산 조회
   *
   * @param member 사용자 정보
   */
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

  /**
   * 총액만 입력한 사용자에게 예산 추천
   *
   * @param totalAmount 사용자 입력 총액
   */
  @Override
  public BudgetsResDto recommendBudget(BigDecimal totalAmount) {
    BigDecimal restAmount = totalAmount;
    List<BudgetsResDto.BudgetDto> budgetDtos = new ArrayList<>();
    List<Category> categories = categoryRepository.findAll();

    for (Category category : categories) {
      if (CategoryType.ETC.getName().equals(category.getName())) {
        continue;
      }

      BigDecimal categoryBudget = getCategoryBudget(category, totalAmount);
      restAmount = restAmount.subtract(categoryBudget);
      budgetDtos.add(new BudgetDto(category.getId(), category.getName(), categoryBudget));
    }

    Optional<Category> etcOptional = categories.stream()
        .filter(it -> it.getName().equals(CategoryType.ETC.getName()))
        .findFirst();
    if (etcOptional.isPresent()) {
      Category etcCategory = etcOptional.get();
      BigDecimal budgetAmount = restAmount.divide(new BigDecimal("1000"))
          .multiply(new BigDecimal("1000"));
      budgetDtos.add(new BudgetDto(etcCategory.getId(), etcCategory.getName(), budgetAmount));
    }
    return new BudgetsResDto(budgetDtos, totalAmount);
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
    return budgetDtos.stream().map(BudgetsResDto.BudgetDto::amount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  // ------ 카테고리 평균 비율 계산 관련 메서드 ------
  private void updateCategoryAverageRate(Map<Long, Category> categories, List<Budget> budgets,
      Member member) {
    BigDecimal restRate = BigDecimal.valueOf(100);
    BigDecimal budgetsSum = budgets.stream().map(Budget::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    Map<Long, Long> budgetCounts = budgetRepository.getCountGroupByCategory(member);

    for (Budget budget : budgets) {
      if (CategoryType.ETC.getName().equals(budget.getCategory().getName())) {
        continue;
      }

      long categoryId = budget.getCategory().getId();
      long categoryCount = budgetCounts.containsKey(categoryId) ? budgetCounts.get(categoryId) : 0;
      Category category = categories.get(categoryId);

      BigDecimal memberRate = budget.getAmount().divide(budgetsSum, 4, RoundingMode.HALF_UP)
          .multiply(BigDecimal.valueOf(100));
      BigDecimal categoryNewAverageRate = getCategoryNewAverageRate(category, categoryCount,
          memberRate);
      category.updateAverageRate(categoryNewAverageRate);
      restRate = restRate.subtract(categoryNewAverageRate);
    }

    Optional<Category> etcOptional = categories.values().stream()
        .filter(it -> it.getName().equals(CategoryType.ETC.getName()))
        .findFirst();
    if (etcOptional.isPresent()) {
      Category etcCategory = etcOptional.get();
      etcCategory.updateAverageRate(restRate);
    }
  }

  private BigDecimal getCategoryNewAverageRate(Category category, long categoryCount,
      BigDecimal rate) {
    BigDecimal categoryAverageRate =
        category.getAverageRate() != null ? category.getAverageRate() : BigDecimal.ZERO;
    BigDecimal categorySum = categoryAverageRate.multiply(BigDecimal.valueOf(categoryCount));
    return categorySum.add(rate)
        .divide(BigDecimal.valueOf(categoryCount + 1), 4, RoundingMode.HALF_UP);
  }

  private BigDecimal getCategoryBudget(Category category, BigDecimal totalAmount) {
    BigDecimal averageRate =
        category.getAverageRate() != null ? category.getAverageRate() : BigDecimal.ZERO;

    if (averageRate.compareTo(BigDecimal.TEN) >= 0) {
      return totalAmount.multiply(averageRate).divide(new BigDecimal("100000"))
          .multiply(new BigDecimal("1000"));
    } else {
      return BigDecimal.ZERO;
    }
  }

}
