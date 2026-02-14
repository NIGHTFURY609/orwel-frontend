package com.orwel.service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.orwel.config.AppConfig;
import com.orwel.model.*;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Direct Supabase REST API client using PostgREST
 * Matches actual Supabase schema: tag, legislation, committees,
 * committee_materials, nominations, members
 */
public class SupabaseClient {
    private static SupabaseClient instance;
    private final OkHttpClient client;
    private final Gson gson;
    private final String supabaseUrl;
    private final String supabaseKey;
    private String authToken;
    
    private SupabaseClient() {
        this.supabaseUrl = AppConfig.SUPABASE_URL;
        this.supabaseKey = AppConfig.SUPABASE_ANON_KEY;
        
        this.client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build();
        // snake_case mapping: tagId <-> tag_id, legId <-> leg_id, etc.
        // Custom LocalDate adapter for "yyyy-MM-dd" date strings from Supabase
        this.gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                    private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
                    @Override
                    public void write(JsonWriter out, LocalDate value) throws IOException {
                        out.value(value != null ? value.format(fmt) : null);
                    }
                    @Override
                    public LocalDate read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) { in.nextNull(); return null; }
                        String s = in.nextString();
                        try { return LocalDate.parse(s, fmt); } catch (Exception e) { return null; }
                    }
                })
                .create();
        
        if (isConfigured()) {
            System.out.println("✓ Supabase client initialized: " + supabaseUrl);
        }
    }
    
    public static SupabaseClient getInstance() {
        if (instance == null) {
            instance = new SupabaseClient();
        }
        return instance;
    }
    
    public boolean isConfigured() {
        return supabaseUrl != null && !supabaseUrl.isEmpty() 
            && supabaseKey != null && !supabaseKey.isEmpty();
    }
    
    public void setAuthToken(String token) {
        this.authToken = token;
    }
    
    private Request.Builder createRequestBuilder() {
        Request.Builder builder = new Request.Builder()
                .addHeader("apikey", supabaseKey)
                .addHeader("Content-Type", "application/json");
        
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }
        
        return builder;
    }
    
    // ─── Helper: resolve tag names → tag_id list ───
    
    private List<Integer> getTagIdsByNames(List<String> tagNames) throws IOException {
        String namesParam = tagNames.stream()
                .map(name -> "\"" + name + "\"")
                .collect(Collectors.joining(","));
        
        // Table: tag (singular), columns: tag_id, tag_name
        String url = supabaseUrl + "/rest/v1/tag?tag_name=in.(" + namesParam + ")&select=tag_id";
        
        Request request = createRequestBuilder().url(url).get().build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                System.out.println("  Supabase tag lookup: " + body);
                Type listType = new TypeToken<List<TagIdRow>>(){}.getType();
                List<TagIdRow> rows = gson.fromJson(body, listType);
                if (rows != null) {
                    return rows.stream().map(r -> r.tag_id).collect(Collectors.toList());
                }
            }
            return new ArrayList<>();
        }
    }
    
    // ─── Legislation (direct tag_id on legislation table) ───
    
    public List<Legislation> getLegislationByTags(List<String> tags) throws IOException {
        if (!isConfigured()) return new ArrayList<>();
        
        List<Integer> tagIds = getTagIdsByNames(tags);
        if (tagIds.isEmpty()) {
            System.out.println("  Supabase: no matching tag IDs found");
            return new ArrayList<>();
        }
        
        String tagIdsParam = tagIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        // legislation has tag_id FK directly — no junction table
        String url = supabaseUrl + "/rest/v1/legislation?tag_id=in.(" + tagIdsParam 
                + ")&order=date_introduced.desc&limit=20&select=*";
        
        System.out.println("  Supabase legislation query: tag_id in (" + tagIdsParam + ")");
        
        Request request = createRequestBuilder().url(url).get().build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                System.out.println("  Supabase legislation response length: " + body.length());
                Type listType = new TypeToken<List<Legislation>>(){}.getType();
                List<Legislation> result = gson.fromJson(body, listType);
                return result != null ? result : new ArrayList<>();
            }
            System.out.println("  Supabase legislation HTTP " + response.code());
            return new ArrayList<>();
        }
    }
    
    // ─── Committees (direct tag_id on committees table) ───
    
    public List<Committee> getCommitteesByTags(List<String> tags) throws IOException {
        if (!isConfigured()) return new ArrayList<>();
        
        List<Integer> tagIds = getTagIdsByNames(tags);
        if (tagIds.isEmpty()) return new ArrayList<>();
        
        String tagIdsParam = tagIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String url = supabaseUrl + "/rest/v1/committees?tag_id=in.(" + tagIdsParam + ")&select=*";
        
        Request request = createRequestBuilder().url(url).get().build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<Committee>>(){}.getType();
                List<Committee> result = gson.fromJson(response.body().string(), listType);
                return result != null ? result : new ArrayList<>();
            }
            return new ArrayList<>();
        }
    }
    
    // ─── Hearings = committee_materials filtered through committees by tag ───
    
    public List<CommitteeMaterial> getHearingsByTags(List<String> tags) throws IOException {
        if (!isConfigured()) return new ArrayList<>();
        
        // Step 1: get committees matching these tags
        List<Committee> committees = getCommitteesByTags(tags);
        if (committees.isEmpty()) {
            // Also try getting hearings linked to legislation with these tags
            return getHearingsViaLegislation(tags);
        }
        
        // Step 2: get committee_materials for those committee IDs
        String comIds = committees.stream()
                .map(c -> String.valueOf(c.getComId()))
                .collect(Collectors.joining(","));
        
        String url = supabaseUrl + "/rest/v1/committee_materials?com_id=in.(" + comIds 
                + ")&order=event_date.desc&limit=20&select=*";
        
        Request request = createRequestBuilder().url(url).get().build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<CommitteeMaterial>>(){}.getType();
                List<CommitteeMaterial> result = gson.fromJson(response.body().string(), listType);
                return result != null ? result : new ArrayList<>();
            }
            return new ArrayList<>();
        }
    }
    
    private List<CommitteeMaterial> getHearingsViaLegislation(List<String> tags) throws IOException {
        // Get legislation IDs for these tags, then find hearings linked to that legislation
        List<Legislation> legislation = getLegislationByTags(tags);
        if (legislation.isEmpty()) return new ArrayList<>();
        
        String legIds = legislation.stream()
                .filter(l -> l.getLegId() != null)
                .map(l -> String.valueOf(l.getLegId()))
                .collect(Collectors.joining(","));
        if (legIds.isEmpty()) return new ArrayList<>();
        
        String url = supabaseUrl + "/rest/v1/committee_materials?leg_id=in.(" + legIds 
                + ")&order=event_date.desc&limit=20&select=*";
        
        Request request = createRequestBuilder().url(url).get().build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<CommitteeMaterial>>(){}.getType();
                List<CommitteeMaterial> result = gson.fromJson(response.body().string(), listType);
                return result != null ? result : new ArrayList<>();
            }
            return new ArrayList<>();
        }
    }
    
    // ─── Nominations (direct tag_id on nominations table) ───
    
    public List<Nomination> getNominationsByTags(List<String> tags) throws IOException {
        if (!isConfigured()) return new ArrayList<>();
        
        List<Integer> tagIds = getTagIdsByNames(tags);
        if (tagIds.isEmpty()) return new ArrayList<>();
        
        String tagIdsParam = tagIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String url = supabaseUrl + "/rest/v1/nominations?tag_id=in.(" + tagIdsParam 
                + ")&order=date_received.desc&limit=20&select=*";
        
        Request request = createRequestBuilder().url(url).get().build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<Nomination>>(){}.getType();
                List<Nomination> result = gson.fromJson(response.body().string(), listType);
                return result != null ? result : new ArrayList<>();
            }
            return new ArrayList<>();
        }
    }
    
    // ─── Auth ───
    
    public AuthResponse login(String email, String password) throws IOException {
        if (!isConfigured()) throw new IOException("Supabase not configured");
        
        String url = supabaseUrl + "/auth/v1/token?grant_type=password";
        String json = gson.toJson(new LoginRequest(email, password));
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = createRequestBuilder().url(url).post(body).build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                AuthResponse authResponse = gson.fromJson(response.body().string(), AuthResponse.class);
                if (authResponse != null && authResponse.getAccessToken() != null) {
                    setAuthToken(authResponse.getAccessToken());
                }
                return authResponse;
            }
            throw new IOException("Login failed: " + response.code());
        }
    }
    
    public AuthResponse register(String email, String password) throws IOException {
        if (!isConfigured()) throw new IOException("Supabase not configured");
        
        String url = supabaseUrl + "/auth/v1/signup";
        String json = gson.toJson(new LoginRequest(email, password));
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = createRequestBuilder().url(url).post(body).build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return gson.fromJson(response.body().string(), AuthResponse.class);
            }
            throw new IOException("Registration failed: " + response.code());
        }
    }
    
    // ─── Helper DTOs for Supabase JSON parsing ───
    
    private static class TagIdRow {
        int tag_id;
    }
}
