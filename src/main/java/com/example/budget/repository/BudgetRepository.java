package com.example.budget.repository;

import com.example.budget.entity.Budget;
import com.example.budget.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long>, BudgetQRepository {

  List<Budget> findByMember(Member member);

  List<Budget> findAllByMember(Member member);

}
