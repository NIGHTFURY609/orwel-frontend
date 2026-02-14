# Orwel Backend API Endpoints - COMPLETE SPECIFICATION

**Base URL**: `http://localhost:8080/api`  
**Authentication**: JWT Bearer Token (sent in `Authorization` header)

---

## Overview

The frontend sends user's commodity tags (e.g., "oil", "gold", "IT sector") to the backend.  
Backend matches these tags against the `tag` table in PostgreSQL and returns filtered results.

---

## Authentication Endpoints

### POST `/auth/login`
**Description**: Authenticate user and receive JWT token  
**Request Body**:
```json
{
  "username": "string",
  "password": "string"
}
```
**Response**:
```json
{
  "success": true,
  "token": "jwt-token-here",
  "message": "Login successful",
  "user": {
    "id": 1,
    "username": "trader1",
    "email": "trader@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "occupation": "Trader",
    "hasStocks": true,
    "commodityTags": ["oil", "gold", "IT sector"]
  }
}
```

### POST `/auth/register`
**Description**: Register new user  
**Request Body**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "firstName": "string",
  "lastName": "string",
  "occupation": "string",
  "hasStocks": boolean
}
```
**Response**: Same as login

---

## User Management Endpoints

### GET `/users/me`
**Description**: Get current authenticated user's profile  
**Headers**: `Authorization: Bearer {token}`  
**Response**:
```json
{
  "id": 1,
  "username": "trader1",
  "email": "trader@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "occupation": "Trader",
  "hasStocks": true,
  "commodityTags": ["oil", "gold", "IT sector"]
}
```

### PUT `/users/me`
**Description**: Update current user's profile  
**Headers**: `Authorization: Bearer {token}`  
**Request Body**: Same as User object  
**Response**: Updated User object

---

## User Tags Endpoints

### POST `/user/tags`
**Description**: Save user's commodity tags  
**Headers**: `Authorization: Bearer {token}`  
**Request Body**:
```json
["oil", "gold", "IT sector", "iron", "finance"]
```
**Response**: 200 OK

### GET `/user/tags`
**Description**: Get user's commodity tags  
**Headers**: `Authorization: Bearer {token}`  
**Response**:
```json
["oil", "gold", "IT sector"]
```

---

## Legislation Endpoints

### POST `/legislation/by-tags`
**Description**: Get legislation filtered by user's commodity tags  
**Headers**: `Authorization: Bearer {token}`  
**Request Body**:
```json
["oil", "gold", "IT sector"]
```

**Backend Logic**:
1. Match tags against `tag.tag_name` (case-insensitive)
2. Get matching `tag_id` values
3. Query: `SELECT * FROM legislation WHERE tag_id IN (matched_tag_ids)`
4. JOIN with `gov_mem` and `members` for sponsor/cosponsor details

**Response**:
```json
[
  {
    "legId": 1,
    "billType": "hr",
    "billNumber": "101",
    "refCode": "H.R. 101",
    "tagId": 5,
    "title": "Energy Security Act of 2026",
    "summary": "A bill to promote domestic oil production...",
    "policyArea": "Energy",
    "sponsorGovMemId": 123,
    "cosponsorGovMemId": 456,
    "dateIntroduced": "2026-01-15",
    "currentStatus": "Introduced",
    "tag": {
      "tagId": 5,
      "tagName": "oil",
      "naicsCode": "21",
      "naceCode": "B"
    },
    "sponsor": {
      "gm": 123,
      "member": {
        "memId": 50,
        "firstName": "John",
        "lastName": "Smith",
        "territoryPrimary": "USA",
        "politicalParty": "Republican"
      }
    },
    "cosponsor": {
      "gm": 456,
      "member": {
        "memId": 51,
        "firstName": "Jane",
        "lastName": "Doe",
        "territoryPrimary": "USA",
        "politicalParty": "Democrat"
      }
    }
  }
]
```

### GET `/legislation/{id}`
**Description**: Get specific legislation by ID  
**Headers**: `Authorization: Bearer {token}`  
**Response**: Single Legislation object

---

## Committee Hearings Endpoints

### POST `/hearings/by-tags`
**Description**: Get committee hearings (material_type='Hearing') filtered by tags  
**Headers**: `Authorization: Bearer {token}`  
**Request Body**:
```json
["oil", "finance"]
```

**Backend Logic**:
1. Match tags → get `tag_id` values
2. Query committees: `SELECT com_id FROM committees WHERE tag_id IN (matched_tag_ids)`
3. Query hearings: `SELECT * FROM committee_materials WHERE com_id IN (committee_ids) AND material_type='Hearing'`

**Response**:
```json
[
  {
    "matId": 1,
    "comId": 7,
    "legId": 1,
    "materialType": "Hearing",
    "title": "Hearing on Oil Price Controls",
    "officialRefNumber": "H.Hrg. 118-12",
    "officialSummary": "Committee hearing to examine...",
    "sectionAnalysis": "{...}",
    "fiscalImpactValue": 1500000000.00,
    "eventDate": "2026-02-20",
    "documentUrl": "https://congress.gov/...",
    "committee": {
      "comId": 7,
      "name": "US Senate Banking",
      "officialCode": "SSBK"
    },
    "legislation": {
      "legId": 1,
      "refCode": "H.R. 101",
      "title": "Energy Security Act"
    }
  }
]
```

---

## Nominations Endpoints

### POST `/nominations/by-tags`
**Description**: Get nominations filtered by tags  
**Headers**: `Authorization: Bearer {token}`  
**Request Body**:
```json
["finance", "securities"]
```

**Backend Logic**:
1. Match tags → get `tag_id` values
2. Query: `SELECT * FROM nominations WHERE tag_id IN (matched_tag_ids)`
3. JOIN with `members` table for nominee details

**Response**:
```json
[
  {
    "nomId": 1,
    "memId": 100,
    "confirmingGovId": 1,
    "positionTitle": "SEC Commissioner",
    "targetOrganization": "Securities and Exchange Commission",
    "tagId": 8,
    "dateReceived": "2026-01-10",
    "currentStatus": "Pending",
    "officialSummary": "Nomination of John Doe for SEC Commissioner...",
    "documentUrl": "https://senate.gov/...",
    "member": {
      "memId": 100,
      "firstName": "John",
      "lastName": "Doe",
      "territoryPrimary": "USA",
      "politicalParty": "Independent"
    },
    "tag": {
      "tagId": 8,
      "tagName": "finance"
    }
  }
]
```

---

## Committees Endpoints

### POST `/committees/by-tags`
**Description**: Get committees filtered by tags  
**Headers**: `Authorization: Bearer {token}`  
**Request Body**:
```json
["finance", "energy"]
```

**Backend Logic**:
```sql
SELECT c.*, t.tag_name, g.name as gov_name 
FROM committees c
JOIN tag t ON c.tag_id = t.tag_id
JOIN gov g ON c.gov_id = g.gov_id
WHERE c.tag_id IN (matched_tag_ids)
```

**Response**:
```json
[
  {
    "comId": 7,
    "govId": 1,
    "name": "US Senate Banking",
    "officialCode": "SSBK",
    "apiUrl": "https://api.propublica.org/...",
    "tagId": 8,
    "government": {
      "govId": 1,
      "name": "US Senate",
      "jurisdiction": "US",
      "branch": "Legislative"
    },
    "tag": {
      "tagId": 8,
      "tagName": "finance"
    }
  }
]
```

---

## Treaties Endpoints

### POST `/treaties/by-tags`
**Description**: Get treaties filtered by tags  
**Headers**: `Authorization: Bearer {token}`  
**Request Body**:
```json
["trade", "oil"]
```

**Backend Logic**:
```sql
SELECT * FROM treaties WHERE tag_id IN (matched_tag_ids)
```

**Response**:
```json
[
  {
    "treatyId": 1,
    "govId": 1,
    "officialNumber": "Treaty Doc. 118-1",
    "title": "US-UK Oil Trade Agreement",
    "foreignPartner": "United Kingdom",
    "tagId": 5,
    "transmissionDate": "2026-01-05",
    "currentStatus": "Pending",
    "officialSummary": "Agreement to facilitate oil trade...",
    "documentUrl": "https://state.gov/...",
    "tag": {
      "tagId": 5,
      "tagName": "oil"
    }
  }
]
```

---

## Research Reports Endpoints

### POST `/research-reports/by-tags`
**Description**: Get CRS/EU research reports filtered by tags  
**Headers**: `Authorization: Bearer {token}`  
**Request Body**:
```json
["oil", "renewable energy"]
```

**Backend Logic**:
```sql
SELECT * FROM research_reports WHERE tag_id IN (matched_tag_ids)
```

**Response**:
```json
[
  {
    "reportId": 1,
    "govId": 1,
    "reportNumber": "R47123",
    "title": "Oil Market Analysis 2026",
    "tagId": 5,
    "legId": 1,
    "summaryText": "This report examines...",
    "datePublished": "2026-01-15",
    "documentUrl": "https://crsreports.congress.gov/...",
    "tag": {
      "tagId": 5,
      "tagName": "oil"
    }
  }
]
```

---

## Dashboard Statistics Endpoint

### POST `/dashboard/stats`
**Description**: Get dashboard statistics for user's tags  
**Headers**: `Authorization: Bearer {token}`  
**Request Body**:
```json
["oil", "finance"]
```

**Backend Logic**:
```sql
-- Match tags
SELECT tag_id FROM tag WHERE tag_name IN ('oil', 'finance');

