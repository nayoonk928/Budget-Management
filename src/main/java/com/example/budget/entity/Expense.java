package com.example.budget.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expense {

    @Id
    @Column(name = "expense_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Column(nullable = false)
    LocalDateTime expendedAt;

    @Column(name = "expense_amount", nullable = false)
    BigDecimal amount;

    @Column(nullable = false)
    Boolean isExcludedSum;

    String description;

    @Builder
    public Expense(Member member, Category category, LocalDateTime expendedAt, BigDecimal amount, Boolean isExcludedSum, String description) {
        this.member = member;
        this.category = category;
        this.expendedAt = expendedAt;
        this.amount = amount;
        this.isExcludedSum = isExcludedSum != null ? isExcludedSum : false;
        this.description = description;
    }

}
