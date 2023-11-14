package com.example.budget.repository;

import com.example.budget.entity.Category;
import com.example.budget.entity.Expense;
import com.example.budget.entity.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;

public interface ExpenseQRepository {

  List<Expense> filterExpenses(Member member, LocalDate startDate, LocalDate endDate,
      Integer minAmount, Integer maxAMount, String categoryType, Pageable pageable);

  List<Expense> getExpensesByDateRange(Member member, LocalDate startDate, LocalDate endDate);

  Map<Category, Integer> getTotalExpenseByDateRangeGroupByCategory(Member member,
      LocalDate startDate, LocalDate endDate);

}
