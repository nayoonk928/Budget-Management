package com.example.budget.service.impl;

import static com.example.budget.dto.res.BudgetsResDto.BudgetDto.mapToBudgetDtos;

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
import com.example.budget.repository.MemberRepository;
import com.example.budget.service.BudgetService;
import com.example.budget.type.CategoryType;
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
  private final MemberRepository memberRepository;

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
      Integer amount = budgetDto.amount();

      Optional<Budget> existingBudget = findBudgetByCategory(budgets, categoryId);

      if (existingBudget.isPresent()) {
        existingBudget.get().updateAmount(amount);
        updatedBudgets.add(existingBudget.get());
      } else {
        createNewBudget(updatedBudgets, member, categories, categoryId, amount);
      }
    }

    // 사용자 예산 비율 업데이트
    updateCategoryRateByMember(budgets);

    // 카테고리 비율 업데이트
    updateCategoryAverageRate();

    List<Budget> savedBudgets = budgetRepository.saveAll(updatedBudgets);
    List<BudgetsResDto.BudgetDto> updatedBudgetDtos = mapToBudgetDtos(savedBudgets);

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
        .map(budget -> new BudgetDto(budget.getCategory().getId(),
            budget.getCategory().getName(), budget.getAmount()))
        .collect(Collectors.toList());

    int totalAmount = BudgetsResDto.getTotalAmount(budgetDtos);

    return new BudgetsResDto(budgetDtos, totalAmount);
  }

  /**
   * 총액만 입력한 사용자에게 예산 추천
   *
   * @param totalAmount 사용자 입력 총액
   */
  @Override
  public BudgetsResDto recommendBudget(Integer totalAmount) {
    int restAmount = totalAmount;
    List<BudgetsResDto.BudgetDto> budgetDtos = new ArrayList<>();
    List<Category> categories = categoryRepository.findAll();

    for (Category category : categories) {
      if (CategoryType.ETC.getName().equals(category.getName())) {
        continue;
      }

      int categoryBudget = getCategoryBudget(category, totalAmount);
      restAmount -= categoryBudget;
      budgetDtos.add(new BudgetDto(category.getId(), category.getName(), categoryBudget));
    }

    Optional<Category> etcOptional = categories.stream()
        .filter(it -> it.getName().equals(CategoryType.ETC.getName()))
        .findFirst();
    if (etcOptional.isPresent()) {
      Category etcCategory = etcOptional.get();
      int budgetAmount = (restAmount / 1000) * 1000;
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

  private void createNewBudget(List<Budget> updatedBudgets, Member member,
      Map<Long, Category> categories, Long categoryId, Integer amount) {
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

  // ------ getBudgets() 관련 메서드 ------
  private BudgetsResDto createEmptyBudgetsResDto() {
    Map<Long, Category> categories = loadCategories();
    List<BudgetsResDto.BudgetDto> budgetDtos = categories.values().stream()
        .map(category -> new BudgetsResDto.BudgetDto(category.getId(), category.getName(), 0))
        .collect(Collectors.toList());

    return new BudgetsResDto(budgetDtos, 0);
  }

  // ------ 카테고리 평균 비율 계산 메서드 ------
  private void updateCategoryRateByMember(List<Budget> memberBudgets) {
    long totalAmount = memberBudgets.stream().mapToLong(Budget::getAmount).sum();

    for (Budget memberBudget : memberBudgets) {
      double rate = (((double) memberBudget.getAmount() / totalAmount) * 100);
      memberBudget.updateRate(rate);
    }
  }

  private void updateCategoryAverageRate() {
    Map<Long, Double> categoryAverageRates = budgetRepository.getCategoryAverageRates();

    for (Map.Entry<Long, Double> entry : categoryAverageRates.entrySet()) {
      Long categoryId = entry.getKey();
      Double averageRate = entry.getValue();

      Category category = categoryRepository.findById(categoryId).orElse(null);

      if (category != null) {
        int roundedRate = (int) Math.round(averageRate);
        category.updateAverageRate(roundedRate);
        categoryRepository.save(category);
      }
    }
  }

  private int getCategoryBudget(Category category, int totalAmount) {
    int averageRate = category.getAverageRate() != null ? category.getAverageRate() : 0;
    return averageRate >= 10 ? ((totalAmount * averageRate) / (100 * 1000)) * 1000 : 0;
  }

}
