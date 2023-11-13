package com.example.budget.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    Date expendedAt;

    @Column(name = "expense_amount", nullable = false)
    Integer amount;

    @Column(nullable = false)
    Boolean isExcludedSum;

    String description;

    @Builder
    public Expense(Member member, Category category, Date expendedAt, Integer amount, Boolean isExcludedSum, String description) {
        this.member = member;
        this.category = category;
        this.expendedAt = expendedAt;
        this.amount = amount;
        this.isExcludedSum = isExcludedSum != null ? isExcludedSum : false;
        this.description = description;
    }

}
