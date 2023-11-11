package com.example.budget.entity;

import jakarta.persistence.*;
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

    @Column(name = "budget_amount", nullable = false)
    Long amount;

    @Column(name = "budget_year", nullable = false)
    Integer year;

    @Column(name = "budget_month", nullable = false)
    Integer month;

    @Builder
    public Budget(Member member, Long amount, Integer year, Integer month) {
        this.member = member;
        this.amount = amount;
        this.year = year;
        this.month = month;
    }

}
