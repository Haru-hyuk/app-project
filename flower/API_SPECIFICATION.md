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
| flowerId | Long | 꽃 ID |

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

## 3. 검색 API (Search)

> **컨트롤러**: `SearchController.java`
> **서비스**: `SearchService.java`, `SearchHistoryService.java`
> **상태**: ✅ 구현 완료

### 3.1 시맨틱 검색
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
[0.123, 0.456, ...]  // 임베딩 벡터
```

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
| flowerId | Long | 꽃 ID |

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
| flowerId | Long | 꽃 ID |

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
| viewId | Long | 조회 기록 ID |

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
| **Flowers** | GET | /api/flowers | 전체 꽃 목록 | O |
| | GET | /api/flowers/today | 오늘의 꽃 | O |
| | GET | /api/flowers/season | 계절별 꽃 | O |
| | GET | /api/flowers/{flowerId} | 꽃 상세 정보 (즐겨찾기 여부 포함) | O |
| | GET | /api/flowers/keyword | 키워드 검색 | O |
| | GET | /api/flowers/search | 이름 검색 | O |
| **Search** | POST | /api/search | 시맨틱 검색 + 최근 검색어 저장 | O |
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
| **Test/Dev** | POST | /api/test/deepseek | 의미 해석 테스트 | O |
| | POST | /api/test/deepseek/embedding | 의미문 + 임베딩 벡터 생성 | O |

### ❌ 미구현

| 분류 | 엔드포인트 | 설명 |
|------|-----------|------|
| **Users** | /api/users/me | 프로필 조회/수정 |
| **Messages** | /api/messages | 메시지 생성/저장/조회 |
| **Auth** | /api/auth/social-login | 소셜 로그인 (카카오/네이버/Apple) |

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

*문서 최종 업데이트: 2026-01-16*
*버전: 2.0.1*
