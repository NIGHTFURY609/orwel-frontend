# Supabase Setup Guide

## Prerequisites
- Supabase account (sign up at https://supabase.com)
- PostgreSQL database knowledge (basic)

## Step 1: Create Supabase Project

1. Go to https://app.supabase.com
2. Click "New Project"
3. Enter project details:
   - **Name**: orwel-backend
   - **Database Password**: Choose a strong password (save it!)
   - **Region**: Choose closest to your users
4. Wait 2-3 minutes for project to initialize

## Step 2: Get API Keys

1. In your Supabase project dashboard, go to **Settings** → **API**
2. Copy the following values:
   - **Project URL** (e.g., `https://xxxxx.supabase.co`)
   - **anon public** key
   - **service_role** key (⚠️ Keep this secret!)

## Step 3: Configure Environment Variables

1. Open `.env` file in the project root
2. Fill in your Supabase credentials:

```env
# Backend API URL (your deployed backend or localhost)
BACKEND_API_URL=http://localhost:8080/api

# Supabase Configuration
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key-here
SUPABASE_SERVICE_ROLE_KEY=your-service-role-key-here

# Database URL (from Supabase Settings → Database → Connection string)
DATABASE_URL=postgresql://postgres:your-password@db.your-project.supabase.co:5432/postgres
```

## Step 4: Set Up Database Schema

1. In Supabase dashboard, go to **SQL Editor**
2. Run the SQL schema (from `database/schema.sql` or see below)

### Database Tables

Run these SQL commands in order:

```sql
-- 1. Create tags table
CREATE TABLE tags (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW()
);

-- 2. Create legislation table
CREATE TABLE legislation (
    id SERIAL PRIMARY KEY,
    ref_code VARCHAR(50) UNIQUE NOT NULL,
    title TEXT NOT NULL,
    summary TEXT,
    sponsor_id INTEGER,
    current_status VARCHAR(100),
    introduced_date DATE,
    last_action_date DATE,
    full_text_url TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 3. Create legislation_tags junction table
CREATE TABLE legislation_tags (
    legislation_id INTEGER REFERENCES legislation(id) ON DELETE CASCADE,
    tag_id INTEGER REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (legislation_id, tag_id)
);

-- 4. Create committees table
CREATE TABLE committees (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    chamber VARCHAR(50),
    description TEXT,
    website_url TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- 5. Create committee_materials table
CREATE TABLE committee_materials (
    id SERIAL PRIMARY KEY,
    committee_id INTEGER REFERENCES committees(id),
    title TEXT NOT NULL,
    material_type VARCHAR(50),
    event_date DATE,
    document_url TEXT,
    summary TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- 6. Create members table
CREATE TABLE members (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    party VARCHAR(50),
    state VARCHAR(50),
    chamber VARCHAR(50),
    office_address TEXT,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT NOW()
);

-- 7. Create nominations table
CREATE TABLE nominations (
    id SERIAL PRIMARY KEY,
    member_id INTEGER REFERENCES members(id),
    position_title VARCHAR(255) NOT NULL,
    target_organization VARCHAR(255),
    nominated_date DATE,
    confirmation_status VARCHAR(50),
    vote_date DATE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- 8. Create users table (for authentication)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    occupation VARCHAR(100),
    has_stocks BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 9. Create user_commodity_tags table
CREATE TABLE user_commodity_tags (
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    tag_id INTEGER REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, tag_id)
);

-- 10. Insert sample commodity tags
INSERT INTO tags (name, category) VALUES
('oil', 'commodity'),
('gold', 'commodity'),
('technology', 'sector'),
('agriculture', 'sector'),
('healthcare', 'sector'),
('finance', 'sector'),
('energy', 'sector'),
('defense', 'sector');
```

## Step 5: Enable Row Level Security (RLS)

For production, enable RLS on tables:

```sql
-- Enable RLS on users table
ALTER TABLE users ENABLE ROW LEVEL SECURITY;

-- Policy: Users can read their own data
CREATE POLICY "Users can read own data" ON users
    FOR SELECT USING (auth.uid()::text = id::text);

-- Policy: Users can update their own data
CREATE POLICY "Users can update own data" ON users
    FOR UPDATE USING (auth.uid()::text = id::text);
```

## Step 6: Test Connection

Run the app and check logs:

```bash
DISPLAY=:0 JAVA_HOME=~/.sdkman/candidates/java/current \
~/.sdkman/candidates/maven/3.9.12/bin/mvn javafx:run
```

You should see:
```
=== Orwel Configuration ===
Backend API URL: http://localhost:8080/api
Supabase URL: https://xxxxx.supabase.co
Supabase Anon Key: ***
Database URL: ***
===========================
```

## Troubleshooting

### "Supabase URL not configured"
- Check `.env` file exists in project root
- Verify `SUPABASE_URL` is set correctly
- Restart the application

### "Connection refused"
- Verify your backend API is running
- Check `BACKEND_API_URL` in `.env`
- For now, app works offline with SQLite

### "Database connection failed"
- Check Supabase project is active
- Verify `DATABASE_URL` has correct password
- Check firewall/network settings

## Next Steps

1. ✅ Configure `.env` with Supabase credentials
2. ✅ Run SQL schema in Supabase
3. 🔄 Deploy backend API (Java/Python/Node.js)
4. 🔄 Connect backend to Supabase PostgreSQL
5. 🔄 Implement API endpoints from BACKEND_API_SPECIFICATION.md

## Security Notes

⚠️ **NEVER commit `.env` file to Git!**
- `.env` is already in `.gitignore`
- Use `.env.example` as template for team members
- Store production keys in secure environment (Heroku Config Vars, AWS Secrets Manager, etc.)

## Support

For issues, check:
- Supabase docs: https://supabase.com/docs
- Project logs: Supabase Dashboard → Logs
- Backend API logs: Check your backend server console
