package com.example.budget.service.impl;

import com.example.budget.dto.res.ExpenseRecommendDto;
import com.example.budget.entity.Budget;
import com.example.budget.entity.Expense;
import com.example.budget.entity.Member;
import com.example.budget.exception.CustomException;
import com.example.budget.exception.ErrorCode;
import com.example.budget.repository.BudgetRepository;
import com.example.budget.repository.ExpenseRepository;
import com.example.budget.service.ConsultingService;
import com.example.budget.type.ConsultingMessage;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsultingServiceImpl implements ConsultingService {

  private static final int MIN_BUDGET = 10000;

  private final BudgetRepository budgetRepository;
  private final ExpenseRepository expenseRepository;

  @Override
  public ExpenseRecommendDto recommendDailyExpense(Member member) {
    LocalDate today = LocalDate.now();
    int expenseUntilYesterday = getExpenseUntilYesterday(member, today);
    int todayBudget = getTodayBudget(member, today, expenseUntilYesterday);
    List<ExpenseRecommendDto.CategoryResDto> categoryResDtos = getCategoryRestAmount(member, today);

    if (today.getDayOfMonth() == 1) {
      return new ExpenseRecommendDto(todayBudget, ConsultingMessage.START.getMessage(),
          categoryResDtos);
    }

    // 컨설팅 메세지 위해 지출 위험도 계산
    LocalDate yesterday = today.minusDays(1);
    int totalBudgetOfMonth = budgetRepository.getTotalAmountByMember(member);
    int budgetUntilYesterday =
        (totalBudgetOfMonth / yesterday.lengthOfMonth()) * yesterday.getDayOfMonth();
    double risk = calculateBudgetExhaustionRate(totalBudgetOfMonth, budgetUntilYesterday, today);

    ConsultingMessage message;
    if (risk > 0.8) {
      message = ConsultingMessage.HIGH_RISK;
    } else if (risk > 0.6) {
      message = ConsultingMessage.MEDIUM_RISK;
    } else {
      message = ConsultingMessage.LOW_RISK;
    }

    return new ExpenseRecommendDto(todayBudget, message.getMessage(), categoryResDtos);
  }

  private double calculateBudgetExhaustionRate(int totalBudgetOfMonth, int expenseUntilYesterday,
      LocalDate today) {
    // 예산 소진 속도 계산 (일일 평균 소비 예산)
    double remainingBudget = totalBudgetOfMonth - expenseUntilYesterday;
    double remainingDays = today.lengthOfMonth() - today.getDayOfMonth() + 1;
    double dailyBudget = remainingBudget / remainingDays;

    // 속도를 예산 대비로 환산
    return dailyBudget / totalBudgetOfMonth;
  }

  private int getExpenseUntilYesterday(Member member, LocalDate today) {
    if (today.getDayOfMonth() == 1) {
      return 0;
    }

    List<Expense> expenses = getExpensesBeforeToday(member, today);
    return expenses.stream().mapToInt(Expense::getAmount).sum();
  }

  private int getTodayBudget(Member member, LocalDate today, int totalExpenditureToDate) {
    Integer totalBudgets = budgetRepository.getTotalAmountByMember(member);

    if (totalBudgets == 0 || totalBudgets == null) {
      throw new CustomException(ErrorCode.BUDGET_NOT_FOUND);
    }

    int restBudget = totalBudgets - totalExpenditureToDate;
    int restDay = today.lengthOfMonth() - today.getDayOfMonth() + 1;
    if (restBudget > 0) {
      return Integer.max(MIN_BUDGET, ((restBudget / restDay) / 1000) * 1000);
    } else {
      return MIN_BUDGET;
    }
  }

  private List<ExpenseRecommendDto.CategoryResDto> getCategoryRestAmount(Member member,
      LocalDate today) {
    List<Budget> budgets = budgetRepository.findByMember(member);
    List<Expense> expenses = getExpensesBeforeToday(member, today);

    // 카테고리별로 초기 예산을 저장하는 categoryBudgetMap 초기화
    Map<String, Integer> categoryBudgetMap = budgets.stream()
        .collect(Collectors.toMap(budget -> budget.getCategory().getName(), Budget::getAmount));

    // 카테고리별로 남은 예산을 저장하는 categoryRemainBudgetMap 초기화
    Map<String, Integer> categoryRemainBudgetMap = new HashMap<>(categoryBudgetMap);

    // 지출을 기반으로 categoryRemainBudgetMap 업데이트
    for (Expense expense : expenses) {
      String category = expense.getCategory().getName();

      if (categoryRemainBudgetMap.containsKey(category)) {
        int categoryBudget = categoryRemainBudgetMap.get(category);

        categoryBudget -= expense.getAmount();

        if (categoryBudget < 0) {
          categoryBudget = 0;
        }

        categoryRemainBudgetMap.put(category, categoryBudget);
      }
    }

    // 업데이트된 categoryRemainBudgetMap을 기반으로 CategoryResDto 객체 생성
    List<ExpenseRecommendDto.CategoryResDto> categoryResDtos = categoryRemainBudgetMap.entrySet()
        .stream()
        .map(entry -> new ExpenseRecommendDto.CategoryResDto(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());

    return categoryResDtos;
  }

  private List<Expense> getExpensesBeforeToday(Member member, LocalDate today) {
    LocalDate startDate = today.withDayOfMonth(1);
    LocalDate endDate = today.minusDays(1);
    return expenseRepository.getExpensesByDateRange(member, startDate, endDate);
  }

}
