package com.example.budget.service.impl;

import com.example.budget.dto.res.DailyReportDto;
import com.example.budget.dto.res.ExpenseRecommendDto;
import com.example.budget.entity.Budget;
import com.example.budget.entity.Category;
import com.example.budget.entity.Expense;
import com.example.budget.entity.Member;
import com.example.budget.exception.CustomException;
import com.example.budget.exception.ErrorCode;
import com.example.budget.repository.BudgetRepository;
import com.example.budget.repository.ExpenseRepository;
import com.example.budget.service.ConsultingService;
import com.example.budget.type.ConsultingMessage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
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
    // 카테고리 별 남은 예산 가져오기
    Map<Category, Integer> categoryRestAmounts = getCategoryRestAmount(member, today);

    // 지출 카테고리별 추천 예산 분배
    List<ExpenseRecommendDto.CategoryResDto> categoryResDtos =
        distributeBudgetToCategories(categoryRestAmounts, todayBudget);

    if (today.getDayOfMonth() == 1) {
      return new ExpenseRecommendDto(todayBudget, ConsultingMessage.START.getMessage(),
          categoryResDtos);
    }

    // 컨설팅 메세지 위해 지출 위험도 계산
    LocalDate yesterday = today.minusDays(1);
    int totalBudgetOfMonth = budgetRepository.getTotalAmountByMember(member);
    int budgetUntilYesterday =
        (totalBudgetOfMonth / yesterday.lengthOfMonth()) * yesterday.getDayOfMonth();
    int risk = calculateRisk(expenseUntilYesterday, budgetUntilYesterday);

    ConsultingMessage message;
    if (risk > 120) {
      message = ConsultingMessage.HIGH_RISK;
    } else if (risk > 100) {
      message = ConsultingMessage.MEDIUM_RISK;
    } else {
      message = ConsultingMessage.LOW_RISK;
    }

    return new ExpenseRecommendDto(todayBudget, message.getMessage(), categoryResDtos);
  }

  @Override
  public DailyReportDto getDailyReport(Member member) {
    LocalDate today = LocalDate.now();
    int expenseUntilYesterday = getExpenseUntilYesterday(member, today);
    int todayBudget = getTodayBudget(member, today, expenseUntilYesterday);

    // 오늘 지출 합계
    int todayExpense = 0;
    List<DailyReportDto.CategoryResDto> categoryResDtos = new ArrayList<>();
    Map<Category, Integer> dailyTotalExpenseByCategory =
        expenseRepository.getTotalExpenseByDateRangeGroupByCategory(member, today, today);

    for (Map.Entry<Category, Integer> entry : dailyTotalExpenseByCategory.entrySet()) {
      Category category = entry.getKey();
      int categoryExpense = entry.getValue();
      int risk = calculateRisk(categoryExpense, todayBudget);

      categoryResDtos.add(new DailyReportDto.CategoryResDto(category.getName(), categoryExpense, risk));
      todayExpense += categoryExpense;
    }

    int risk = calculateRisk(todayExpense, todayBudget);
    return new DailyReportDto(todayBudget, todayExpense, risk, categoryResDtos);
  }

  // Risk 계산 메서드
  private int calculateRisk(int expense, int budget) {
    if (budget == 0) {
      return 0; // 예산이 0이면 위험도를 0으로 설정
    }

    return (int) (((double) expense / budget) * 100);
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

    if (totalBudgets == null || totalBudgets == 0) {
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

  private Map<Category, Integer> getCategoryRestAmount(Member member,
      LocalDate today) {
    List<Budget> budgets = budgetRepository.findByMember(member);
    List<Expense> expenses = getExpensesBeforeToday(member, today);

    // 카테고리별로 초기 예산을 저장하는 categoryBudgetMap 초기화
    Map<Category, Integer> categoryBudgetMap = budgets.stream()
        .collect(Collectors.toMap(budget -> budget.getCategory(), Budget::getAmount));

    // 카테고리별로 남은 예산을 저장하는 categoryRemainBudgetMap 초기화
    Map<Category, Integer> categoryRemainBudgetMap = new HashMap<>(categoryBudgetMap);

    // 지출을 기반으로 categoryRemainBudgetMap 업데이트
    for (Expense expense : expenses) {
      Category category = expense.getCategory();

      if (categoryRemainBudgetMap.containsKey(category)) {
        int categoryBudget = categoryRemainBudgetMap.get(category);

        categoryBudget -= expense.getAmount();

        if (categoryBudget < 0) {
          categoryBudget = 0;
        }

        categoryRemainBudgetMap.put(category, categoryBudget);
      }
    }

    return categoryRemainBudgetMap;
  }

  private List<ExpenseRecommendDto.CategoryResDto> distributeBudgetToCategories(
      Map<Category, Integer> categoryRestAmounts, int todayBudget) {
    int totalRestAmount = categoryRestAmounts.values().stream().mapToInt(Integer::intValue).sum();

    // 각 카테고리별로 남은 예산 비율에 따라 추천 예산 분배
    List<ExpenseRecommendDto.CategoryResDto> categoryResDtos = categoryRestAmounts.entrySet()
        .stream()
        .sorted(Comparator.comparing(entry -> entry.getKey().getId())) // 카테고리 ID로 정렬
        .map(entry -> {
          Category category = entry.getKey();
          int restAmount = entry.getValue();
          int recommendedBudget = (totalRestAmount != 0) ?
              (int) (((double) restAmount / totalRestAmount) * todayBudget) : 0;

          return new ExpenseRecommendDto.CategoryResDto(category.getName(), recommendedBudget);
        })
        .collect(Collectors.toList());

    return categoryResDtos;
  }

  private List<Expense> getExpensesBeforeToday(Member member, LocalDate today) {
    LocalDate startDate = today.withDayOfMonth(1);
    LocalDate endDate = today.minusDays(1);
    return expenseRepository.getExpensesByDateRange(member, startDate, endDate);
  }

}
