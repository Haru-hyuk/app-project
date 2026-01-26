# 꽃필무렵 (Floral Stories) API 명세서

> 백엔드 구현 현황을 반영한 API 명세서입니다.

---

## 목차

1. [인증 API (Auth)](#1-인증-api-auth)
2. [꽃 정보 API (Flowers)](#2-꽃-정보-api-flowers)
3. [검색 API (Search)](#3-검색-api-search)
4. [즐겨찾기 API (Favorites)](#4-즐겨찾기-api-favorites)
5. [조회 기록 API (View History)](#5-조회-기록-api-view-history)
6. [꽃집 API (Shops)](#6-꽃집-api-shops)
7. [사용자 프로필 API (Users)](#7-사용자-프로필-api-users)
8. [인기 키워드 API (Keywords)](#8-인기-키워드-api-keywords)
9. [카드 메시지 API (Cards)](#9-카드-메시지-api-cards)
10. [앱 설정 API (Settings)](#10-앱-설정-api-settings)
11. [테스트 API (Test)](#11-테스트-api-test)

---

## 서버 정보

| 항목 | 값 |
|------|-----|
| Base URL | `http://localhost:8080/api` |
| Database | MySQL (`127.0.0.1:3306/flower_db`) |
| 인증 방식 | JWT (Bearer Token) |

---

## 1. 인증 API (Auth)

> **컨트롤러**: `AuthController.java`
> **서비스**: `AuthService.java`
> **상태**: ✅ 구현 완료

### 1.1 회원가입
```
POST /api/auth/signup
```

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "nickname": "꽃사랑",
  "userName": "홍길동",
  "userBirth": "19900101"
}
```

**Response** `200 OK`
```
"회원가입 완료"
```

---

### 1.2 로그인
```
POST /api/auth/login
```

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response** `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

### 1.3 토큰 재발급
```
POST /api/auth/refresh
```

**Request Body**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response** `200 OK`
```json
{
  "accessToken": "새로운 액세스 토큰",
  "refreshToken": "새로운 리프레시 토큰"
}
```

---

### 1.4 로그아웃
```
POST /api/auth/logout
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Response** `200 OK`
```
"로그아웃 완료"
```

---

### 1.5 이메일 찾기
```
POST /api/auth/find-email
```

**Request Body**
```json
{
  "userName": "홍길동",
  "userBirth": "19900101"
}
```

**Response** `200 OK`
```json
{
  "email": "user@example.com"
}
```

---

### 1.6 비밀번호 재설정
```
POST /api/auth/reset-password
```

**Request Body**
```json
{
  "userName": "홍길동",
  "email": "user@example.com"
}
```

**Response** `200 OK`
```json
{
  "message": "임시 비밀번호가 이메일로 발송되었습니다."
}
```

---

### 1.7 이메일 중복 확인
```
POST /api/auth/check-email
```

**Request Body**
```json
{
  "email": "user@example.com"
}
```

**Response** `200 OK`
```json
{
  "exists": true,
  "message": "이미 사용 중인 이메일입니다."
}
```

---

### 1.8 닉네임 중복 확인
```
POST /api/auth/check-nickname
```

**Request Body**
```json
{
  "nickname": "꽃사랑"
}
```

**Response** `200 OK`
```json
{
  "exists": false,
  "message": "사용 가능한 닉네임입니다."
}
```

---

### 1.7 회원 탈퇴
```
POST /api/auth/delete-account
```

> **Description (EN)**: Delete user account and all associated data

**Headers**
```
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "password": "currentPassword123"
}
```

**Response** `200 OK`
```
"회원 탈퇴가 완료되었습니다."
```

**Note**: This operation permanently deletes the user account and all related data including favorites, search history, and view history.

---

## 2. 꽃 정보 API (Flowers)

> **컨트롤러**: `FlowerController.java`
> **서비스**: `FlowerService.java`
> **상태**: ✅ 구현 완료

### 2.1 전체 꽃 목록 조회
```
GET /api/flowers
```

**Response** `200 OK`
```json
[
  {
    "flowerId": 725,
    "flowerName": "힙노시스 카네이션",
    "floriography": ["존경"],
    "flowerKeyword": ["경의", "배려", "감사"],
    "imageUrl": "https://...",
    "season": ["spring", "summer", "autumn", "winter"]
  }
]
```

---

### 2.2 오늘의 꽃 조회
```
GET /api/flowers/today
```

**Response** `200 OK`
```json
{
  "flowerId": 1,
  "flowerName": "더글라스",
  "floriography": ["불로장수", "용감"],
  "flowerKeyword": ["용기", "장수", "건강"],
  "flowerOrigin": "더글라스는 북아메리카 서부가 원산지로...",
  "flowerDescribe": "...",
  "imageUrl": "https://...",
  "season": ["spring", "summer"],
  "todayFlower": "2026-01-16",
  "isFavorite": false
}
```

---

### 2.3 계절별 꽃 조회
```
GET /api/flowers/season?season={season}
```

**Query Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| season | string | `spring`, `summer`, `autumn`, `winter` |

**Response** `200 OK`
```json
[
  {
    "flowerId": 1,
    "flowerName": "프리지아",
    "floriography": ["새로운 시작"],
    "flowerKeyword": ["응원"],
    "imageUrl": "https://...",
    "season": ["spring"]
  }
]
```

---

### 2.4 꽃 상세 정보 조회
```
GET /api/flowers/{flowerId}
```

**Path Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| flowerId | Integer | 꽃 ID |

**Response** `200 OK`
```json
{
  "flowerId": 1,
  "flowerName": "프리지아",
  "floriography": ["순결", "영원한 우정"],
  "flowerKeyword": ["신뢰의 상징"],
  "flowerOrigin": "남아프리카가 원산지인 프리지아는...",
  "flowerDescribe": "꽃말로는 '순결'과 '영원한 우정'을 의미하며...",
  "imageUrl": "https://...",
  "season": ["spring"],
  "todayFlower": null,
  "isFavorite": false
}
```

---

### 2.5 키워드로 꽃 검색
```
GET /api/flowers/keyword?keyword={keyword}
```

**Query Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| keyword | string | 꽃 키워드 (예: "사랑", "감사") |

**Response** `200 OK`
```json
[
  {
    "flowerId": 1,
    "flowerName": "장미",
    "floriography": ["사랑"],
    "flowerKeyword": ["사랑", "열정"],
    "imageUrl": "https://...",
    "season": ["spring", "summer"]
  }
]
```

---

### 2.6 꽃 이름 검색
```
GET /api/flowers/search?name={name}
```

**Query Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| name | string | 꽃 이름 (부분 일치) |

**Response** `200 OK`
```json
[
  {
    "flowerId": 1,
    "flowerName": "장미",
    "floriography": ["사랑"],
    "flowerKeyword": ["사랑"],
    "imageUrl": "https://...",
    "season": ["spring"]
  }
]
```

---

### 2.7 꽃말/키워드로 검색
```
GET /api/flowers/search/floriography?query={query}
```

**Query Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| query | string | 꽃말 또는 키워드 (예: "사랑", "감사") |

**Response** `200 OK`
```json
[
  {
    "flowerId": 1,
    "flowerName": "장미",
    "floriography": ["사랑"],
    "flowerKeyword": ["사랑", "열정"],
    "imageUrl": "https://...",
    "season": ["spring"]
  }
]
```

**설명**: 검색 결과가 있으면 `popular_keyword` 테이블의 해당 키워드 카운트가 자동으로 증가합니다.

---

### 2.8 계절별 랜덤 꽃 1개 조회
```
GET /api/flowers/season/random?season={season}
```

**Query Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| season | string | `spring`, `summer`, `autumn`, `winter` |

**Response** `200 OK`
```json
{
  "flowerId": 1,
  "flowerName": "프리지아",
  "floriography": ["새로운 시작"],
  "flowerKeyword": ["응원"],
  "imageUrl": "https://...",
  "season": ["spring"]
}
```

**설명**: 특정 계절의 꽃 중 랜덤으로 1개를 반환합니다.

---

### 2.9 모든 계절의 랜덤 꽃 한 번에 조회
```
GET /api/flowers/season/random/all
```

**Response** `200 OK`
```json
{
  "spring": {
    "flowerId": 1,
    "flowerName": "프리지아",
    "floriography": ["새로운 시작"],
    "flowerKeyword": ["응원"],
    "imageUrl": "https://...",
    "season": ["spring"]
  },
  "summer": {
    "flowerId": 2,
    "flowerName": "해바라기",
    "floriography": ["기쁨"],
    "flowerKeyword": ["행복"],
    "imageUrl": "https://...",
    "season": ["summer"]
  },
  "autumn": {
    "flowerId": 3,
    "flowerName": "코스모스",
    "floriography": ["순수"],
    "flowerKeyword": ["아름다움"],
    "imageUrl": "https://...",
    "season": ["autumn"]
  },
  "winter": {
    "flowerId": 4,
    "flowerName": "동백꽃",
    "floriography": ["기다림"],
    "flowerKeyword": ["인내"],
    "imageUrl": "https://...",
    "season": ["winter"]
  }
}
```

**설명**: 봄, 여름, 가을, 겨울 각 계절의 랜덤 꽃을 한 번에 조회합니다. 4번의 API 호출을 1번으로 최적화한 엔드포인트입니다.

---

## 3. 검색 API (Search)

> **컨트롤러**: `SearchController.java`
> **서비스**: `SearchService.java`, `SearchHistoryService.java`
> **상태**: ✅ 구현 완료

### 3.1 벡터 유사도 기반 시맨틱 검색
```
POST /api/search?query={query}
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Query Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| query | string | 검색어 (예: "친구 졸업 축하") |

**Response** `200 OK`
```json
{
  "results": [
    {
      "flower": {
        "flowerId": 1,
        "flowerName": "장미",
        "floriography": ["사랑", "기쁨"],
        "flowerKeyword": ["사랑", "열정"],
        "imageUrl": "https://...",
        "season": ["spring", "summer"]
      },
      "similarity": 0.95
    }
  ],
  "count": 20,
  "query": "친구 졸업 축하"
}
```

**설명**:
- DeepSeek Chat API로 검색어 의미 분석
- SemanticQueryBuilder로 의미 문장 생성
- Ollama Embedding API로 벡터 변환
- 모든 꽃의 임베딩과 코사인 유사도 계산
- 유사도 높은 순으로 상위 20개 반환

---

### 3.2 최근 검색어 조회
```
GET /api/search/recent
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Response** `200 OK`
```json
["부모님 생신", "화이트데이", "졸업 축하"]
```

---

### 3.3 검색어 삭제
```
DELETE /api/search/recent?query={query}
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Query Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| query | string | 삭제할 검색어 |

---

### 3.4 검색어 전체 삭제
```
DELETE /api/search/recent/all
```

**Headers**
```
Authorization: Bearer {accessToken}
```

---

## 4. 즐겨찾기 API (Favorites)

> **컨트롤러**: `FavoriteController.java`
> **서비스**: `FavoriteService.java`
> **상태**: ✅ 구현 완료

### 4.1 즐겨찾기 목록 조회
```
GET /api/favorites
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Response** `200 OK`
```json
[
  {
    "favoriteId": 1,
    "flowerId": 1,
    "flowerName": "프리지아",
    "floriography": ["신뢰와 우정"],
    "imageUrl": "https://...",
    "createdAt": "2026-01-16T10:00:00"
  }
]
```

---

### 4.2 즐겨찾기 추가
```
POST /api/favorites
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "flowerId": 1
}
```

**Response** `200 OK`
```json
{
  "favoriteId": 1,
  "flowerId": 1,
  "flowerName": "프리지아",
  "floriography": ["신뢰와 우정"],
  "imageUrl": "https://...",
  "createdAt": "2026-01-16T10:00:00"
}
```

---

### 4.3 즐겨찾기 삭제
```
DELETE /api/favorites/{flowerId}
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Path Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| flowerId | Integer | 꽃 ID |

**Response** `200 OK`
```json
{
  "message": "즐겨찾기에서 삭제되었습니다."
}
```

---

### 4.4 즐겨찾기 여부 확인
```
GET /api/favorites/check/{flowerId}
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Path Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| flowerId | Integer | 꽃 ID |

**Response** `200 OK`
```json
{
  "isFavorite": true,
  "favoriteId": 1
}
```

---

## 5. 조회 기록 API (View History)

> **컨트롤러**: `ViewHistoryController.java`
> **서비스**: `ViewHistoryService.java`
> **상태**: ✅ 구현 완료

### 5.1 조회 기록 목록
```
GET /api/view-history
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Response** `200 OK`
```json
[
  {
    "viewId": 1,
    "flowerId": 1,
    "flowerName": "프리지아",
    "imageUrl": "https://...",
    "createdAt": "2026-01-16T14:30:00"
  }
]
```

---

### 5.2 조회 기록 저장
```
POST /api/view-history
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "flowerId": 1
}
```

**Response** `200 OK`
```json
{
  "message": "조회 기록이 저장되었습니다."
}
```

---

### 5.3 조회 기록 삭제
```
DELETE /api/view-history/{viewId}
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Path Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| viewId | Integer | 조회 기록 ID |

**Response** `200 OK`
```json
{
  "message": "조회 기록이 삭제되었습니다."
}
```

---

### 5.4 조회 기록 전체 삭제
```
DELETE /api/view-history
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Response** `200 OK`
```json
{
  "message": "모든 조회 기록이 삭제되었습니다."
}
```

---

## 6. 꽃집 API (Shops)

> **컨트롤러**: `ShopController.java`
> **서비스**: `ShopService.java`
> **외부 API**: 카카오 지도 API
> **상태**: ✅ 구현 완료

### 6.1 주변 꽃집 검색
```
GET /api/shops/nearby?lat={lat}&lng={lng}&radius={radius}&keyword={keyword}
```

**Query Parameters**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| lat | double | O | - | 위도 |
| lng | double | O | - | 경도 |
| radius | int | X | 1000 | 검색 반경 (m, 최대 20000) |
| keyword | string | X | "꽃집" | 검색 키워드 |

**Response** `200 OK`
```json
[
  {
    "name": "이일플라워",
    "address": "서울 중구 세종대로 124",
    "lat": 37.567143916618885,
    "lng": 126.97789378754335,
    "distance": 71,
    "phone": "1588-1943",
    "placeUrl": "http://place.map.kakao.com/2062274720"
  }
]
```

---

## 7. 사용자 프로필 API (Users)

> **컨트롤러**: `UserController.java`
> **서비스**: `UserService.java`
> **상태**: ✅ 구현 완료

### 7.1 현재 사용자 정보 조회
```
GET /api/users/me
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Response** `200 OK`
```json
{
  "userId": 1,
  "email": "user@example.com",
  "nickname": "꽃사랑",
  "userName": "홍길동",
  "userBirth": "19900101",
  "userIntro": "꽃과 향기를 사랑하는 수집가",
  "profileImage": "https://..."
}
```

---

### 7.2 사용자 정보 수정
```
PUT /api/users/me
```

> **Description (EN)**: Update user profile (nickname, intro, profile image)

**Headers**
```
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "nickname": "새로운닉네임",
  "userIntro": "새로운 소개",
  "profileImage": "https://..."
}
```

**Response** `200 OK`
```json
{
  "userId": 1,
  "email": "user@example.com",
  "nickname": "새로운닉네임",
  "userName": "홍길동",
  "userBirth": "19900101",
  "userIntro": "새로운 소개",
  "profileImage": "https://..."
}
```

---

### 7.3 비밀번호 변경
```
PUT /api/users/me/password
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "currentPassword": "현재비밀번호",
  "newPassword": "새비밀번호",
  "confirmNewPassword": "새비밀번호"
}
```

**Response** `200 OK`
```json
{
  "message": "비밀번호가 변경되었습니다."
}
```

---

### 7.4 프로필 이미지 업로드
```
POST /api/users/me/profile-image
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "imageUrl": "https://..."
}
```

**Response** `200 OK`
```json
{
  "message": "프로필 이미지가 업로드되었습니다.",
  "imageUrl": "https://..."
}
```

---

## 8. 인기 키워드 API (Keywords)

> **컨트롤러**: `KeywordController.java`
> **서비스**: `KeywordService.java`
> **상태**: ✅ 구현 완료

### 8.1 인기 검색 키워드 조회
```
GET /api/keywords/popular
```

**인증**: 불필요

**Response** `200 OK`
```json
{
  "keywords": ["졸업식", "생일", "기념일", "축하", "감사"],
  "count": 10
}
```

**설명**: `popular_keyword` 테이블에서 검색 횟수(`Count`) 기준 내림차순으로 상위 10개 반환

---

### 8.2 트렌딩 키워드 조회
```
GET /api/keywords/trending
```

**인증**: 불필요

**Response** `200 OK`
```json
{
  "keywords": ["졸업식", "생일", "기념일"],
  "count": 10
}
```

**설명**: 최근 7일간 업데이트된 키워드 중 검색 횟수 기준 상위 10개 반환. 데이터가 없으면 전체 인기 키워드 반환

---

## 9. 카드 메시지 API (Cards)

> **컨트롤러**: `CardMessageController.java`
> **서비스**: `CardMessageService.java`
> **상태**: ✅ 구현 완료

### 9.1 AI 카드 메시지 생성

> **Description (EN)**: AI Card Message Generation Feature - Generate personalized card messages using DeepSeek AI based on flower information and user query

```
POST /api/cards/message
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "flowerId": 1,
  "flowerName": "프리지아",
  "floriography": ["순결", "영원한 우정"],
  "query": "여자친구 생일선물"
}
```

**Response** `200 OK`
```json
{
  "message": "프리지아처럼 순수하고 아름다운 당신의 생일을 축하합니다. 영원한 우정처럼 변하지 않는 사랑을 전합니다."
}
```

**설명**:
- DeepSeek Chat API를 사용하여 꽃 정보와 검색어(상황)를 기반으로 개인화된 카드 메시지 생성
- 생성된 메시지는 `Flower_Message` 테이블에 저장됨
- 사용자 ID, 꽃 ID, 메시지 내용이 함께 저장되어 나중에 조회 가능

---

## 10. 앱 설정 API (Settings)

> **컨트롤러**: `SettingsController.java`
> **서비스**: `SettingsService.java`
> **상태**: ✅ 구현 완료

### 9.1 사용자 설정 조회
```
GET /api/settings
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Response** `200 OK`
```json
{
  "pushNotifications": true,
  "emailNotifications": true,
  "language": "ko",
  "theme": "light",
  "appVersion": "1.0.0"
}
```

---

### 9.2 사용자 설정 저장
```
PUT /api/settings
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "pushNotifications": true,
  "emailNotifications": false,
  "language": "ko",
  "theme": "dark"
}
```

**Response** `200 OK`
```json
{
  "pushNotifications": true,
  "emailNotifications": false,
  "language": "ko",
  "theme": "dark",
  "appVersion": "1.0.0"
}
```

---

## 11. 테스트 API (Test)

> **컨트롤러**: `TestController.java`
> **서비스**: `SearchService.java`
> **상태**: ✅ 구현 완료

### 11.1 DeepSeek 임베딩 테스트
```
POST /api/test/deepseek/embedding
```

**Headers**
```
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "text": "친구 생일 축하"
}
```

**Response** `200 OK`
```json
{
  "text": "친구 생일 축하",
  "embedding": "[0.123, 0.456, ...]",
  "dimension": 768,
  "message": "임베딩 변환 성공"
}
```

**설명**: 텍스트를 임베딩 벡터로 변환하여 테스트

---

## API 구현 현황 요약

### ✅ 구현 완료

| 분류 | 메서드 | 엔드포인트 | 설명 | 인증 |
|------|--------|-----------|------|------|
| **Auth** | POST | /api/auth/signup | 회원가입 | X |
| | POST | /api/auth/login | 로그인 | X |
| | POST | /api/auth/refresh | 토큰 재발급 | X |
| | POST | /api/auth/logout | 로그아웃 | O |
| | POST | /api/auth/find-email | 이메일 찾기 | X |
| | POST | /api/auth/reset-password | 비밀번호 재설정 | X |
| | POST | /api/auth/check-email | 이메일 중복 확인 | X |
| | POST | /api/auth/check-nickname | 닉네임 중복 확인 | X |
| | POST | /api/auth/delete-account | 회원 탈퇴 | O |
| **Flowers** | GET | /api/flowers | 전체 꽃 목록 | O |
| | GET | /api/flowers/today | 오늘의 꽃 | O |
| | GET | /api/flowers/season | 계절별 꽃 | O |
| | GET | /api/flowers/season/random | 계절별 랜덤 꽃 1개 | O |
| | GET | /api/flowers/season/random/all | 모든 계절 랜덤 꽃 (최적화) | O |
| | GET | /api/flowers/{flowerId} | 꽃 상세 정보 (즐겨찾기 여부 포함) | O |
| | GET | /api/flowers/keyword | 키워드 검색 | O |
| | GET | /api/flowers/search | 이름 검색 | O |
| | GET | /api/flowers/search/floriography | 꽃말/키워드 검색 | O |
| **Search** | POST | /api/search | 벡터 유사도 기반 시맨틱 검색 | O |
| | GET | /api/search/recent | 최근 검색어 | O |
| | DELETE | /api/search/recent | 검색어 삭제 | O |
| | DELETE | /api/search/recent/all | 검색어 전체 삭제 | O |
| **Favorites** | GET | /api/favorites | 즐겨찾기 목록 | O |
| | POST | /api/favorites | 즐겨찾기 추가 | O |
| | DELETE | /api/favorites/{flowerId} | 즐겨찾기 삭제 | O |
| | GET | /api/favorites/check/{flowerId} | 즐겨찾기 여부 확인 | O |
| **History** | GET | /api/view-history | 조회 기록 목록 | O |
| | POST | /api/view-history | 조회 기록 저장 | O |
| | DELETE | /api/view-history/{viewId} | 조회 기록 삭제 | O |
| | DELETE | /api/view-history | 조회 기록 전체 삭제 | O |
| **Shops** | GET | /api/shops/nearby | 주변 꽃집 검색 | O |
| **Users** | GET | /api/users/me | 현재 사용자 정보 조회 | O |
| | PUT | /api/users/me | 사용자 정보 수정 | O |
| | PUT | /api/users/me/password | 비밀번호 변경 | O |
| | POST | /api/users/me/profile-image | 프로필 이미지 업로드 | O |
| **Keywords** | GET | /api/keywords/popular | 인기 검색 키워드 | X |
| | GET | /api/keywords/trending | 트렌딩 키워드 | X |
| **Cards** | POST | /api/cards/message | AI 카드 메시지 생성 | O |
| **Settings** | GET | /api/settings | 사용자 설정 조회 | O |
| | PUT | /api/settings | 사용자 설정 저장 | O |
| **Test** | POST | /api/test/deepseek/embedding | 임베딩 벡터 테스트 | O |

### ❌ 미구현

| 분류 | 엔드포인트 | 설명 |
|------|-----------|------|
| **Auth** | /api/auth/social-login | 소셜 로그인 (카카오/네이버/Apple) |
| **Notifications** | /api/notifications | 알림 목록/읽음/삭제 (프론트엔드 요구사항 없음) |

---

## 인증 방식

- **JWT (JSON Web Token)** 사용
- Access Token: 1시간 유효
- Refresh Token: 2주 유효 (DB 저장)
- 헤더 형식: `Authorization: Bearer {accessToken}`

---

## 에러 응답 형식

```json
{
  "success": false,
  "status": 401,
  "message": "유효하지 않은 토큰입니다."
}
```

| HTTP Status | Description |
|-------------|-------------|
| 400 | 잘못된 요청 |
| 401 | 인증 실패 |
| 403 | 권한 없음 |
| 404 | 리소스 없음 |
| 500 | 서버 오류 |

---

---

## 데이터 타입 변경 사항

### Long → Integer 변경
모든 엔티티의 ID 필드가 `Long`에서 `Integer`로 변경되었습니다.
- 이유: MySQL 데이터베이스와의 호환성 향상
- 영향 범위:
  - 모든 Entity 클래스 (`@Id` 필드)
  - 모든 Repository 인터페이스
  - 모든 Service 메서드
  - 모든 Controller 엔드포인트
  - 모든 DTO 클래스

---

---

## 주요 변경 사항 (최신 업데이트)

### 추가된 API
1. **카드 메시지 API** (`/api/cards/message`)
   - AI 기반 개인화된 카드 메시지 생성 기능
   - DeepSeek Chat API 활용
   - 생성된 메시지는 DB에 저장

2. **계절별 랜덤 꽃 조회 API**
   - `/api/flowers/season/random`: 특정 계절의 랜덤 꽃 1개
   - `/api/flowers/season/random/all`: 모든 계절의 랜덤 꽃 한 번에 조회 (성능 최적화)

3. **꽃말/키워드 검색 API** (`/api/flowers/search/floriography`)
   - 꽃말 또는 키워드로 꽃 검색
   - 검색 결과가 있으면 인기 키워드 카운트 자동 증가

---

*문서 최종 업데이트: 2026-01-20*
*버전: 3.1.0*
