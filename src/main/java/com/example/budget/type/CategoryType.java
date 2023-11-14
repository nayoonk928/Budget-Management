package com.example.budget.type;

public enum CategoryType {
  FOOD("식비"),
  TRANSPORTATION("교통"),
  SHOPPING("쇼핑"),
  LEISURE("여가"),
  HEALTH("의료/건강"),
  LIFE("생활"),
  ETC("기타");

  private final String name;

  CategoryType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}