-- Count legislation
SELECT COUNT(*) FROM legislation WHERE tag_id IN (matched_tag_ids);

-- Count hearings
SELECT COUNT(*) FROM committee_materials 
WHERE material_type='Hearing' 
AND com_id IN (SELECT com_id FROM committees WHERE tag_id IN (matched_tag_ids));

-- Count nominations
SELECT COUNT(*) FROM nominations WHERE tag_id IN (matched_tag_ids);

-- Count treaties
SELECT COUNT(*) FROM treaties WHERE tag_id IN (matched_tag_ids);

-- Count committees
SELECT COUNT(*) FROM committees WHERE tag_id IN (matched_tag_ids);

-- Recent legislation (last 30 days)
SELECT COUNT(*) FROM legislation 
WHERE tag_id IN (matched_tag_ids) 
AND date_introduced >= CURRENT_DATE - INTERVAL '30 days';

-- Recent hearings (last 30 days)
SELECT COUNT(*) FROM committee_materials 
WHERE material_type='Hearing' 
AND event_date >= CURRENT_DATE - INTERVAL '30 days'
AND com_id IN (SELECT com_id FROM committees WHERE tag_id IN (matched_tag_ids));
```

**Response**:
```json
{
  "totalLegislation": 45,
  "totalHearings": 12,
  "totalNominations": 3,
  "totalTreaties": 2,
  "totalCommittees": 8,
  "recentLegislationCount": 5,
  "recentHearingsCount": 2
}
```

---

## Countries Endpoints (SIDE FEATURE - KEPT AS-IS)

### GET `/countries`
**Description**: Get all countries (for side feature)  
**Headers**: `Authorization: Bearer {token}`  
**Response**: Array of Country objects

### GET `/countries/{countryCode}`
**Description**: Get specific country details  
**Headers**: `Authorization: Bearer {token}`  
**Response**: Country object with policies/stances

### GET `/countries/{countryCode}/warnings`
**Description**: Get warnings for specific country  
**Headers**: `Authorization: Bearer {token}`  
**Response**: Array of Warning objects

---

## Implementation Notes

### 1. Tag Matching Algorithm

```java
// Pseudo-code for backend
List<String> userTags = ["oil", "finance", "gold"];

