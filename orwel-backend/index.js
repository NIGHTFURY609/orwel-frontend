const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const { nanoid } = require('nanoid');

const app = express();
const PORT = process.env.PORT || 8080;
const API_PREFIX = '/api';

app.use(cors());
app.use(bodyParser.json());

// In-memory stores (demo)
const users = new Map(); // token -> user
const usersByUsername = new Map(); // username -> user
let userIdCounter = 1;

const countries = [
  {
    code: 'US', name: 'United States', flag: '🇺🇸', summary: 'Large diversified market', warnings: []
  },
  {
    code: 'GB', name: 'United Kingdom', flag: '🇬🇧', summary: 'Established market', warnings: []
  },
  {
    code: 'DE', name: 'Germany', flag: '🇩🇪', summary: 'EU market leader', warnings: []
  }
];

const news = [
  {
    id: 1,
    title: 'Market opens higher on tech optimism',
    summary: 'Tech stocks lead gains as earnings beat estimates.',
    content: 'Full article content for demo.',
    source: 'Demo News',
    author: 'Reporter',
    publishedAt: new Date().toISOString(),
    url: 'https://example.com/article/1',
    imageUrl: '',
    tags: ['markets','tech'],
    countryCode: 'US',
    region: 'North America',
    category: 'general',
    relevanceScore: 0.5
  },
  {
    id: 2,
    title: 'European indices steady',
    summary: 'European markets hold steady amid mixed data.',
    content: 'Full article content for demo.',
    source: 'Demo News',
    author: 'Correspondent',
    publishedAt: new Date().toISOString(),
    url: 'https://example.com/article/2',
    imageUrl: '',
    tags: ['markets','europe'],
    countryCode: 'DE',
    region: 'Europe',
    category: 'general',
    relevanceScore: 0.4
  }
];

// Helper: create demo AuthResponse style object
function makeAuthResponse(user, token) {
  return {
    success: true,
    message: 'OK',
    token,
    user
  };
}

// Auth: register
app.post(`${API_PREFIX}/auth/register`, (req, res) => {
  const user = req.body;
  if (!user || !user.username) {
    return res.status(400).json({ success: false, message: 'Username required' });
  }
  if (usersByUsername.has(user.username)) {
    return res.status(409).json({ success: false, message: 'User already exists' });
  }
  user.id = userIdCounter++;
  // DO NOT store plaintext password in production — demo only
  usersByUsername.set(user.username, user);
  const token = nanoid();
  users.set(token, user);
  return res.json(makeAuthResponse(user, token));
});

// Auth: login
app.post(`${API_PREFIX}/auth/login`, (req, res) => {
  const { username, password } = req.body || {};
  if (!username) return res.status(400).json({ success: false, message: 'Username required' });
  const user = usersByUsername.get(username);
  if (!user) return res.status(401).json({ success: false, message: 'Invalid credentials' });
  // Skip password check for demo if password missing; otherwise compare
  if (user.password && password && user.password !== password) {
    return res.status(401).json({ success: false, message: 'Invalid credentials' });
  }
  const token = nanoid();
  users.set(token, user);
  return res.json(makeAuthResponse(user, token));
});

// Middleware to extract token and user
function authMiddleware(req, res, next) {
  const auth = req.headers['authorization'];
  if (!auth) return res.status(401).json({ message: 'Missing Authorization header' });
  const parts = auth.split(' ');
  if (parts.length !== 2) return res.status(401).json({ message: 'Invalid Authorization header' });
  const token = parts[1];
  const user = users.get(token);
  if (!user) return res.status(401).json({ message: 'Invalid token' });
  req.user = user;
  req.token = token;
  next();
}

// Users: get current
app.get(`${API_PREFIX}/users/me`, authMiddleware, (req, res) => {
  res.json(req.user);
});

// Users: update current
app.put(`${API_PREFIX}/users/me`, authMiddleware, (req, res) => {
  const update = req.body;
  const user = req.user;
  Object.assign(user, update);
  res.json(user);
});

// Countries
app.get(`${API_PREFIX}/countries`, (req, res) => {
  res.json(countries);
});

app.get(`${API_PREFIX}/countries/:code`, (req, res) => {
  const c = countries.find(x => x.code.toLowerCase() === req.params.code.toLowerCase());
  if (!c) return res.status(404).json({ message: 'Not found' });
  res.json(c);
});

app.get(`${API_PREFIX}/countries/:code/warnings`, (req, res) => {
  const c = countries.find(x => x.code.toLowerCase() === req.params.code.toLowerCase());
  if (!c) return res.status(404).json({ message: 'Not found' });
  res.json(c.warnings || []);
});

// News
app.get(`${API_PREFIX}/news/personalized`, authMiddleware, (req, res) => {
  // For demo return all with small personalization score
  const personalized = news.map(n => ({ ...n, relevanceScore: Math.random() }));
  res.json(personalized);
});

app.get(`${API_PREFIX}/news/general`, (req, res) => {
  res.json(news.filter(n => n.category === 'general' || !n.category));
});

app.get(`${API_PREFIX}/news/country/:code`, (req, res) => {
  const code = req.params.code.toUpperCase();
  res.json(news.filter(n => n.countryCode && n.countryCode.toUpperCase() === code));
});

app.get(`${API_PREFIX}/news/region/:region`, (req, res) => {
  const region = req.params.region.toLowerCase();
  res.json(news.filter(n => n.region && n.region.toLowerCase() === region));
});

app.get(`${API_PREFIX}/news/search`, (req, res) => {
  const q = (req.query.q || '').toLowerCase();
  const country = (req.query.country || '').toLowerCase();
  const region = (req.query.region || '').toLowerCase();

  let results = news.filter(n => {
    let ok = true;
    if (q) ok = ok && ((n.title || '').toLowerCase().includes(q) || (n.summary || '').toLowerCase().includes(q) || (n.content || '').toLowerCase().includes(q));
    if (country) ok = ok && (n.countryCode || '').toLowerCase() === country;
    if (region) ok = ok && (n.region || '').toLowerCase() === region;
    return ok;
  });
  res.json(results);
});

// Fallback
app.use((req, res) => {
  res.status(404).json({ message: 'Not found' });
});

app.listen(PORT, () => {
  console.log(`Orwel backend (demo) listening on port ${PORT}${API_PREFIX}`);
});
