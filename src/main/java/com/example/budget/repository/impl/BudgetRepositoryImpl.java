package com.example.budget.repository.impl;

import static com.example.budget.entity.QBudget.budget;
import static com.example.budget.entity.QCategory.category;

import com.example.budget.entity.Member;
import com.example.budget.repository.BudgetQRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BudgetRepositoryImpl implements BudgetQRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Map<Long, Long> getCountGroupByCategory(Member member) {
    return queryFactory.select(category.id, budget.count())
        .from(budget)
        .leftJoin(budget.category)
        .where(budget.member.ne(member))
        .groupBy(category)
        .fetch()
        .stream().collect(Collectors.toMap(
            tuple -> tuple.get(category.id),
            tuple -> tuple.get(budget.count())
        ))
        ;
  }

}
