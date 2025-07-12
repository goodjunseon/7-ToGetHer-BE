## 👩🏼‍❤️‍👨🏼 ToGetHer
### 우리의 피임, 함께라서 더 안전하고 편안하게.

---

## 📌 Main Function

작성중...

---

## 

---


## ✨ Contributors

| 항목       | 박준선 | 임승우 |
|------------|--------|--------|
| **프로필**   | <img src="https://github.com/goodjunseon.png" width="100"> | <img src="https://github.com/swoo0514.png" width="100"> |
| **GitHub**  | [@goodjunseon](https://github.com/goodjunseon) | [@Lims](https://github.com/swoo0514) |

---
## 🔗Architecture

작성중...

---

## 📀 ERD

작성중...

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



