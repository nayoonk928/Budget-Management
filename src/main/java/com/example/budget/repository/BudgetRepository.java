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

  // 카테고리별 예산 항목의 총 개수를 구하는 쿼리 메서드
  @Query("SELECT COUNT(b) FROM Budget b WHERE b.category.id = :categoryId")
  int countByCategoryId(@Param("categoryId") Long categoryId);

}
