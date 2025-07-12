## 👩🏼‍❤️‍👨🏼 ToGetHer
### 우리의 피임, 함께라서 더 안전하고 편안하게.

---

## 📌 Main Function

### **👤 회원 기능 (인증 및 인가)**

> 본 프로젝트는 **카카오 로그인 API**를 활용하여 사용자 인증 및 인가를 처리합니다.

1. 사용자가 카카오 로그인을 시도하면, 서버는 **OAuth2User 객체**를 통해 다음과 같은 사용자 정보를 받아 저장합니다.
    - 이메일  
    - 닉네임  
    - 카카오 ID  

2. 카카오로부터 **인증 코드**를 받은 후, 이를 기반으로 **JWT 토큰**을 발급합니다:
    - `AccessToken`은 브라우저의 **쿠키**에 저장됩니다.
        - **XSS 공격 방지**: `HttpOnly` 속성을 적용하여 자바스크립트를 통한 접근을 차단하고 보안성을 강화합니다.
        - **편의성**: 쿠키는 동일 도메인 요청 시 **자동으로 HTTP 요청에 포함**되므로, 프론트엔드에서 토큰을 직접 헤더에 추가할 필요가 없습니다.
    - `RefreshToken`은 **Redis**에 저장되어 안전하게 관리됩니다.

3. 사용자가 **로그아웃**할 경우 다음과 같은 처리가 이루어집니다:
    - 쿠키에 저장된 `AccessToken`을 **즉시 만료**시킵니다.
    - 해당 `AccessToken`은 **Redis 블랙리스트에 등록**되어 더 이상 사용할 수 없도록 처리합니다.

### 🔗 카카오 공유 기능

> 카카오 공유 API를 사용하여 사용자와 파트너가 쉽게 연동될 수 있도록 지원합니다.

- 온보딩 과정에서 사용자는 파트너와 공유할 수 있는 커스텀 **URL**을 생성할 수 있습니다.
- 파트너는 이 URL을 클릭해 간편하게 연동 절차를 시작하며, 연동이 완료되면 두 사용자는 **같은 캘린더를 공유**하게 됩니다.
- 이제 두 사용자는 각자의 역할에 따라 다음과 같은 기록을 함께 남기고 이중 피임을 실천할 수 있습니다:
  
    - 피임약 복용
    - 감정 기록
    - 관계 여부
    - 콘돔 사용 여부


### 📅 캘린더 기능

> 파트너와 사용자가 같은 캘린더를 공유하여 함께 기록을 작성할 수 있습니다.

- 카카오 공유 API를 통해 연결된 사용자와 파트너는 하나의 캘린더에서 아래와 같은 기록을 함께 작성합니다:
  
    - **피임약 복용 기록**
    - **감정 기록**
    - **관계 여부**
    - **콘돔 사용 여부**

- 사용자의 **`role` 값**(`ROLE_USER`, `ROLE_PARTNER`)에 따라 기록 가능한 항목과 접근 권한이 다릅니다.
  
    - **여성(ROLE_USER):** 관계 여부, 피임약 복용 여부, 감정 기록
    - **남성(ROLE_PARTNER):** 관계 여부, 콘돔 사용 여부

### 🔔 알림 기능

> 시스템 및 연동된 사용자와의 상호작용을 알림 스택으로 제공합니다.

- **SseEmitter**를 활용한 **SSE(Server-Sent Events)**를 통해 실시간 알림을 전송합니다.
- 사용자는 다음과 같은 알림을 실시간으로 받을 수 있습니다:
  
    - 피임약 **복용 알림**
    - **감정 기록 알림**
    - **약 구매 알림**
    - **파트너 연동 수락 알림**

---

## ✨ Contributors

