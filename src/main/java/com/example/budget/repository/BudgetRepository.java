package com.example.budget.repository;

import com.example.budget.entity.Budget;
import com.example.budget.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BudgetRepository extends JpaRepository<Budget, Long>, BudgetQRepository {

  List<Budget> findByMember(Member member);

  List<Budget> findAllByMember(Member member);

  @Query("select sum(b.amount) from Budget b where b.member = :member")
  Integer getTotalAmountByMember(@Param("member") Member member);

}
