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
import java.util.HashMap;
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
   * 사용자 예산을 설정/업데이트/추천
   *
   * @param member  사용자 정보
   * @param request 예산 설정 요청 DTO
   */
  @Override
  @Transactional
  public BudgetsResDto createBudget(Member member, BudgetCreateReqDto request) {
    Map<Long, Category> categories = categoryRepository.findAll()
        .stream().collect(Collectors.toMap(Category::getId, category -> category));

    log.info("budgets size: {}", request.budgets().size());
    log.info("categories size: {}", categories.size());

    if (request.budgets().size() < categories.size()) {
      throw new CustomException(ErrorCode.ALL_CATEGORIES_NOT_ROAD);
    }

    List<BudgetCreateReqDto.BudgetDto> budgetDtos = request.budgets().stream().toList();

    // 예산 계획 없다면 추천, 있다면 생성 또는 업데이트
    if (budgetDtos.isEmpty()) {
      return recommendBudgets(member, request.totalAmount());
    } else {
      return createOrUpdateBudgets(member, budgetDtos, request.totalAmount(), categories);
    }
  }

  @Override
  public BudgetsResDto getBudgets(Member member) {
    List<Budget> budgets = budgetRepository.findAllByMember(member);

    if (budgets.isEmpty()) {
      // budgets가 비어있을 때, 모든 카테고리에 0원을 할당한 BudgetDto 목록을 생성
      List<Category> categories = categoryRepository.findAll();
      List<BudgetsResDto.BudgetDto> budgetDtos = categories.stream()
          .map(category -> new BudgetsResDto.BudgetDto(category.getId(), category.getName(), BigDecimal.ZERO))
          .collect(Collectors.toList());

      // BudgetsResDto를 생성하여 반환
      return new BudgetsResDto(budgetDtos, BigDecimal.ZERO);
    }

    // budgets가 비어있지 않으면, budgets를 가지고 BudgetDto 목록을 생성하고, 총 금액을 계산하여 BudgetsResDto를 반환
    List<BudgetsResDto.BudgetDto> budgetDtos = budgets.stream()
        .map(budget -> new BudgetsResDto.BudgetDto(budget.getCategory().getId(), budget.getCategory()
            .getName(), budget.getAmount()))
        .collect(Collectors.toList());

    BigDecimal totalAmount = budgetDtos.stream()
        .map(BudgetsResDto.BudgetDto::amount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new BudgetsResDto(budgetDtos, totalAmount);
  }

  /**
   * 예산 생성 또는 업데이트
   *
   * @param member      사용자 정보
   * @param budgetDtos  예산 리스트
   * @param totalAmount 총 예산
   * @param categories  DB에 있는 모든 카테고리
   */
  private BudgetsResDto createOrUpdateBudgets(Member member,
      List<BudgetCreateReqDto.BudgetDto> budgetDtos, BigDecimal totalAmount,
      Map<Long, Category> categories) {

    List<Budget> budgets = budgetRepository.findByMember(member);
    List<Budget> updatedBudgets = new ArrayList<>();

    // 사용자가 이미 설정한 예산을 업데이트
    for (BudgetCreateReqDto.BudgetDto budgetDto : budgetDtos) {
      Long categoryId = budgetDto.categoryId();
      BigDecimal amount = budgetDto.amount();

      // 사용자의 기존 예산 중에서 해당 카테고리를 찾음
      Optional<Budget> existingBudget = budgets.stream()
          .filter(budget -> budget.getCategory().getId().equals(categoryId))
          .findFirst();

      if (existingBudget.isPresent()) {
        // 기존 예산이 존재하면 업데이트
        Budget budget = existingBudget.get();
        budget.updateAmount(amount);
        updatedBudgets.add(budget);
      } else {
        // 기존 예산이 없으면 새로운 예산 생성
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
    }

    // 저장된 예산 정보를 데이터베이스에 업데이트
    List<Budget> savedBudgets = budgetRepository.saveAll(updatedBudgets);

    // BudgetsResDto를 생성하여 반환
    List<BudgetsResDto.BudgetDto> updatedBudgetDtos = savedBudgets.stream()
        .map(budget -> new BudgetsResDto.BudgetDto(
            budget.getCategory().getId(),
            budget.getCategory().getName(),
            budget.getAmount()
        ))
        .collect(Collectors.toList());

    return new BudgetsResDto(updatedBudgetDtos, totalAmount);
  }

  private BudgetsResDto recommendBudgets(Member member, BigDecimal totalAmount) {
    return null;
  }

}
