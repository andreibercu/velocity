package com.abercu.velo.birds.service;

public class BirdsConstants {
	
	public static final String API_BASE_URL = getApiBaseUrl();
	public static final String BIRDS_ENDPOINT = "/birds";
	public static final String SIGHTINGS_ENDPOINT = "/sightings";
	public static final String GET = "GET";
	public static final String POST = "POST";

	public BirdsConstants() {}
	
	private static String getApiBaseUrl() {
        String envUrl = System.getenv("API_BASE_URL");

        return (envUrl != null && !envUrl.isBlank())
                ? envUrl
                : "http://localhost:8080";
    }

}
