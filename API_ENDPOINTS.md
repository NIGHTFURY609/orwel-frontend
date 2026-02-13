# REST API Endpoints Documentation

This document describes all the REST API endpoints that need to be implemented by the backend team.

**Base URL**: `http://localhost:8080/api` (configurable via `config.properties`)

## Authentication Endpoints

### POST `/api/auth/login`
Login user and get authentication token.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "success": true,
  "token": "jwt_token_string",
  "user": {
    "id": 1,
    "username": "string",
    "email": "string",
    ...
  },
  "message": "Login successful"
}
```

### POST `/api/auth/register`
Register a new user.

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "firstName": "string",
  "lastName": "string",
  ...
}
```

**Response:**
```json
{
  "success": true,
  "token": "jwt_token_string",
  "user": { ... },
  "message": "Registration successful"
}
```

## User Endpoints

All user endpoints require authentication header: `Authorization: Bearer <token>`

### GET `/api/users/me`
Get current authenticated user's profile.

**Response:**
```json
{
  "id": 1,
  "username": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "address": "string",
  "country": "string",
  "city": "string",
  "postalCode": "string",
  "phoneNumber": "string",
  "marriageStatus": "SINGLE|MARRIED|DIVORCED|WIDOWED|DOMESTIC_PARTNERSHIP",
  "dateOfBirth": "2000-01-01",
  "occupation": "string",
  "hasStocks": true,
  "plansToTravel": true,
  "interests": ["politics", "tax", "travel"],
  "locationInfo": {
    "latitude": 40.7128,
    "longitude": -74.0060,
    "formattedAddress": "string",
    "countryCode": "US",
    "region": "string",
    "city": "string",
    "timezone": "string"
  }
}
```

### PUT `/api/users/me`
Update current user's profile.

**Request Body:** Same as User object above (all fields optional)

**Response:** Updated User object

## Country Endpoints

### GET `/api/countries`
Get list of all countries with their policies and stances.

**Response:**
```json
[
  {
    "code": "US",
    "name": "United States",
    "flag": "🇺🇸",
    "policies": [...],
    "stances": [...],
    "abidesBy": ["NATO", "WTO", ...],
    "doesNotFollow": ["Kyoto Protocol", ...],
    "summary": "string",
    "warnings": [...]
  },
  ...
]
```

### GET `/api/countries/{countryCode}`
Get detailed information about a specific country.

**Path Parameters:**
- `countryCode`: ISO country code (e.g., "US", "GB", "FR")

**Response:** Country object with full details

### GET `/api/countries/{countryCode}/warnings`
Get personalized warnings for the current user regarding a specific country.

**Path Parameters:**
- `countryCode`: ISO country code

**Response:**
```json
[
  {
    "id": 1,
    "title": "Stock Trading Restrictions",
    "message": "This country has restrictions on foreign stock trading...",
    "severity": "high",
    "category": "stocks",
    "countryCode": "US",
    "affectedUserTypes": ["stock_holders"]
  },
  ...
]
```

## News Endpoints

### GET `/api/news/personalized`
Get personalized news articles based on user profile.

**Response:**
```json
[
  {
    "id": 1,
    "title": "string",
    "content": "string",
    "summary": "string",
    "source": "string",
    "author": "string",
    "publishedAt": "2026-02-13T10:00:00",
    "url": "string",
    "imageUrl": "string",
    "tags": ["politics", "tax"],
    "countryCode": "US",
    "region": "North America",
    "relatedPoliticians": ["John Doe"],
    "category": "personalized",
    "relevanceScore": 0.95
  },
  ...
]
```

### GET `/api/news/general`
Get general news articles from all countries.

**Query Parameters:**
- `page`: Page number (default: 1)
- `limit`: Items per page (default: 20)

**Response:** Array of NewsArticle objects

### GET `/api/news/country/{countryCode}`
Get news articles for a specific country.

**Path Parameters:**
- `countryCode`: ISO country code

**Response:** Array of NewsArticle objects

### GET `/api/news/region/{region}`
Get news articles filtered by region.

**Path Parameters:**
- `region`: Region name (e.g., "North America", "Europe", "Asia")

**Response:** Array of NewsArticle objects

### GET `/api/news/search`
Search news articles.

**Query Parameters:**
- `q`: Search query (required)
- `country`: Filter by country code (optional)
- `region`: Filter by region (optional)

**Response:** Array of NewsArticle objects matching the search

## Error Responses

All endpoints may return error responses in the following format:

```json
{
  "success": false,
  "message": "Error description",
  "errorCode": "ERROR_CODE",
  "timestamp": "2026-02-13T10:00:00"
}
```

**Common HTTP Status Codes:**
- `200`: Success
- `201`: Created
- `400`: Bad Request
- `401`: Unauthorized
- `403`: Forbidden
- `404`: Not Found
- `500`: Internal Server Error

## Notes for Backend Team

1. **Authentication**: Use JWT tokens. Token should be included in `Authorization` header as `Bearer <token>`.

2. **Personalization**: The `/api/news/personalized` endpoint should consider:
   - User's country and location
   - User's interests
   - User's profile (marriage status, stocks, travel plans)
   - User's location info for location-based news

3. **Warnings**: The `/api/countries/{countryCode}/warnings` endpoint should analyze:
   - User's profile against country policies
   - User's stock holdings vs country stock regulations
   - User's travel plans vs country travel restrictions
   - User's marital status vs country policies
   - Any other relevant factors

4. **CORS**: Ensure CORS is enabled for the frontend origin.

5. **Pagination**: Consider implementing pagination for news endpoints that return large datasets.

6. **Rate Limiting**: Consider implementing rate limiting for public endpoints.
