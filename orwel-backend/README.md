Orwel Backend (Demo)

Quick start:

1. Install dependencies:

```bash
cd orwel-backend
npm install
```

2. Start server:

```bash
npm start
```

The server listens on port 8080 and exposes the API under `/api` to match the frontend `AppConfig.API_BASE_URL`.

Note: This is an in-memory demo server—data is not persisted. For production, replace with a proper database, authentication, and input validation.