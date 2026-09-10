# Ranking System

Redis 학습을 목적으로 만든 개인 프로젝트입니다. 조회수 기반 실시간 인기 상품 랭킹 서비스를 직접 설계하고 구현하면서, Sorted Set을 비롯한 Redis의 다양한 기능을 실습하는 데 초점을 두고 있습니다.

기존 개인 프로젝트인 [WhaleOrder](https://github.com/LeeJiiYoung/WhaleOrder)에서는 Redis를 분산 락과 단순 캐시(TTL) 용도로만 사용했는데, 이번 프로젝트에서는 Sorted Set, Lua 스크립트, Pub/Sub 등 다른 영역까지 다뤄보는 것이 목표입니다.

## 프로젝트 구조

모노레포 형태로 구성했습니다. 지금은 backend만 있지만, 필요해지면 frontend를 같은 저장소에 추가할 수 있도록 분리해두었습니다.

```
ranking_system
└── backend
    ├── build.gradle
    ├── docker-compose.yml
    └── src
        └── main/java/com/ranking
            ├── RankingApplication.java
            ├── config
            │   ├── RedisConfig.java       # RedisTemplate 빈 등록
            │   └── DataInitializer.java   # 앱 최초 실행 시 샘플 상품 20개 등록
            ├── product                    # 상품 도메인 (PostgreSQL)
            └── ranking                    # 랭킹 도메인 (Redis)
```

## 기술 스택

Java 21, Spring Boot 3.3.5, Spring Data JPA, Spring Data Redis, PostgreSQL, Docker Compose, Gradle

## 핵심 아이디어

상품 정보(이름, 가격)는 PostgreSQL에 저장하고, 실시간으로 바뀌는 조회수·랭킹은 Redis의 Sorted Set(`ZSET`) 하나로만 관리합니다. 상품이 조회될 때마다 `ZINCRBY`로 해당 상품의 점수를 올리고, 랭킹 조회 시 `ZREVRANGE`로 점수 높은 순서대로 꺼내옵니다.

```
key: ranking:view:realtime
  └─ member "1"(상품ID) - score 15.0(조회수)
  └─ member "2"(상품ID) - score 8.0(조회수)
```

## API

| Method | URL | 설명 |
|---|---|---|
| POST | /products | 상품 등록 (테스트용) |
| GET | /products/{productId} | 상품 상세 조회 (조회수 +1) |
| GET | /rankings?limit=10 | 조회수 상위 N개 랭킹 조회 |

요청/응답 예시

```
POST /products
{ "name": "아메리카노", "price": 4500 }

GET /rankings?limit=10
[
  { "productId": 1, "productName": "아메리카노", "viewCount": 15.0 },
  { "productId": 2, "productName": "카페라떼", "viewCount": 8.0 }
]
```

## 실행 방법

```bash
cd backend
docker-compose up -d
```

PostgreSQL, Redis가 뜬 뒤 `RankingApplication`을 실행하면, DB에 상품이 하나도 없을 경우 카페 메뉴 20개가 자동으로 등록됩니다.

## 진행 상황

- [x] 상품 등록/조회 API
- [x] Redis Sorted Set 기반 조회수 집계 (ZINCRBY)
- [x] 실시간 랭킹 조회 API (ZREVRANGE, 상품명 조인)
- [x] Request/Response DTO 분리
- [x] 앱 시작 시 샘플 데이터 자동 등록
- [x] Lua 스크립트로 원자적 처리
- [ ] 상위 랭킹 캐시 + 캐시 스탬피드 방지
- [ ] 일간/주간 랭킹 롤업
- [ ] 어뷰징 방지 (중복 조회 필터링, HyperLogLog)
- [ ] Pub/Sub 기반 실시간 랭킹 전파
- [ ] k6 부하 테스트, Redis Sentinel 장애 시나리오 검증
