package com.abercu.velo.birds.model;

import java.time.Instant;

public class Sighting {

	private Long id;
    private String location;
    private Instant datetime;
    private Long birdId;
    private String birdName;
    private String birdColor;
    
	public Sighting(Long id, Long birdId, String birdName, String birdColor, String location, Instant datetime) {
		this.id = id;
		this.birdId = birdId;
		this.birdName = birdName;
		this.birdColor = birdColor;
		this.location = location;
		this.datetime = datetime;
	}
	
	
    public Long getId() {
    	return id;
    }

    public String getLocation() {
        return location;
    }
    
    public Instant getDatetime() {
    	return datetime;
    }

    public Long getBirdId() {
        return birdId;
    }
    
    public String getBirdName() {
        return birdName;
    }
    
    public String getBirdColor() {
        return birdColor;
    }
}