| 항목       | 박준선 | 임승우 |
|------------|--------|--------|
| **프로필**   | <img src="https://github.com/goodjunseon.png" width="100"> | <img src="https://github.com/swoo0514.png" width="100"> |
| **GitHub**  | [@goodjunseon](https://github.com/goodjunseon) | [@Lims](https://github.com/swoo0514) |

---
## 💫 Architecture
![ToGetHer-Architecture.png](document/ToGetHer-Architecture.png)

---

## 📀 ERD

![ToGetHer-erd.png](document/ToGetHer-erd.png)

---

## 🤝🏻 Git Convention
### [Git Convention](document/GitConvention.md)

---
## 🤝🏻 Code Convention
### [Code Convention](document/CodeConvetion.md)

---
## Tech Stack
[![My Skills](https://skillicons.dev/icons?i=java,spring)](https://skillicons.dev)

[![My Skills](https://skillicons.dev/icons?i=mysql,redis)](https://skillicons.dev)

[![My Skills](https://skillicons.dev/icons?i=aws,linux,nginx)](https://skillicons.dev)

[![My Skills](https://skillicons.dev/icons?i=github,git,githubactions)](https://skillicons.dev)

[![My Skills](https://skillicons.dev/icons?i=idea,postman,figma,discord,notion)](https://skillicons.dev)

---
<details>
<summary> <h2>📁 Fold er Structure</h2></summary>
    <div markdown="1">
<h3>도메인 중심 구조 설계(DDD 구조)</h3> 
        
```
├── java
│   └── com
│       └── together
│           └── backend
│               ├── ToGetHerApplication.java
│               ├── domain
│               │   ├── calendar
│               │   │   ├── controller
│               │   │   │   └── CalendarController.java
│               │   │   ├── dto
│               │   │   │   ├── CalendarDetailResponse.java
│               │   │   │   ├── CalendarRecordRequest.java
│               │   │   │   ├── CalendarRecordResponse.java
│               │   │   │   └── CalendarSummaryResponse.java
│               │   │   ├── model
│               │   │   │   └── entity
│               │   │   │       ├── BasicRecord.java
│               │   │   │       ├── CondomUsage.java
│               │   │   │       ├── IntakeRecord.java
│               │   │   │       ├── IntakeType.java
│               │   │   │       ├── MoodType.java
│               │   │   │       └── RelationRecord.java
│               │   │   ├── repository
│               │   │   │   ├── BasicRecordRepository.java
│               │   │   │   ├── IntakeRecordRepository.java
│               │   │   │   └── RelationRecordRepository.java
│               │   │   └── service
│               │   │       ├── CalendarService.java
│               │   │       └── IntakeRecordInitService.java
│               │   ├── couple
│               │   │   ├── controller
│               │   │   │   └── CoupleController.java
│               │   │   ├── model
│               │   │   │   ├── entity
│               │   │   │   │   ├── Couple.java
│               │   │   │   │   └── CoupleStatus.java
│               │   │   │   ├── request
│               │   │   │   │   ├── ConnectRequest.java
│               │   │   │   │   └── CoupleRequest.java
│               │   │   │   └── response
│               │   │   │       ├── ConnectResponse.java
│               │   │   │       └── CoupleResponse.java
│               │   │   ├── repository
│               │   │   │   └── CoupleRepository.java
│               │   │   └── service
│               │   │       └── CoupleService.java
│               │   ├── notification
│               │   │   ├── controller
│               │   │   │   ├── NotificationSettingsController.java
│               │   │   │   └── NotificationSseController.java
│               │   │   ├── model
│               │   │   │   ├── Notification.java
│               │   │   │   ├── NotificationSettings.java
│               │   │   │   ├── NotificationType.java
│               │   │   │   ├── intake
│               │   │   │   │   ├── request
│               │   │   │   │   │   └── IntakeRequest.java
│               │   │   │   │   └── response
│               │   │   │   │       └── IntakeResponse.java
│               │   │   │   └── notification
│               │   │   │       ├── request
│               │   │   │       │   ├── NotificationDayRequest.java
│               │   │   │       │   ├── NotificationEnabledRequest.java
│               │   │   │       │   └── NotificationTimeRequest.java
│               │   │   │       └── response
│               │   │   │           ├── NotificationDayResponse.java
│               │   │   │           ├── NotificationEnabledResponse.java
│               │   │   │           └── NotificationTimeResponse.java
│               │   │   ├── repository
│               │   │   │   ├── NotificationRepository.java
│               │   │   │   └── NotificationSettingsRepository.java
│               │   │   └── service
│               │   │       ├── NotificationService.java
│               │   │       ├── NotificationSettingsService.java
│               │   │       └── NotificationSseService.java
│               │   ├── pill
│               │   │   ├── controller
│               │   │   │   └── UserPillController.java
│               │   │   ├── model
│               │   │   │   ├── IntakeInfo.java
│               │   │   │   ├── IntakeOption.java
│               │   │   │   ├── UserPill.java
│               │   │   │   ├── request
│               │   │   │   │   └── UserPillRequest.java
│               │   │   │   └── response
│               │   │   │       ├── TodayPillResponse.java
│               │   │   │       ├── UserPillRemainResponse.java
│               │   │   │       └── UserPillResponse.java
│               │   │   ├── repository
│               │   │   │   ├── IntakeInfoRepository.java
│               │   │   │   └── UserPillRepository.java
│               │   │   └── service
│               │   │       └── UserPillService.java
│               │   ├── sharing
│               │   │   ├── controller
│               │   │   │   └── SharingController.java
│               │   │   ├── model
│               │   │   │   ├── Sharing.java
│               │   │   │   ├── request
│               │   │   │   │   ├── ConfirmRequest.java
│               │   │   │   │   └── SaveUrlRequest.java
│               │   │   │   └── response
│               │   │   │       ├── ConfirmResponse.java
│               │   │   │       └── SaveUrlResponse.java
│               │   │   ├── repository
│               │   │   │   └── SharingRepository.java
│               │   │   └── service
│               │   │       └── SharingService.java
│               │   └── user
│               │       ├── controller
│               │       │   ├── MainPageController.java
│               │       │   ├── UserAuthController.java
│               │       │   ├── UserController.java
│               │       │   └── UserRedirectionController.java
│               │       ├── model
│               │       │   ├── entity
│               │       │   │   ├── Role.java
│               │       │   │   └── User.java
│               │       │   ├── request
│               │       │   │   └── UserRequest.java
│               │       │   └── response
│               │       │       ├── MyPageResponse.java
│               │       │       ├── UserResponse.java
│               │       │       └── mainpageinfo
│               │       │           ├── PartnerInfoResponse.java
│               │       │           ├── PillInfoResponse.java
│               │       │           └── UserInfoResponse.java
│               │       ├── repository
│               │       │   └── UserRepository.java
│               │       └── service
│               │           ├── MainPageService.java
│               │           ├── UserAuthService.java
│               │           ├── UserDeleteService.java
│               │           └── UserProfileService.java
│               └── global
│                   ├── common
│                   │   ├── BaseResponse.java
│                   │   ├── BaseResponseStatus.java
│                   │   └── model
│                   │       └── BaseEntity.java
│                   ├── config
│                   │   ├── CorsMvcConfig.java
│                   │   ├── RedisConfig.java
│                   │   └── SecurityConfig.java
│                   └── security
│                       ├── jwt
│                       │   ├── JWTFilter.java
│                       │   ├── model
│                       │   │   ├── BlackListToken.java
│                       │   │   └── RefreshToken.java
│                       │   ├── service
│                       │   │   ├── BlackListTokenService.java
│                       │   │   └── JwtTokenService.java
│                       │   └── util
│                       │       ├── CookieUtil.java
│                       │       └── JWTUtil.java
│                       └── oauth2
│                           ├── CustomOAuth2UserService.java
│                           ├── CustomSuccessHandler.java
│                           └── dto
│                               ├── CustomOAuth2User.java
│                               ├── KakaoResponse.java
│                               ├── OAuth2Response.java
│                               └── UserDTO.java
└── resources
    ├── application-dev.yml
    ├── application-prod.yml
    └── application.yml
```
</div>
</details>



