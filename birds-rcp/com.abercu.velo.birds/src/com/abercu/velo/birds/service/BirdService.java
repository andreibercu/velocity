package com.abercu.velo.birds.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.abercu.velo.birds.model.Bird;
import com.abercu.velo.birds.model.Page;

import static com.abercu.velo.birds.service.BirdsConstants.*;

public class BirdService {

	// todo abercu - make sure json*.jar lib is added to the exported bundle - add it to the .product file?
	public BirdService() {}

	public Page<Bird> fetchBirdsFromApi(int page, int size) {
	    Page<Bird> result = new Page<>();
	    
	    try {
	        URL url = new URL(API_BASE_URL + BIRDS_ENDPOINT + "?page=" + page + "&size=" + size);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod(GET);
	        
	        if (connection.getResponseCode() != 200) {
	        	System.out.println("Error getting bird list, response: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
	        	return result;
	        }

	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String response = reader.lines().collect(Collectors.joining());
                JSONObject obj = new JSONObject(response);
                
                result.number = obj.getInt("number");
                result.totalPages = obj.getInt("totalPages");
                result.totalElements = obj.getInt("totalElements");
                result.size = obj.getInt("size");
                
                JSONArray content = obj.getJSONArray("content");
                List<Bird> birds = new ArrayList<>();

                for (int i = 0; i < content.length(); i++) {
                    JSONObject json = content.getJSONObject(i);

                    Bird bird = new Bird(
                    		json.optLong("id"),
                    		json.optString("name"),
                    		json.optString("color"),
                    		json.isNull("weight") ? null : json.optDouble("weight"),
                    		json.isNull("height") ? null : json.optDouble("height"));
                    birds.add(bird);
                }
                result.content = birds;
            }
	    } catch (Exception e) {
	    	System.out.println("Error getting bird list: " + e.getMessage());
//	        e.printStackTrace();
	    }

	    return result;
	}

    public boolean postBird(String name, String color, Double weight, Double height) {
    	String json = String.format("" +
            	"{" +
            	"\"name\": \"%s\"," +
            	"\"color\": \"%s\"," +
            	"\"weight\": %s," +
            	"\"height\": %s" +
            	"}",
            	name, 
            	color,
                weight != null ? weight.toString() : "null",
                height != null ? height.toString() : "null"
            );
    	
        try {
            URL url = new URL(API_BASE_URL + BIRDS_ENDPOINT);
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
