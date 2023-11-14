package com.example.budget.service.impl;

import com.example.budget.dto.res.StatisticsDto;
import com.example.budget.dto.res.StatisticsDto.CategoryResDto;
import com.example.budget.entity.Category;
import com.example.budget.entity.Member;
import com.example.budget.repository.CategoryRepository;
import com.example.budget.repository.ExpenseRepository;
import com.example.budget.service.StatisticsService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

  private final CategoryRepository categoryRepository;
  private final ExpenseRepository expenseRepository;

  @Override
  public StatisticsDto compareWithLastMonth(Member member) {
    // 이번달 오늘까지 사용한 지출액
    LocalDate today = LocalDate.now();
    Map<Category, Integer> currentTotalExpenseByCategory =
        expenseRepository.getTotalExpenseByDateRangeGroupByCategory(
            member, today.withDayOfMonth(1), today);

    // 지난달 오늘까지 사용한 지출액
    LocalDate lastToday = today.minusMonths(1);
    Map<Category, Integer> lastTotalExpenseByCategory =
        expenseRepository.getTotalExpenseByDateRangeGroupByCategory(
            member, lastToday.withDayOfMonth(1), lastToday);

    return getStatisticsDto(currentTotalExpenseByCategory, lastTotalExpenseByCategory);
  }

  @Override
  public StatisticsDto compareWithLastWeek(Member member) {
    // 오늘 사용한 지출액
    LocalDate today = LocalDate.now();
    Map<Category, Integer> currentTotalExpenseByCategory =
        expenseRepository.getTotalExpenseByDateRangeGroupByCategory(member, today, today);

    // 지난달 오늘 사용한 지출액
    LocalDate lastToday = today.minusMonths(1);
    Map<Category, Integer> lastTotalExpenseByCategory =
        expenseRepository.getTotalExpenseByDateRangeGroupByCategory(member, lastToday, lastToday);

    return getStatisticsDto(currentTotalExpenseByCategory, lastTotalExpenseByCategory);
  }

  // 카테고리별 통계 정보를 가져오는 메서드
  private StatisticsDto getStatisticsDto(Map<Category, Integer> current, Map<Category, Integer> last) {
    int lastTotalExpense = 0;
    int currentTotalExpense = 0;
    List<CategoryResDto> categoryResDtos = new ArrayList<>();

    for (Category category : categoryRepository.findAll()) {
      int currentExpense = current.getOrDefault(category, 0);
      int lastExpense = last.getOrDefault(category, 0);

      // 증가율 계산
      int increaseRate = calculateIncreaseRate(lastExpense, currentExpense);

      categoryResDtos.add(new StatisticsDto.CategoryResDto(
          category.getName(),
          lastExpense,
          currentExpense,
          increaseRate
      ));

      lastTotalExpense += lastExpense;
      currentTotalExpense += currentExpense;
    }

    // 전체 지출액 증가율 계산
    int totalIncreaseRate = calculateIncreaseRate(lastTotalExpense, currentTotalExpense);

    return new StatisticsDto(
        lastTotalExpense,
        currentTotalExpense,
        totalIncreaseRate,
        categoryResDtos
    );
  }

  private int calculateIncreaseRate(int lastValue, int currentValue) {
    if (lastValue == 0 || currentValue == 0) return 0;
    return (int) (((double) currentValue / lastValue) * 100);
  }

}
