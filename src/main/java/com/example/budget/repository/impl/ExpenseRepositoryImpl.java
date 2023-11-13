package com.example.budget.repository.impl;

import static com.example.budget.entity.QCategory.category;
import static com.example.budget.entity.QExpense.expense;

import com.example.budget.entity.Expense;
import com.example.budget.entity.Member;
import com.example.budget.repository.ExpenseQRepository;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ExpenseRepositoryImpl implements ExpenseQRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Expense> filterExpenses(Member member, LocalDate startDate, LocalDate endDate,
      Integer minAmount, Integer maxAmount, String categoryType, Pageable pageable) {
    JPAQuery<Expense> query = queryFactory.selectFrom(expense)
        .leftJoin(expense.category, category)
        .where(
            expense.member.eq(member),
            expense.isExcludedSum.isFalse(),
            expense.expendedAt.between(startDate, endDate),
            expense.amount.between(minAmount, maxAmount)
        );

    if (categoryType != null) {
      query.where(expense.category.name.eq(categoryType));
    }

    List<OrderSpecifier> order = new ArrayList<>();
    pageable.getSort().forEach(o -> {
      order.add(new OrderSpecifier((o.getDirection().isDescending() ? Order.DESC : Order.ASC),
          new PathBuilder(Expense.class, "expense").get(o.getProperty())));
    });

    List<Expense> expenses = query
        .orderBy(order.toArray(new OrderSpecifier[0]))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    return expenses;
  }

  @Override
  public List<Long> findExpenseIdsByMember(Long memberId) {
    return queryFactory.select(expense.id)
        .from(expense)
        .where(expense.member.id.eq(memberId))
        .fetch();
  }

}
