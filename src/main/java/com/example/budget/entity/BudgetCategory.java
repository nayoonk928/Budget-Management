package com.example.budget.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BudgetCategory {

    @Id
    @Column(name = "budget_category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Column(name = "budget_category_amount", nullable = false)
    Long amount;

    @Column(name = "rate", nullable = false)
    Double rate;

    @Builder
    public BudgetCategory(Budget budget, Category category, Long amount, Double rate) {
        this.budget = budget;
        this.category = category;
        this.amount = amount;
        this.rate = rate != null ? rate : 0.0;
    }

}
