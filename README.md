# 🎟️ Book Tickets

[![CI](https://github.com/choonngg/book-tickets/actions/workflows/ci.yml/badge.svg)](https://github.com/choonngg/book-tickets/actions/workflows/ci.yml)
[![Deploy](https://github.com/choonngg/book-tickets/actions/workflows/deploy.yml/badge.svg)](https://github.com/choonngg/book-tickets/actions/workflows/deploy.yml)

동시 좌석 예매 상황에서 **중복 구매를 방지하는 것**을 핵심 목표로 만든 공연 예매 프로젝트입니다.  
팬은 공연을 조회하고 구역별 좌석을 선택해 예매할 수 있고, 아티스트는 공연을 등록하고 관리할 수 있습니다.

단순 CRUD를 넘어, 좌석 예매에서 발생하는 동시성 문제를 다루고 Optimistic Lock, Pessimistic Lock, Redisson 기반 Distributed Lock 전략을 비교했습니다.  
최종적으로 AWS 환경에서 ALB, EC2 Docker Compose, RDS MySQL, ElastiCache Valkey를 사용해 배포까지 검증했습니다.

## ✨ 주요 기능

| 구분 | 내용 |
|---|---|
| 인증/인가 | JWT 기반 로그인, Refresh Token, FAN / ARTIST / ADMIN 역할 분리 |
| 공연 관리 | 공연 생성, 공연 목록 조회, 공연 상세 조회, 공연 취소 |
| 좌석 조회 | 공연별 좌석 조회, 구역별 예매 가능 좌석 조회, 좌석 상태 표시 |
| 예매 | 좌석 예매, 구매 완료 처리, 내 티켓 조회 |
| 동시성 제어 | 동일 좌석 중복 예매 방지, 예매 전략별 부하 테스트 |
| 배포 | GitHub Actions, GHCR, Docker Compose, AWS 기반 배포 |


## 🛠️ 기술 스택

| 영역 | 기술 |
|---|---|
| Backend | Java 25, Spring Boot 4, Spring Security, Spring Data JPA, Spring Data Redis |
| Database | MySQL, H2 for tests |
| Locking | Redisson, Redis/Valkey Distributed Lock |
| Auth | JWT, Spring Security |
| Frontend | React 19, TypeScript, Vite, Vitest, Testing Library |
| Infra | Docker, Docker Compose, Nginx |
| Cloud | AWS EC2, ALB, RDS MySQL, ElastiCache Valkey, Elastic IP |
| CI/CD | GitHub Actions, GHCR |
| Test | JUnit 5, Mockito, Spring MVC Test, Vitest |


## 🏗️ 아키텍처

<img width="1119" height="776" alt="Architecture Diagram" src="https://github.com/user-attachments/assets/5e2d8c87-9069-437a-bb90-99fef44f2ab5" />

운영 트래픽은 ALB를 통해 EC2의 Nginx 컨테이너로 들어옵니다.  
Nginx는 React 정적 파일을 서빙하고, `/api/**` 및 `/actuator/health` 요청을 Spring Boot 백엔드로 프록시합니다.

## 🧭 핵심 사용자 흐름

### Artist
```text
회원가입 -> 로그인 -> 공연 생성 -> 공연 목록 확인
```

### Fan
```text
회원가입 -> 로그인 -> 공연 새로고침 -> 공연 선택
-> 구역 선택 -> 예매 가능 좌석 확인 -> 예매 -> 내 티켓 확인
```

### Admin
현재 ADMIN 역할은 로그인 가능하지만, 별도 관리 기능은 준비 중 화면으로 처리합니다. 프로젝트 범위를 예매 핵심 흐름에 집중하기 위한 결정입니다.

  
## 🔌 API 개요

| 도메인 | 엔드포인트 |
|---|---|
| Auth | `POST /api/auth/signup`, `POST /api/auth/login`, `POST /api/auth/logout`, `POST /api/auth/token/refresh` |
| User | `GET /api/users/me`, `PATCH /api/users/me`, `DELETE /api/users/me` |
| Concert | `GET /api/concerts`, `GET /api/concerts/{concertId}`, `POST /api/concerts`, `PATCH /api/concerts/{concertId}`, `PATCH /api/concerts/{concertId}/cancel`, `GET /api/concerts/{concertId}/stats` |
| Seat | `GET /api/concerts/{concertId}/seats`, `GET /api/concerts/{concertId}/seats/available` |
| Ticket | `POST /api/tickets`, `GET /api/tickets/me`, `GET /api/tickets/{ticketId}`, `PATCH /api/tickets/{ticketId}/cancel` |

`POST /api/tickets` 요청에는 중복 결제를 막기 위해 `Idempotency-Key` 헤더가 필요합니다.

  
## 🔒 예매 전략
예매 로직은 `TICKET_PURCHASE_STRATEGY` 설정값으로 전략을 선택합니다.

| 전략 | 목적 |
|---|---|
| `optimistic` | Optimistic Lock 기반 동시 예매 동작 비교 |
| `pessimistic` | DB Row Lock 기반 동시 예매 동작 비교 |
| `distributed` | Redisson 분산 락 기반 운영 유사 환경 적용 |


AWS 배포 환경에서는 다음 전략을 사용했습니다.
```text
TICKET_PURCHASE_STRATEGY=distributed
```


## 💻 로컬 실행

### 요구 사항
- Java 25
- Node.js 24
- MySQL
- Redis 또는 Valkey

### Backend
먼저 필요한 환경 변수를 설정합니다.
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ticket?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=<password>
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_SSL_ENABLED=false
JWT_SECRET=<secret-at-least-32-bytes>
JWT_ACCESS_TOKEN_MINUTES=30
JWT_REFRESH_TOKEN_DAYS=7
TICKET_PURCHASE_STRATEGY=distributed
PAYMENT_TIMEOUT_MILLIS=5000
```

실행:
```bash
cd backend
./gradlew bootRun
```
  
Windows:
```powershell
cd backend
.\gradlew.bat bootRun
```

### Frontend

```bash
cd frontend
npm ci
npm run dev
```
  
## ✅ 테스트

### Backend

```bash
cd backend
./gradlew --no-daemon test
```

Windows:

```powershell
cd backend
.\gradlew.bat --no-daemon test
```

### Frontend

```bash
cd frontend
npm test
npm run build
```

### Production Compose 설정 검증

```bash
docker compose -f docker-compose.prod.yml config
```

  
## 🚀 배포

검증한 배포 구조는 다음과 같습니다.

```text
AWS ALB
-> EC2 Docker Compose
-> Nginx
-> Spring Boot
-> RDS MySQL
-> ElastiCache Valkey
```

GitHub Actions 배포 흐름:

```text
Verify -> Build and Push Images -> Deploy to EC2
```

배포 이미지는 GHCR에 게시됩니다.

```text
ghcr.io/choonngg/book-tickets-backend:<commit-sha>
ghcr.io/choonngg/book-tickets-nginx:<commit-sha>
```

### 배포에 필요한 GitHub Secrets

| Secret | 설명 |
|---|---|
| `EC2_HOST` | SSH 배포에 사용할 EC2 Elastic IP 또는 Public DNS |
| `EC2_USER` | EC2 SSH 사용자, 예: `ec2-user` |
| `EC2_SSH_KEY` | GitHub Actions에서 사용할 EC2 접속 private key |
| `REGISTRY_USERNAME` | GitHub 사용자명 |
| `REGISTRY_TOKEN` | EC2에서 GHCR 이미지를 pull 할 때 사용할 token |
| `SPRING_DATASOURCE_URL` | RDS MySQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | RDS 사용자명 |
| `SPRING_DATASOURCE_PASSWORD` | RDS 비밀번호 |
| `SPRING_DATA_REDIS_HOST` | ElastiCache Valkey endpoint host. 포트는 포함하지 않음 |
| `SPRING_DATA_REDIS_PORT` | Redis/Valkey 포트. 일반적으로 `6379` |
| `SPRING_DATA_REDIS_SSL_ENABLED` | 검증한 Valkey 환경에서는 `false` |
| `JWT_SECRET` | JWT 서명 secret |
| `JWT_ACCESS_TOKEN_MINUTES` | Access Token 만료 시간 |
| `JWT_REFRESH_TOKEN_DAYS` | Refresh Token 만료 일수 |
| `TICKET_PURCHASE_STRATEGY` | AWS 배포 환경에서는 `distributed` |
| `PAYMENT_TIMEOUT_MILLIS` | Mock 결제 timeout |

  
## 🧯 배포 중 해결한 문제

| 문제 | 해결 |
|---|---|
| GitHub Actions Ubuntu runner에서 `backend/gradlew` 실행 실패 | Gradle wrapper 실행 권한 추가 |
| Redisson host 파싱 실패 | `SPRING_DATA_REDIS_HOST`에서 `:6379` 제거 |
| Redis TLS 설정 불일치 | ElastiCache Valkey 설정에 맞춰 `SPRING_DATA_REDIS_SSL_ENABLED=false` 적용 |
| AWS 환경에서 백엔드 기동 시간이 길어 healthcheck 실패 | Compose healthcheck `start_period`와 `retries` 조정 |
| 브라우저에서 ALB `503` 응답 | ALB 가용 영역 및 Target Group 설정을 EC2 subnet/AZ와 맞춤 |
| README만 수정해도 Deploy 실행 | Deploy workflow에 path filter를 적용해 문서 변경 배포 제외 |

  
## 📁 프로젝트 구조

```text
.
├── backend/                 # Spring Boot API
├── frontend/                # React/Vite frontend
├── nginx/                   # Nginx reverse proxy config
├── .github/workflows/       # CI/CD workflows
├── Dockerfile.backend
├── Dockerfile.nginx
├── docker-compose.prod.yml
└── .env.example
```

  
## 📌 현재 상태

| 항목 | 상태 |
|---|---|
| Backend test | Passing |
| Frontend test/build | Passing |
| Docker build | Passing |
| GitHub Actions CI | Passing |
| GitHub Actions Deploy | Passing |
| AWS ALB target | Healthy |
| Public user flow | Artist flow, Fan purchase flow 검증 완료 |