// Step 1: Find matching tag IDs
String sql = "SELECT tag_id FROM tag WHERE LOWER(tag_name) IN (?, ?, ?)";
List<Integer> tagIds = executeQuery(sql, userTags.map(toLowerCase));

// Step 2: Query legislation
String legSql = "SELECT l.*, t.tag_name, gm1.*, m1.*, gm2.*, m2.* " +
                "FROM legislation l " +
                "JOIN tag t ON l.tag_id = t.tag_id " +
                "LEFT JOIN gov_mem gm1 ON l.sponsor_gov_mem_id = gm1.gm " +
                "LEFT JOIN members m1 ON gm1.mem_id = m1.mem_id " +
                "LEFT JOIN gov_mem gm2 ON l.cosponsor_gov_mem_id = gm2.gm " +
                "LEFT JOIN members m2 ON gm2.mem_id = m2.mem_id " +
                "WHERE l.tag_id IN (?, ?, ?)";
```

### 2. Date Formats
- **Send dates to frontend**: ISO format `YYYY-MM-DD`
- **Example**: `"2026-02-15"`
- JavaFX uses `LocalDate.parse()` which expects ISO 8601

### 3. Authentication Flow
1. User logs in → Backend generates JWT token
2. Frontend stores token in `ApiService.authToken`
3. Every subsequent request includes `Authorization: Bearer {token}` header
4. Backend validates JWT on each request

### 4. CORS Configuration
Backend must allow:
- Origin: `http://localhost` (JavaFX runs locally)
- Methods: GET, POST, PUT, DELETE
- Headers: Authorization, Content-Type

