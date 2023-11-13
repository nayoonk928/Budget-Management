package com.example.budget.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Budget {

    @Id
    @Column(name = "budget_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Column(name = "budget_amount", nullable = false)
    Integer amount;

    @Column(name = "rate", nullable = false)
    Double rate;

    @Builder
    public Budget(Member member, Category category, Integer amount, Double rate) {
        this.member = member;
        this.category = category;
        this.amount = amount != null ? amount : 0;
        this.rate = rate != null ? rate : 0;
    }

    public void updateAmount(Integer amount) {
        this.amount = amount;
    }

    public void updateRate(Double rate) {
        this.rate = rate;
    }

}
