# ✨Xpact✨

---
<a href="https://xpact.site/" target="_blank">
<img src="https://xpactbucket.s3.ap-northeast-2.amazonaws.com/PUBLIC/key1.png" alt="xpact" width="100%"/>
<img src="https://xpactbucket.s3.ap-northeast-2.amazonaws.com/PUBLIC/key2.png" alt="xpact" width="100%"/>
<img src="https://xpactbucket.s3.ap-northeast-2.amazonaws.com/PUBLIC/key3.png" alt="xpact" width="100%"/>
<img src="https://xpactbucket.s3.ap-northeast-2.amazonaws.com/PUBLIC/key4.png" alt="xpact" width="100%"/>
<img src="https://xpactbucket.s3.ap-northeast-2.amazonaws.com/PUBLIC/key5.png" alt="xpact" width="100%"/>
<img src="https://xpactbucket.s3.ap-northeast-2.amazonaws.com/PUBLIC/key6.png" alt="xpact" width="100%"/>
<img src="https://xpactbucket.s3.ap-northeast-2.amazonaws.com/PUBLIC/key7.png" alt="xpact" width="100%"/>

</a>


## 🎁Team Members (팀원 및 팀 소개)🎁

---
|                                          임원재                                           |                  전유연                   |
|:--------------------------------------------------------------------------------------:|:--------------------------------------:|
| <img src="https://avatars.githubusercontent.com/u/80938176?v=4" alt="임원재" width="150"> |   <img src="https://avatars.githubusercontent.com/u/109857975?v=4" alt="전유연" width="150">   |
|                                       [GitHub](https://github.com/yyytir777)                                       | [GitHub](https://github.com/youyeon11) |


## 🛠️기술 스택🛠️
> 안정적이고 확장성 있는 백엔드와 클라우드 기반 인프라 구성
- ☕ Java 17
- 🌱 Spring Boot 3.4.4
- 🐬 MySQL 8.0.40
- 🚀 Redis 7.4.1
- 🌐 Nginx 1.27.4
- 🧬 AWS Lambda
- 🗂️ AWS S3
- 🐳 Docker / Docker-Compose

## 🏗️아키텍처 구성🏗️

---
![](https://xpactbucket.s3.ap-northeast-2.amazonaws.com/PUBLIC/architecture.png)

<br>

## 🗃️ERD (Entity Relationship Diagram)️🗃️

---
![](https://xpactbucket.s3.ap-northeast-2.amazonaws.com/PUBLIC/erd.png)


## 📁프로젝트 구조📁

---
```
<pre><code>
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
 ┃ ┗ 📂main
 ┃   ┣ 📂java
 ┃   ┃ ┗ 📂com
 ┃   ┃   ┗ 📂itstime
 ┃   ┃     ┗ 📂xpact
 ┃   ┃       ┣ 📂domain # 도메인별 핵심 비즈니스 로직 영역
 ┃   ┃       ┃ ┣ 📂common
 ┃   ┃       ┃ ┣ 📂dashboard
 ┃   ┃       ┃ ┃ ┣ 📂controller # 도메인 내부 구조
 ┃   ┃       ┃ ┃ ┣ 📂dto
 ┃   ┃       ┃ ┃ ┃ ┣ 📂request
 ┃   ┃       ┃ ┃ ┃ ┗ 📂response
 ┃   ┃       ┃ ┃ ┣ 📂entity
 ┃   ┃       ┃ ┃ ┣ 📂repository
 ┃   ┃       ┃ ┃ ┗ 📂service
 ┃   ┃       ┃ ┣ 📂experience
 ┃   ┃       ┃ ┣ 📂guide
 ┃   ┃       ┃ ┣ 📂member
 ┃   ┃       ┃ ┗ 📂recruit
 ┃   ┃       ┣ 📂global # 전역적으로 사용하는 기술 패키지
 ┃   ┃       ┃ ┣ 📂aspect # AOP 관련 기능 (ex. 로깅)
 ┃   ┃       ┃ ┣ 📂auth # 인증/인가 처리 기능
 ┃   ┃       ┃ ┣ 📂config # 스프링 설정 클래스
 ┃   ┃       ┃ ┣ 📂exception # 전역 예외 처리 및 에러코드 정의
 ┃   ┃       ┃ ┣ 📂openai # openAI 관련 로직
 ┃   ┃       ┃ ┣ 📂response # 통일된 응답 정의
 ┃   ┃       ┃ ┣ 📂security # Spring Security 설정 및 필터 로직
 ┃   ┃       ┃ ┗ 📂webclient
 ┃   ┃       ┃   ┣ 📂openai # OpenAI 호출
 ┃   ┃       ┃   ┗ 📂school # 학교정보 API 호출
 ┃   ┃       ┣ 📂infra # 인프라, 외부 시스템 연동
 ┃   ┃       ┃ ┣ 📂lambda # AWS lambda 로직
 ┃   ┃       ┃ ┃ ┗ 📂recruit
 ┃   ┃       ┃ ┗ 📂s3 # AWS S3 로직
 ┃   ┃       ┃   ┣ 📂controller
 ┃   ┃       ┃   ┣ 📂dto
 ┃   ┃       ┃   ┗ 📂service
 ┃   ┃       ┗ 📜XpactApplication.java # Spring Boot 메인 실행 클래스
 ┃   ┗ 📂resources
 ┃     ┣ 📂static
 ┃     ┣ 📂templates
 ┃     ┣ 📜application-dev.yaml # 개발 프로필
 ┃     ┣ 📜application-local.yaml # 로컬 프로필
 ┃     ┗ 📜application.yaml # 공통 프로필
 ┣ 📜.env # 중요 환경변수 설정
 ┣ 📜.gitignore
 ┣ 📜Dockerfile # Spring 애플리케이션 패키징 Dockerfile
 ┣ 📜README.md
 ┣ 📜build.gradle
 ┗ 📜docker-compose.yaml # 컨테이너 정의 및 실행 설정 (API Server, Redis, Nginx, Certbot)
</code></pre>
```
## Git branch

---
- **main**: 🚀 배포 가능한 안정 버전
- **develop**: 🌱 기능 통합 및 테스트용 브랜치
- **feature/**: ✨ 새로운 기능 개발용 브랜치 (예: feature/#123/login-api)
- **fix/**: 🛠️ 긴급 수정 브랜치 (예: fix/#456/critical-bug)

> 브랜치 네이밍 예시: `feat/#이슈넘버/{기능명}` `fix/#이슈넘버/{기능명}`

## Commit Message Convention

---

| Type | 설명 |
| --- | --- |
| feature | 새로운 기능 추가 |
| fix | 버그 수정 |
| refactor | 코드 리팩토링 |
| chore | 빌드 설정, 패키지 변경, 문서 수정 |

> 커밋 예시: `fix/#이슈넘버 : 커밋 내용 설명`
