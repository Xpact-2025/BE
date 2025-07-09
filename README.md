# ✨ Xpact - 취준생을 위한 경험 정리 서비스

## 🛠️ 기술 스택
> 안정적이고 확장성 있는 백엔드와 클라우드 기반 인프라 구성
- ☕ Java 17
- 🌱 Spring Boot 3.4.4
- 🐬 MySQL 8.0.40
- 🚀 Redis 7.4.1
- 🌐 Nginx 1.27.4
- 🧬 AWS Lambda
- 🗂️ AWS S3
- 🐳 Docker / Docker-Compose

## 🏗️ 아키텍처 구성
![](https://github.com/user-attachments/assets/4b750337-0c15-4192-b6be-946a6fb65a17)

<br>

## 🗃️ ERD (Entity Relationship Diagram)
![](https://private-user-images.githubusercontent.com/80938176/464281461-8594eae7-e068-4007-9a79-7bf5a971cdc0.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTIwNzcyOTUsIm5iZiI6MTc1MjA3Njk5NSwicGF0aCI6Ii84MDkzODE3Ni80NjQyODE0NjEtODU5NGVhZTctZTA2OC00MDA3LTlhNzktN2JmNWE5NzFjZGMwLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTA3MDklMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwNzA5VDE2MDMxNVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTA3NjQzN2RhODNkZWI0NjRhMDc5ZWUwMDAyZWZkYzg0OTgzMTRkOTRmNjUyZGE5ZTZiMjgzNTgzNDVkZjMxNjEmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.uroAeongjpjgHgyyCVlJm9_H9s9Oys3ZmziRqiUdNOw)


## 📁 프로젝트 구조
```
📦xpact
 ┣ 📂.github 
 ┃ ┣ 📂ISSUE_TEMPLATE # 이슈 템플릿
 ┃ ┣ 📂workflows # Github Actions Workflow 설정
 ┣ 📂lambda # AWS Lambda 코드 (python)
 ┃ ┣ 📂recruit # 직무 크롤링
 ┃ ┗ 📂scrap # 활동 크롤링 
 ┣ 📂nginx
 ┃ ┣ 📂html
 ┣ 📂src
 ┃ ┣ 📂main
 ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┗ 📂itstime
 ┃ ┃ ┃ ┃ ┃ ┗ 📂xpact
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂domain # 도메인별 핵심 비즈니스 로직 영역
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂common
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dashboard
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller # 도메인 내부 구조
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂request
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂response
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂experience
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂guide
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂recruit
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂global # 전역적으로 사용하는 기술 패키지
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂aspect # AOP 관련 기능 (ex. 로깅)
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂auth # 인증/인가 처리 기능
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config # 스프링 설정 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂exception # 전역 예외 처리 및 에러코드 정의
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂openai # openAI 관련 로직
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂response # 통일된 응답 정의
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂security # Spring Security 설정 및 필터 로직 
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂webclient
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂openai # OpenAI 호출
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂school # 학교정보 API 호출
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂infra # 인프라, 외부 시스템 연동
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂lambda # AWS lambda 로직
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂recruit
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂s3 # AWS S3 로직
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜XpactApplication.java # Spring Boot 메인 실행 클래스
 ┃ ┃ ┗ 📂resources
 ┃ ┃ ┃ ┣ 📂static
 ┃ ┃ ┃ ┣ 📂templates
 ┃ ┃ ┃ ┣ 📜application-dev.yaml # 개발 프로필
 ┃ ┃ ┃ ┣ 📜application-local.yaml # 로컬 프로필
 ┃ ┃ ┃ ┣ 📜application.yaml # 공통 프로필
 ┣ 📜.env # 중요 환경변수 설정
 ┣ 📜.gitignore
 ┣ 📜Dockerfile # Spring 애플리케이션 패키징 Dockerfile
 ┣ 📜README.md 
 ┣ 📜build.gradle
 ┗ 📜docker-compose.yaml # 컨테이너 정의 및 실행 설정 (API Server, Redis, Nginx, Certbot)
```
## Git branch
- **main**: 🚀 배포 가능한 안정 버전
- **develop**: 🌱 기능 통합 및 테스트용 브랜치
- **feature/**: ✨ 새로운 기능 개발용 브랜치 (예: feature/#123/login-api)
- **fix/**: 🛠️ 긴급 수정 브랜치 (예: fix/#456/critical-bug)

> 브랜치 네이밍 예시: `feat/#이슈넘버/{기능명}` `fix/#이슈넘버/{기능명}`

## Commit Message Convention

| Type | 설명 |
| --- | --- |
| feature | 새로운 기능 추가 |
| fix | 버그 수정 |
| refactor | 코드 리팩토링 |
| chore | 빌드 설정, 패키지 변경, 문서 수정 |

> 커밋 예시: `fix/#이슈넘버 : 커밋 내용 설명`
