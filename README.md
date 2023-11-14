# 예산 관리 서비스

사용자들이 개인 재무를 관리하고 지출을 추적하는 데 도움을 주는 서비스입니다.

<br/>

## Table of Contents
- [개요](#개요)
- [Skils](#skils)
- [Running Tests](#running-tests)
- [API Reference](#api-reference)
- [프로젝트 진행 및 이슈 관리](#프로젝트-진행-및-이슈-관리)
- [구현과정(설계 및 의도)](#구현과정(설계-및-의도))
- [TIL 및 회고](#til-및-회고)
- [References](#references)

<br/>


## 개요

예산을 직접 설정하고 그에 맞게 지출하는지 지속적으로 확인하며 개인의 소비습관 개선에 도움을 주는 서비스입니다.

<br/>


## Skils

언어 및 프레임워크: ![Static Badge](https://img.shields.io/badge/JAVA-17-blue) ![Static Badge](https://img.shields.io/badge/SpringBoot-3.1.5-green)<br/>
데이터베이스: ![Static Badge](https://img.shields.io/badge/MySQL-blue)<br/>
테스트 데이터베이스: ![Static Badge](https://img.shields.io/badge/H2--red) <br/>
CI: ![Static Badge](https://img.shields.io/badge/Github-Actions-orange)  <br/>

<br/>


## Running Tests

> Test Result ScreenShot ![Static Badge](https://img.shields.io/badge/Test_Passed-20/20-green)
![test_result](doc/TestResult.png)

<br/>


## API Reference

### 회원 API

<details>
<summary>회원 가입 - click</summary>

#### Request
`POST /api/members`

```json
{
  "account": "account1",
  "password": "password1!",
  "nickname": "nickname1",
  "notification": true
}
```
| Field          | Type      | Description     |
|:---------------|:----------|:----------------|
| `account`      | `string`  | (Required) 계정   |
| `password`     | `string`  | (Required) 비밀번호 |
| `nickname`     | `string`  | (Required) 닉네임  |
| `notification` | `boolean` | 알림 동의 여부        |

#### Response
```text
200 OK
```
</details>

<details>
<summary>로그인 - click</summary>

#### Request
`POST /api/members/login`

```json
{
  "account": "계정",
  "password": "비밀번호"
}
```

#### Response
```text
200 OK
```
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWFHAiOjE3MDIzODA3NTd9.pBeBYC-KVlTgEDoctzPn8"
}
```
</details>

<details>
<summary>사용자 정보 조회 - click</summary>

#### Request
`GET /api/members`

```text
Authentication: Bearer {JWT}
```

#### Response
```text 
200 OK
```
```json
{
  "account": "account1",
  "nickname": "member1",
  "notification": true
}
```
</details>

<details>
<summary>사용자 정보 업데이트 - click</summary>

#### Request
`PUT /api/members`

```text
Authentication: Bearer {JWT}
```
```json
{
  "nickname": "member1",
  "notification": false
}
```

#### Response
```text 
200 OK
```
```json
{
  "account": "account1",
  "nickname": "member1",
  "notification": false
}
```
</details>

### 카테고리 API

<details>
<summary>카테고리 목록 조회 - click</summary>

#### Request
`GET /api/categories`

```text
Authentication: Bearer {JWT}
```

#### Response
```text 
200 OK
```
```json
{
  "categories": [
    {
      "id": 1,
      "name": "식비"
    },
    {
      "id": 2,
      "name": "생활"
    }
  ]
}
```
</details>

### 예산 API

<details>
<summary>예산 설정 - click</summary>

#### Request
`POST /api/budgets`

```text
Authentication: Bearer {JWT}
```
```json
{
  "budgets": [
    {
      "category_id": 1,
      "amount": 200000
    },
    {
      "category_id": 2,
      "amount": 100000
    }
  ]
}
```

#### Response
```text 
201 Created
```
```json
{
  "budgets": [
    {
      "category_id": 1,
      "category_name": "식비",
      "amount": 200000
    },
    {
      "category_id": 2,
      "category_name": "생활",
      "amount": 100000
    }
  ],
  "total_amount": 300000
}
```
</details>

<details>
<summary>예산 조회 - click</summary>

#### Request
`GET /api/budgets`

```text
Authentication: Bearer {JWT}
```

#### Response
```text 
200 OK
```
```json
{
  "budgets": [
    {
      "category_id": 1,
      "category_name": "식비",
      "amount": 200000
    },
    {
      "category_id": 2,
      "category_name": "생활",
      "amount": 100000
    }
  ],
  "total_amount": 300000
}
```
</details>

<details>
<summary>예산 추천 - click</summary>

#### Request
`GET /api/budgets/recommend`

```text
Authentication: Bearer {JWT}
```
| Query Parameter | Type  | Description      |
|:----------------|:------|:-----------------|
| `total_amount`  | `int` | (Required) 예산 총액 |

#### Response
```text 
200 OK
```
```json
{
    "budgets": [
        {
            "category_id": 1,
            "category_name": "식비",
            "amount": 200000
        },
        {
            "category_id": 2,
            "category_name": "생활",
            "amount": 100000
        }
    ],
    "total_amount": 300000
}
```
</details>

### 지출 API

<details>
<summary>지출 추가 - click</summary>

#### Request

`POST /api/expenses`

```text
Authentication: Bearer {JWT}
```

```json
{
  "expended_at": "2023-10-10",
  "amount": 10000,
  "category": "생활",
  "is_excluded_sum": false,
  "description": "다이소"
}
```

| Field             | Type       | Description                         |
|:------------------|:-----------|:------------------------------------|
| `expended_at`     | `datetime` | (Required) 지출 일시                    |
| `amount`          | `int`      | (Required) 지출 금액                    |
| `category`        | `string`   | (Required) 카테고리                     |
| `is_excluded_sum` | `boolean`  | 지출 합계 제외 여부. True인 경우 지출 총액 계산에서 제외 |
|`description`|`string`| 지출 설명                               |

#### Response

```text
200 OK
```
```json
{
    "expense_id": 11,
    "expended_at": "2023-10-10",
    "amount": 10000,
    "category": "생활",
    "is_excluded_sum": false,
    "description": "다이소"
}
```

</details>
<details>
<summary>지출 수정 - click</summary>

#### Request

`PUT /api/expenses/{expense_id}`

```text
Authentication: Bearer {JWT}
```

```json
{
  "expended_at": "2023-11-13",
  "amount": 12000,
  "category": "식비",
  "is_excluded_sum": false,
  "description": "서브웨이 먹음"
}
```

| Field             | Type      | Description                         |
|:------------------|:----------|:------------------------------------|
| `expended_at`     | `date`    | (Required) 지출 일시                    |
| `amount`          | `int`     | (Required) 지출 금액                    |
| `category`        | `string`  | (Required) 카테고리                     |
| `is_excluded_sum` | `boolean` | 지출 합계 제외 여부. True인 경우 지출 총액 계산에서 제외 |
| `description`     | `string`  | 지출 설명                               |

#### Response

```text
200 OK
```
```json
{
  "expense_id": 11,
  "expended_at": "2023-11-13",
  "amount": 12000,
  "category": "식비",
  "is_excluded_sum": false,
  "description": "서브웨이 먹음"
}
```

</details>
<details>
<summary>지출 삭제 - click</summary>

#### Request

`DELETE /api/expenses/{expense_id}`

```text
Authentication: Bearer {JWT}
```

#### Response

```text
204 No Content
```

</details>

<details>
<summary>지출 상세 조회 - click</summary>

#### Request

`GET /api/expenses/{expense_id}`

```text
Authentication: Bearer {JWT}
```

#### Response

```text
200 OK
```
```json
{
  "expense_id": 11,
  "expended_at": "2023-11-13",
  "amount": 12000,
  "category": "식비",
  "is_excluded_sum": false,
  "description": "서브웨이 먹음"
}
```

</details>
<details>
<summary>지출 목록 조회 - click</summary>

#### Request

`GET /api/expenses`

```text
Authentication: Bearer {JWT}
```

| Query Parameter | Type           | Description                       |
|:----------------|:---------------|:----------------------------------|
| `start_date`    | `date`         | (Required) 조회 시작 날짜               |
| `end_date`      | `date`         | (Required) 조회 종료 날짜               |
| `min_amount`    | `int`          | 조회할 최소 금액                         |
| `max_amount`    | `int`          | 조회할 최대 금액                         |
| `category`      | `string` | 해당 카테고리의 지출만 조회                   |
| `order_by`      | `string`       | 정렬 기준 `date`(날짜) 또는 `amount`(금액)  |
| `sort_by`       | `string`       | 정렬 기준 `asc`(오름차순) 또는 `desc`(내림차순) |

#### Response

```text
200 OK
```

```json
{
  "total_amount": 142000,
  "category_amounts": [
    {
      "category": "생활",
      "amount": 20000
    },
    {
      "category": "식비",
      "amount": 122000
    }
  ],
  "expenses": [
    {
      "expense_id": 8,
      "expended_at": "2023-11-15",
      "amount": 10000,
      "category": "생활",
      "description": "다이소"
    },
    {
      "expense_id": 9,
      "expended_at": "2023-11-15",
      "amount": 20000,
      "category": "식비",
      "description": "짜장 + 탕수육"
    },
    {
      "expense_id": 5,
      "expended_at": "2023-11-14",
      "amount": 10000,
      "category": "식비",
      "description": "서브웨이 먹음"
    },
    {
      "expense_id": 11,
      "expended_at": "2023-11-13",
      "amount": 12000,
      "category": "식비",
      "description": "서브웨이 먹음"
    }
  ]
}
```

| Field              | Type          | Description |
|:-------------------|:--------------|:------------|
| `total_amount`     | `int`         | 조회된 지출들의 총액 |
| `category_amounts` | `object list` | 카테고리별 지출 총액 |
| `expenses`         | `object list` | 각 지출 정보     |

</details>

### 지출 컨설팅 API

<details>
<summary>오늘의 지출 추천 - click</summary>

#### Request

`GET /api/expenses/today/recommend`

```text
Authentication: Bearer {JWT}
```

#### Response

```text
200 OK
```
```json
{
  "total_budget": 43000,
  "message": "현재 절약을 잘 하고 있어요! 남은 날도 화이팅!",
  "categories": [
    {
      "category": "식비",
      "budget": 0
    },
    {
      "category": "생활",
      "budget": 6231
    }
  ]
}
```

| Field          | Type          | Description          |
|:---------------|:--------------|:---------------------|
| `total_budget` | `int`         | 오늘의 추천 지출 금액         |
| `message`      | `string`      | 사용자의 예산/지출 상황에 맞는 멘트 |
| `categories`   | `object list` | 각 카테고리별 오늘의 추천 지출 금액 |

</details>
<details>
<summary>오늘의 지출 안내 - click</summary>

#### Request

`GET /api/expenses/today`

```text
Authentication: Bearer {JWT}
```

#### Response

```text
200 OK
```
```json
{
  "recommend_expense": 43000,
  "spent_expense": 30000,
  "risk": 69,
  "categories": [
    {
      "category": "식비",
      "expense": 20000,
      "risk": 46
    },
    {
      "category": "생활",
      "expense": 10000,
      "risk": 23
    }
  ]
}
```

| Field     | Type            | Description                             |
|:----------|:----------------|:----------------------------------------|
| `recommend_expense`  | `int`           | 예산을 만족하기 위해 오늘 지출했어야 할 금액               |
| `spent_expense` | `int`           | 오늘 사용한 총액                               |
| `risk`    | `int (percent)` | 위험도: 오늘 지출했어야 할 금액 대비 오늘 실제로 지출한 금액 (%) |
| `categories` | `object list`           | 카테고리별 오늘 지출 총액 및 위험도                    |

</details>

### 통계 API

<details>
<summary>지난달 대비 지출 통계 - click</summary>

#### Request

`GET /api/statistics/month`

```text
Authentication: Bearer {JWT}
```

#### Response

```text
200 OK
```
```json
{
  "last_expense": 100000,
  "current_expense": 142000,
  "increase_rate": 142,
  "categories": [
    {
      "category": "식비",
      "last_expense": 80000,
      "current_expense": 122000,
      "increase_rate": 152
    },
    {
      "category": "생활",
      "last_expense": 20000,
      "current_expense": 20000,
      "increase_rate": 100
    }
  ]
}
```

| Field     | Type            | Description                                                     |
|:----------|:----------------|:----------------------------------------------------------------|
| `last_expense`  | `int`           | 지난 달 N일까지 사용한 총액                                                |
| `current_expense` | `int`           | 이번 달 오늘(N일)까지 사용한 총액                                            |
| `increase_rate`    | `int (percent)` | 지난달 대비 이번달에 사용한 금액의 증가율 (%)                                     |
| `categories` | `object list`           | 카테고리별 지난달 총액, 이번달 총액 및 증가율 |

</details>
<details>
<summary>지난 요일 대비 지출 통계 - click</summary>

#### Request

`GET /api/statistics/weekday`

```text
Authentication: Bearer {JWT}
```

#### Response

```text
200 OK
```
```json
{
  "last_expense": 20000,
  "current_expense": 30000,
  "increase_rate": 150,
  "categories": [
    {
      "category": "식비",
      "last_expense": 20000,
      "current_expense": 20000,
      "increase_rate": 100
    }
  ]
}
```

| Field     | Type            | Description                                                     |
|:----------|:----------------|:----------------------------------------------------------------|
| `last_expense`  | `int`           | 지난주 N요일에 사용한 총액                                            |
| `current_expense` | `int`           | 오늘(N요일) 사용한 총액                                            |
| `increase_rate`    | `int (percent)` | 지난주 N요일 대비 오늘 사용한 금액의 증가율 (%)                                     |
| `categories` | `object list`           | 카테고리별 지난주 N요일 총액, 오늘 총액 및 증가율 |

</details>

<br/>


## 프로젝트 진행 및 이슈 관리

![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white)
<img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white">

<br/>


## 구현과정(설계 및 의도)


## TIL 및 회고


## References
