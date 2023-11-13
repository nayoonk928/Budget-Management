package com.example.budget.repository.impl;

import static com.example.budget.entity.QBudget.budget;

import com.example.budget.repository.BudgetQRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BudgetRepositoryImpl implements BudgetQRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Map<Long, Double> getCategoryAverageRates() {
    List<Tuple> results = queryFactory
        .select(budget.category.id, budget.rate.avg())
        .from(budget)
        .groupBy(budget.category.id)
        .fetch();

    Map<Long, Double> categoryAverageRates = new HashMap<>();
    for (Tuple result : results) {
      Long categoryId = result.get(budget.category.id);
      Double averageRate = result.get(budget.rate.avg());
      categoryAverageRates.put(categoryId, averageRate);
    }

    return categoryAverageRates;
  }

}
