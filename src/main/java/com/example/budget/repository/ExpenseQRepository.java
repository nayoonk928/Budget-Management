package com.example.budget.repository;

import com.example.budget.entity.Expense;
import com.example.budget.entity.Member;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ExpenseQRepository {

  List<Expense> filterExpenses(Member member, LocalDate startDate, LocalDate endDate,
      Integer minAmount, Integer maxAMount, String categoryType, Pageable pageable);

  List<Long> findExpenseIdsByMember(Long memberId);

}
