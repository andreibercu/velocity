package com.abercu.velo.birds.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.abercu.velo.birds.model.Page;
import com.abercu.velo.birds.model.Sighting;

import static com.abercu.velo.birds.service.BirdsConstants.*;

public class SightingsService {
	
	public SightingsService() {}
	
	public Page<Sighting> fetchSightingsFromApi(int page, int size) {
		Page<Sighting> result = new Page<>();
		
	    try {
	        URL url = new URL(API_BASE_URL + SIGHTINGS_ENDPOINT + "?page=" + page + "&size=" + size);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod(GET);
	        
	        if (conn.getResponseCode() != 200) {
	        	System.out.println("Error getting sighting list, response: " + conn.getResponseCode() + ", " + conn.getResponseMessage());
	        	return result;
	        }

	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String response = reader.lines().collect(Collectors.joining());
                JSONObject obj = new JSONObject(response);

                result.number = obj.getInt("number");
                result.totalPages = obj.getInt("totalPages");
                result.totalElements = obj.getInt("totalElements");
                result.size = obj.getInt("size");
                
                JSONArray content = obj.getJSONArray("content");
                List<Sighting> sightings = new ArrayList<>();
                
                for (int i = 0; i < content.length(); i++) {
                    JSONObject json = content.getJSONObject(i);
                    
                    String dt = json.optString("datetime");
                    Sighting s = new Sighting(
                    		Long.parseLong(json.optString("id")),
                    		Long.parseLong(json.optString("birdId")),
                    		json.optString("birdName"),
                    		json.optString("birdColor"),
                    		json.optString("location"),
                    		dt.isEmpty() ? null : Instant.parse(dt));

                    sightings.add(s);
                }
                result.content = sightings;
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	
	public Page<Sighting> searchSightings(String term, int page, int size) {
		Page<Sighting> result = new Page<>();
		
	    try {
	        URL url = new URL(API_BASE_URL + SIGHTINGS_ENDPOINT + "/search?page=" + page + "&size=" + size
	        		+ (term == null || term.isBlank() ? "" : "&term=" + URLEncoder.encode(term, "UTF-8")));
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod(GET);
	        
	        if (conn.getResponseCode() != 200) {
	        	System.out.println("Error getting sighting list, response: " + conn.getResponseCode() + ", " + conn.getResponseMessage());
	        	return result;
	        }

	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String response = reader.lines().collect(Collectors.joining());
                JSONObject obj = new JSONObject(response);

                result.number = obj.getInt("number");
                result.totalPages = obj.getInt("totalPages");
                result.totalElements = obj.getInt("totalElements");
                result.size = obj.getInt("size");
                
                JSONArray content = obj.getJSONArray("content");
                List<Sighting> sightings = new ArrayList<>();
                
                for (int i = 0; i < content.length(); i++) {
                    JSONObject json = content.getJSONObject(i);
                    JSONObject birdJson = json.optJSONObject("bird");
                    
                    String dt = json.optString("datetime");
                    Sighting s = new Sighting(
                    		Long.parseLong(json.optString("id")),
                    		Long.parseLong(birdJson.optString("id")),
                    		birdJson.optString("name"),
                    		birdJson.optString("color"),
                    		json.optString("location"),
                    		dt.isEmpty() ? null : Instant.parse(dt));

                    sightings.add(s);
                }
                result.content = sightings;
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}

	public boolean postSighting(Long birdId, String location, Instant datetime) {
		String json = String.format(
            	"{" +
            	"\"birdId\": %d," +
            	"\"location\": \"%s\"," +
            	"\"datetime\": \"%s\"" +
            	"}", birdId, location, datetime.toString());
		
        try {
            URL url = new URL(API_BASE_URL + SIGHTINGS_ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(POST);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int responseCode = conn.getResponseCode();
            return responseCode == 200 || responseCode == 201;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
