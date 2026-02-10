# KosaFootballCommunity (MSA Project) ⚽

## 📌 프로젝트 소개
**KosaFootballCommunity**는 축구 동호회 및 커뮤니티 활동을 지원하는 웹 애플리케이션입니다.  
기존의 모놀리식 아키텍처에서 **마이크로서비스 아키텍처(MSA)**로 전환하여 확장성과 유지보수성을 강화하였습니다.  
**AWS EC2 (Ubuntu)** 환경에서 Docker Compose를 통해 배포되어 운영 중입니다.

---

## 🏗️ 아키텍처 (Architecture)

사용자는 도메인(`www.itsfootball.store`)을 통해 접속하며, AWS 클라우드 상의 Ubuntu 서버 내 Docker Network로 요청이 전달됩니다.

```mermaid
graph TD
    User[사용자 / 클라이언트] -->|HTTPS: www.itsfootball.store| DNS[DNS (Route 53 / Gabia)]
    DNS -->|Resolved IP| Nginx[Nginx Gateway (Port 80)]
    
    subgraph "AWS EC2 (Ubuntu Linux)"
        subgraph "Docker Network (msateamprj_network)"
            Nginx
            
            Nginx -->|/stadium_api| Stadium[Stadium API :8100]
            Nginx -->|/reservation_api| Reservation[Reservation API :8101]
            Nginx -->|/kakaopay_api| KakaoPay[KakaoPay API :8102]
            Nginx -->|/security_api| Security[Security API :8110]
            Nginx -->|/login_api| Login[Login API :8111]
            Nginx -->|/alarm_api| Alarm[Alarm API :8120]
            Nginx -->|/club_api| Club[Club API :8121]
            Nginx -->|/recruit_api| Recruit[Recruit API :8122]
            Nginx -->|/widget_api| Weather[Weather API :8130]
            Nginx -->|/board_api| Board[Board API :8131]
            
            Stadium --> DB[(MariaDB :3306)]
            Reservation --> DB
            KakaoPay --> DB
            Security --> DB
            Login --> DB
            Alarm --> DB
            Club --> DB
            Recruit --> DB
            Weather --> DB
            Board --> DB
        end
    end
```

---

## 🛠️ 기술 스택 (Tech Stack)

| 구분 | 사용 기술 | 설명 |
| :--- | :--- | :--- |
| **Frontend** | ![Vue.js](https://img.shields.io/badge/Vue.js-35495E?style=flat&logo=vuedotjs&logoColor=4FC08D) ![Vite](https://img.shields.io/badge/Vite-646CFF?style=flat&logo=vite&logoColor=white) | Vue 3, Pinia (상태관리), Bootstrap 5 (UI), Axios |
| **Backend** | ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=springboot&logoColor=white) ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white) | Spring Boot 기반 마이크로서비스 (보안, 인증, 비즈니스 로직) |
| **Database** | ![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat&logo=mariadb&logoColor=white) | 데이터 영속성 관리 (Docker Container) |
| **Infrastructure** | ![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat&logo=amazon-aws&logoColor=white) ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white) | AWS EC2 (Ubuntu), Docker Compose, Nginx Gateway |

---

## 📡 서비스 목록 및 포트 구성

AWS EC2 내부의 Docker 프라이빗 네트워크(`msateamprj_network`)에서 구동됩니다. 외부에서는 오직 Nginx(80)를 통해서만 접근 가능합니다.

| 서비스 명 (Service) | 역할 (Role) | 내부 포트 (Internal Port) | Nginx 경로 (Path) |
| :--- | :--- | :--- | :--- |
| **nginx** | 웹 서버 & API 게이트웨이 | 80 | `/` (Frontend) |
| **stadium_api** | 구장 정보 관리 | 8100 | `/stadium_api` |
| **reservation_api** | 예약 시스템 | 8101 | `/reservation_api` |
| **kakaopay_api** | 결제 연동 (카카오페이) | 8102 | `/kakaopay_api` |
| **security_api** | 보안 및 인증 (JWT, OAuth) | 8110 | `/security_api` |
| **login_api** | 로그인 및 회원 관리 | 8111 | `/login_api` |
| **alarm_api** | 알림 서비스 | 8120 | `/alarm_api` |
| **club_api** | 동호회(클럽) 관리 | 8121 | `/club_api` |
| **recruit_api** | 용병/팀원 모집 | 8122 | `/recruit_api` |
| **weather_api** | 날씨 정보 연동 | 8130 | `/widget_api` |
| **board_api** | 게시판 커뮤니티 | 8131 | `/board_api` |
| **mariadb** | 통합 데이터베이스 | 3306 | - |

---

## 🚀 배포 및 실행 (Deployment)

### 환경 (Environment)
- **Cloud**: AWS EC2
- **OS**: Ubuntu Linux
- **Domain**: `www.itsfootball.store`

### 실행 방법 (How to Run)

1. **저장소 클론 (Clone)**
   ```bash
   git clone <repository-url>
   cd KosaFootballCommunity
   ```

2. **Docker Compose 실행**
   프로젝트 루트 경로에서 아래 명령어를 실행하여 모든 서비스를 시작합니다.
   ```bash
   # 백그라운드 실행 및 빌드
   docker-compose up -d --build
   ```

3. **상태 확인**
   ```bash
   docker-compose ps
   ```

4. **로그 확인 (실시간)**
   ```bash
   docker-compose logs -f
   ```

---

## 📂 프로젝트 구조

```
KosaFootballCommunity/
├── backend/            # Spring Boot 마이크로서비스 소스 코드
├── frontend/           # Vue.js 프론트엔드 소스 코드
├── docker-compose.yml  # 전체 서비스 오케스트레이션 설정
├── nginx.conf          # Nginx 게이트웨이 설정
└── README.md           # 프로젝트 문서 (현재 파일)
```
