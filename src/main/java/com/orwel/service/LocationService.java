package com.orwel.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.orwel.config.AppConfig;
import com.orwel.model.LocationInfo;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class LocationService {
    private static final LocationService instance = new LocationService();
    private final OkHttpClient client;
    private final Gson gson;
    
    private LocationService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }
    
    public static LocationService getInstance() {
        return instance;
    }
    
    /**
     * Geocode an address to get location coordinates and details
     * Supports multiple providers: Google Maps, OpenStreetMap (Nominatim)
     */
    public LocationInfo geocodeAddress(String address) throws IOException {
        if (AppConfig.LOCATION_API_PROVIDER.equalsIgnoreCase("google")) {
            return geocodeWithGoogle(address);
        } else {
            return geocodeWithNominatim(address);
        }
    }
    
    /**
     * Reverse geocode coordinates to get address
     */
    public LocationInfo reverseGeocode(double latitude, double longitude) throws IOException {
        if (AppConfig.LOCATION_API_PROVIDER.equalsIgnoreCase("google")) {
            return reverseGeocodeWithGoogle(latitude, longitude);
        } else {
            return reverseGeocodeWithNominatim(latitude, longitude);
        }
    }
    
    private LocationInfo geocodeWithGoogle(String address) throws IOException {
        if (AppConfig.LOCATION_API_KEY == null || AppConfig.LOCATION_API_KEY.isEmpty()) {
            throw new IOException("Google Maps API key not configured");
        }
        
        HttpUrl url = HttpUrl.parse("https://maps.googleapis.com/maps/api/geocode/json").newBuilder()
                .addQueryParameter("address", address)
                .addQueryParameter("key", AppConfig.LOCATION_API_KEY)
                .build();
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                
                if (jsonResponse.has("results") && jsonResponse.getAsJsonArray("results").size() > 0) {
                    JsonObject result = jsonResponse.getAsJsonArray("results").get(0).getAsJsonObject();
                    JsonObject location = result.getAsJsonObject("geometry").getAsJsonObject("location");
                    
                    LocationInfo locationInfo = new LocationInfo();
                    locationInfo.setLatitude(location.get("lat").getAsDouble());
                    locationInfo.setLongitude(location.get("lng").getAsDouble());
                    locationInfo.setFormattedAddress(result.get("formatted_address").getAsString());
                    
                    // Extract additional details
                    if (result.has("address_components")) {
                        var components = result.getAsJsonArray("address_components");
                        for (var component : components) {
                            JsonObject comp = component.getAsJsonObject();
                            var types = comp.getAsJsonArray("types");
                            
                            if (types.toString().contains("country")) {
                                locationInfo.setCountryCode(comp.get("short_name").getAsString());
                            }
                            if (types.toString().contains("administrative_area_level_1")) {
                                locationInfo.setRegion(comp.get("long_name").getAsString());
                            }
                            if (types.toString().contains("locality")) {
                                locationInfo.setCity(comp.get("long_name").getAsString());
                            }
                        }
                    }
                    
                    return locationInfo;
                }
            }
            throw new IOException("Geocoding failed: " + response.code());
        }
    }
    
    private LocationInfo reverseGeocodeWithGoogle(double latitude, double longitude) throws IOException {
        if (AppConfig.LOCATION_API_KEY == null || AppConfig.LOCATION_API_KEY.isEmpty()) {
            throw new IOException("Google Maps API key not configured");
        }
        
        String latlng = latitude + "," + longitude;
        HttpUrl url = HttpUrl.parse("https://maps.googleapis.com/maps/api/geocode/json").newBuilder()
                .addQueryParameter("latlng", latlng)
                .addQueryParameter("key", AppConfig.LOCATION_API_KEY)
                .build();
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                
                if (jsonResponse.has("results") && jsonResponse.getAsJsonArray("results").size() > 0) {
                    JsonObject result = jsonResponse.getAsJsonArray("results").get(0).getAsJsonObject();
                    
                    LocationInfo locationInfo = new LocationInfo();
                    locationInfo.setLatitude(latitude);
                    locationInfo.setLongitude(longitude);
                    locationInfo.setFormattedAddress(result.get("formatted_address").getAsString());
                    
                    // Extract additional details
                    if (result.has("address_components")) {
                        var components = result.getAsJsonArray("address_components");
                        for (var component : components) {
                            JsonObject comp = component.getAsJsonObject();
                            var types = comp.getAsJsonArray("types");
                            
                            if (types.toString().contains("country")) {
                                locationInfo.setCountryCode(comp.get("short_name").getAsString());
                            }
                            if (types.toString().contains("administrative_area_level_1")) {
                                locationInfo.setRegion(comp.get("long_name").getAsString());
                            }
                            if (types.toString().contains("locality")) {
                                locationInfo.setCity(comp.get("long_name").getAsString());
                            }
                        }
                    }
                    
                    return locationInfo;
                }
            }
            throw new IOException("Reverse geocoding failed: " + response.code());
        }
    }
    
    private LocationInfo geocodeWithNominatim(String address) throws IOException {
        // OpenStreetMap Nominatim API (free, no key required)
        HttpUrl url = HttpUrl.parse("https://nominatim.openstreetmap.org/search").newBuilder()
                .addQueryParameter("q", address)
                .addQueryParameter("format", "json")
                .addQueryParameter("limit", "1")
                .build();
        
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Orwel-Application/1.0")
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                if (responseBody.startsWith("[") && responseBody.length() > 2) {
                    JsonObject result = gson.fromJson(responseBody, com.google.gson.JsonArray.class)
                            .get(0).getAsJsonObject();
                    
                    LocationInfo locationInfo = new LocationInfo();
                    locationInfo.setLatitude(Double.parseDouble(result.get("lat").getAsString()));
                    locationInfo.setLongitude(Double.parseDouble(result.get("lon").getAsString()));
                    locationInfo.setFormattedAddress(result.get("display_name").getAsString());
                    
                    if (result.has("address")) {
                        JsonObject addressObj = result.getAsJsonObject("address");
                        if (addressObj.has("country_code")) {
                            locationInfo.setCountryCode(addressObj.get("country_code").getAsString().toUpperCase());
                        }
                        if (addressObj.has("state")) {
                            locationInfo.setRegion(addressObj.get("state").getAsString());
                        }
                        if (addressObj.has("city") || addressObj.has("town")) {
                            locationInfo.setCity(addressObj.has("city") ? 
                                addressObj.get("city").getAsString() : 
                                addressObj.get("town").getAsString());
                        }
                    }
                    
                    return locationInfo;
                }
            }
            throw new IOException("Geocoding failed: " + response.code());
        }
    }
    
    private LocationInfo reverseGeocodeWithNominatim(double latitude, double longitude) throws IOException {
        HttpUrl url = HttpUrl.parse("https://nominatim.openstreetmap.org/reverse").newBuilder()
                .addQueryParameter("lat", String.valueOf(latitude))
                .addQueryParameter("lon", String.valueOf(longitude))
                .addQueryParameter("format", "json")
                .build();
        
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Orwel-Application/1.0")
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonObject result = gson.fromJson(response.body().string(), JsonObject.class);
                
                LocationInfo locationInfo = new LocationInfo();
                locationInfo.setLatitude(latitude);
                locationInfo.setLongitude(longitude);
                
                if (result.has("display_name")) {
                    locationInfo.setFormattedAddress(result.get("display_name").getAsString());
                }
                
                if (result.has("address")) {
                    JsonObject addressObj = result.getAsJsonObject("address");
                    if (addressObj.has("country_code")) {
                        locationInfo.setCountryCode(addressObj.get("country_code").getAsString().toUpperCase());
                    }
                    if (addressObj.has("state")) {
                        locationInfo.setRegion(addressObj.get("state").getAsString());
                    }
                    if (addressObj.has("city") || addressObj.has("town")) {
                        locationInfo.setCity(addressObj.has("city") ? 
                            addressObj.get("city").getAsString() : 
                            addressObj.get("town").getAsString());
                    }
                }
                
                return locationInfo;
            }
            throw new IOException("Reverse geocoding failed: " + response.code());
        }
    }
}
