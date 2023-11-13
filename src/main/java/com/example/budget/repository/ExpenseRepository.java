package com.example.budget.repository;

import com.example.budget.entity.Expense;
import com.example.budget.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

  Optional<Expense> findByIdAndMember(Long expenseId, Member member);

}
