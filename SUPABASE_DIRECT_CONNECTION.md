# Quick Start: Connect Directly to Supabase

**No backend needed!** Connect your JavaFX app directly to Supabase using REST API + anon key.

## 🚀 5-Minute Setup

### Step 1: Get Your Supabase Keys

1. **Go to** https://app.supabase.com
2. **Create project** (or open existing)
3. **Navigate to** Settings → API
4. **Copy these two values:**
   - ✅ **Project URL** (looks like: `https://xxxxx.supabase.co`)
   - ✅ **anon public** key (long string starting with `eyJ...`)

### Step 2: Add Keys to `.env`

Open `.env` file and paste your keys:

```bash
# Required: Your Supabase credentials
SUPABASE_URL=https://your-actual-project-id.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.your-actual-key
```

### Step 3: Run Schema SQL

In Supabase dashboard:
1. Go to **SQL Editor**
2. Create new query
3. Paste and run:

```sql
-- Create tags table
CREATE TABLE tags (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50)
);

-- Create legislation table
CREATE TABLE legislation (
    id SERIAL PRIMARY KEY,
    ref_code VARCHAR(50) UNIQUE NOT NULL,
    title TEXT NOT NULL,
    summary TEXT,
    current_status VARCHAR(100),
    introduced_date DATE,
    last_action_date DATE
);

-- Create junction table for tags
CREATE TABLE legislation_tags (
    legislation_id INTEGER REFERENCES legislation(id) ON DELETE CASCADE,
    tag_id INTEGER REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (legislation_id, tag_id)
);

-- Insert sample tags
INSERT INTO tags (name, category) VALUES
('oil', 'commodity'),
('gold', 'commodity'),
('technology', 'sector'),
('agriculture', 'sector');

-- Insert sample legislation
INSERT INTO legislation (ref_code, title, current_status, introduced_date) VALUES
('HR-1234', 'Clean Energy Investment Act', 'In Committee', '2026-01-15'),
('S-5678', 'Tech Innovation Tax Credits', 'Passed House', '2026-01-20');

-- Link legislation to tags
INSERT INTO legislation_tags (legislation_id, tag_id) VALUES
(1, 1), -- HR-1234 tagged with 'oil'
(2, 3); -- S-5678 tagged with 'technology'
```

### Step 4: Test Connection

```bash
cd /home/light/Documents/ullivada/orwell/orwel-frontend
DISPLAY=:0 JAVA_HOME=~/.sdkman/candidates/java/current \
  ~/.sdkman/candidates/maven/3.9.12/bin/mvn clean compile javafx:run
```

**Look for:**
```
✓ Supabase client initialized: https://xxxxx.supabase.co
```

### Step 5: Login & See Data

- Login with demo credentials: `demo@orwel.com` / `demo123`
- Dashboard will fetch legislation from Supabase!

## 🔑 What You Need

### ✅ Required (2 things):
1. `SUPABASE_URL` - Your project URL
2. `SUPABASE_ANON_KEY` - Public anon key (**safe for frontend**)

### ❌ Not Required:
- ~~Custom backend API~~ (SupabaseClient connects directly!)
- ~~Service role key~~ (only for backend servers)
- ~~Database password~~ (only for direct PostgreSQL access)

## 🛡️ Security

**Q: Is anon key safe in frontend?**
A: **YES!** Supabase anon key is designed for client-side use:
- ✅ Row Level Security (RLS) protects data
- ✅ Only allows authenticated requests
- ✅ Can't bypass database permissions
- ❌ Don't use `service_role` key (full admin access!)

## 📊 How It Works

```
JavaFX App
    ↓
SupabaseClient.java (uses anon key)
    ↓
OkHttp → https://xxxxx.supabase.co/rest/v1/
    ↓
Supabase PostgREST API
    ↓
PostgreSQL Database
```

**No custom backend needed!** Supabase provides REST API automatically.

## 🔄 API Examples

### Get Legislation by Tags
```java
SupabaseClient client = SupabaseClient.getInstance();
List<String> tags = Arrays.asList("oil", "technology");
List<Legislation> results = client.getLegislationByTags(tags);
```

### Authenticate User
```java
AuthResponse auth = client.login("user@example.com", "password");
String token = auth.getAccessToken(); // JWT token
```

### Query with PostgREST
Direct REST calls work too:
```bash
curl 'https://xxxxx.supabase.co/rest/v1/legislation?select=*' \
  -H "apikey: YOUR_ANON_KEY"
```

## 🐛 Troubleshooting

### "Supabase client initialized: null"
- Check `.env` file exists in project root
- Verify `SUPABASE_URL` is filled in
- Restart application

### "401 Unauthorized"
- Check anon key is correct (copy from Settings → API)
- Enable RLS policies in Supabase
- Verify user is authenticated

### "No data returned"
- Check tables exist (run schema SQL)
- Insert sample data
- Verify user has commodity tags set

## 📚 Next Steps

1. ✅ Add your Supabase keys to `.env`
2. ✅ Run schema SQL in Supabase
3. ✅ Test the app
4. 🔄 Add more tables (committees, nominations, etc.)
5. 🔄 Set up Row Level Security policies
6. 🔄 Deploy backend API for complex logic (optional)

## 💡 Benefits of Direct Connection

**Pros:**
- ✅ No backend to deploy
- ✅ Instant setup (5 minutes)
- ✅ Automatic REST API
- ✅ Real-time updates possible
- ✅ Built-in authentication

**Cons:**
- ⚠️ Business logic in frontend
- ⚠️ Limited complex queries
- ⚠️ Need RLS policies set up properly

For complex apps, you can add a backend API later!
