# Backend Repository

## 📌 프로젝트 개요

- **프로젝트명:** Xpact
- **설명:** 취준생을 위한 경험 정리 서비스
- **기술 스택:** Java 17, Spring Boot, JPA, MySQL, Redis, Docker, AWS EC2
- **배포 환경:** 미정


## 📂 프로젝트 구조

```tsx
backend/
├── src/main/java/com/example/
│   ├── config/          
│   ├── domain/
│   │   ├── controller/      # 도메인 내부로 이동
│   │   ├── service/         # 도메인 내부로 이동
│   │   ├── repository/      # 도메인 내부로 이동
│   │   ├── dto/             # 도메인 내부로 이동
│   │   └── [기존 엔티티, VO 등]
│   ├── util/
│   └── Application.java
├── src/test/java/com/example/
├── build.gradle
└── README.md
```


## 🔥 Git Branch 전략

- **main**: 배포 가능한 안정적인 코드만 포함
- **develop**: 개발 브랜치, 모든 기능이 병합되는 브랜치
- **feature/**: 새로운 기능 개발 브랜치 (예: `feature/#이슈넘버/login-api`)
- **fix/**: 긴급 수정 사항 반영 (예: `fix/#이슈넘버/critical-login-bug`)

> 브랜치 네이밍 예시: `feat/#이슈넘버/{기능명}`  `fix/#이슈넘버/{기능명}`


## 📌 Commit Message Convention

| Type | 설명 |
| --- | --- |
| feature | 새로운 기능 추가 |
| fix | 버그 수정 |
| refactor | 코드 리팩토링 |
| style | 코드 스타일 변경 (포맷팅, 세미콜론 추가 등) |
| test | 테스트 코드 추가 및 수정 |
| chore | 빌드 설정, 패키지 변경, 문서 수정 |

> 커밋 예시: `fix/#이슈넘버 : 커밋 내용 설명`

## ⭐ PR & Code Review

- **PR** 1명 이상 approve 후에 merge
- 24시간 이내에 확인하기
  
