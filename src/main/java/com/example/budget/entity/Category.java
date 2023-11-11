package com.example.budget.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    Double averageRate;

    @Builder
    public Category(String name, Double averageRate) {
        this.name = name;
        this.averageRate = averageRate != null ? averageRate : 0.0;
    }

    public Category(Long id, String name, Double averageRate) {
        this.id = id;
        this.name = name;
        this.averageRate = averageRate != null ? averageRate : 0.0;
    }

}