### 5. Error Responses
```json
{
  "success": false,
  "message": "Error description",
  "errorCode": "INVALID_TAG"
}
```

HTTP Status Codes:
- `200 OK` - Success
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Missing/invalid token
- `404 Not Found` - Resource doesn't exist
- `500 Internal Server Error` - Server error

### 6. Performance Optimization
- **Index these columns**: `tag_id`, `tag_name`, `date_introduced`, `event_date`
- **Use pagination**: For large result sets (add `?page=1&limit=50`)
- **Cache frequent queries**: Dashboard stats, committee lists

### 7. NULL Handling
Many fields can be NULL (e.g., `cosponsor_gov_mem_id`, `leg_id` in hearings).  
Handle gracefully:
```json
{
  "cosponsorGovMemId": null,
  "cosponsor": null
}
```

### 8. Sample Tags
Common commodity tags traders might use:
- "oil", "crude oil", "petroleum"
- "gold", "silver", "copper", "iron"
- "IT sector", "technology", "semiconductors"
- "finance", "banking", "securities"
- "agriculture", "wheat", "corn"
- "energy", "renewable energy", "solar"
- "pharmaceuticals", "healthcare"
- "mining", "metals"

Backend should support **partial matching** or **synonyms** (e.g., "oil" matches "crude oil").

---

## Testing the API

### Using cURL

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"trader1", "password":"password123"}'

# Get legislation (replace TOKEN)
curl -X POST http://localhost:8080/api/legislation/by-tags \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '["oil", "finance"]'
```

### Using Postman
1. Create POST request to `/auth/login`
2. Copy token from response
3. Create POST request to `/legislation/by-tags`
4. Add header: `Authorization: Bearer {token}`
5. Body: `["oil", "gold"]`

---

## Database Join Examples

### Legislation with Sponsor/Cosponsor
```sql
SELECT 
  l.*,
  t.tag_name,
  m1.f_name as sponsor_first, m1.l_name as sponsor_last, m1.political_party as sponsor_party,
  m2.f_name as cosponsor_first, m2.l_name as cosponsor_last, m2.political_party as cosponsor_party
FROM legislation l
LEFT JOIN tag t ON l.tag_id = t.tag_id
LEFT JOIN gov_mem gm1 ON l.sponsor_gov_mem_id = gm1.gm
LEFT JOIN members m1 ON gm1.mem_id = m1.mem_id
LEFT JOIN gov_mem gm2 ON l.cosponsor_gov_mem_id = gm2.gm
LEFT JOIN members m2 ON gm2.mem_id = m2.mem_id
WHERE l.tag_id IN (5, 8, 12);
```

### Hearings with Committee Info
```sql
SELECT 
  cm.*,
  c.name as committee_name, c.official_code,
  l.ref_code as legislation_ref
FROM committee_materials cm
LEFT JOIN committees c ON cm.com_id = c.com_id
LEFT JOIN legislation l ON cm.leg_id = l.leg_id
WHERE cm.material_type = 'Hearing'
AND c.tag_id IN (5, 8);
```

---

## Frontend Model Classes Reference

All frontend model classes are in: `/src/main/java/com/orwel/model/`

- `Legislation.java` - Maps to `legislation` table
- `CommitteeMaterial.java` - Maps to `committee_materials` table
- `Nomination.java` - Maps to `nominations` table
- `Treaty.java` - Maps to `treaties` table
- `Committee.java` - Maps to `committees` table
- `Member.java` - Maps to `members` table
- `Government.java` - Maps to `gov` table
- `Tag.java` - Maps to `tag` table
- `DashboardStats.java` - For dashboard statistics

Backend should return JSON matching these class structures.

---

## Questions?

Contact the frontend team for clarifications on:
- Response format (JSON structure)
- Date/time formats
- Error handling
- New endpoint requirements
