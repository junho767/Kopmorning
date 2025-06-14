# ⚽ 정보 중심 축구 커뮤니티 플랫폼

> **개발자:** 이준호 ( 개인 프로젝트 )  
> 축구 팬들을 위한, 정보 중심의 건전한 커뮤니티를 목표로 한 프로젝트입니다.

---

## 🧭 개발 배경 및 계기

1. **커뮤니티의 본질 회복**
    - 여러 커뮤니티에서 정보를 나누기보다 친목, 욕설, 무관한 내용으로 소통의 질이 저하되는 경우가 많습니다.
    - 커뮤니티의 본질인 **정보 공유**에 집중한 플랫폼을 만들고자 하였습니다.

2. **축구 팬으로서의 열정**
    - 축구를 사랑하는 팬으로서, 흩어진 정보를 쉽고 편리하게 확인하고 의견을 나눌 수 있는 **정보 통합형 커뮤니티**를 직접 만들고 싶었습니다.

---

## 🧩 주요 기능

### 📌 커뮤니티 게시판 기능
- 게시글 CRUD
- 댓글 및 대댓글
- 좋아요 기능 (Redis 기반 동시성 이슈 해결)

### 🌐 해외 축구 정보 연동 (Open API)
- 팀 별 순위표
- 세부 스탯 확인
- 팀 정보 제공
- 팀 관련 최신 뉴스

### 🔐 로그인 / 인증 시스템
- Form 로그인
- OAuth2: Google, Kakao, Naver 연동
- JWT 기반 인증 처리 (Redis 기반 토큰 관리)

### 🛠 관리자 기능 & 실시간 고객센터 (Q&A)
- WebSocket 기반 실시간 채팅 고객센터
- 관리자 전용 페이지

---

## 🌱 GitHub 협업 및 커밋 전략

- **브랜치 전략:** GitHub Flow
- **이슈 관리:** Kanban 보드 기반 이슈 생성 및 관리
- **작업 방식:**
    1. 이슈 생성
    2. 관련 브랜치 생성
    3. 브랜치 작업 후 Pull Request
- **커밋 컨벤션**: 기능 기반 커밋 메시지 작성

---

## 🧱 기술 스택

### 💻 Language
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=openjdk&logoColor=white)

### ⚙ Framework & Library
![SpringBoot](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![SpringSecurity](https://img.shields.io/badge/SpringSecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![SpringDataJPA](https://img.shields.io/badge/SpringDataJPA-6DB33F?style=for-the-badge&logo=hibernate&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Springdoc OpenAPI](https://img.shields.io/badge/Springdoc%20OpenAPI-68B5F4?style=for-the-badge&logo=swagger&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white)
![SSE](https://img.shields.io/badge/Server--Sent%20Events-lightgrey?style=for-the-badge)
![WebSocket](https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=websocket&logoColor=white)

### 🗄 Database
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)

### 🛠 Development Tools
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)
![Amazon S3](https://img.shields.io/badge/AmazonS3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)
![AWS ECS](https://img.shields.io/badge/AWS%20ECS-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)

### 📊 Monitoring & Logging Tools
- 미정

### 🤝 Collaboration Tools
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white)
![Figma](https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white)

### 📜 시스템 아키텍처
### 🎯 ERD

