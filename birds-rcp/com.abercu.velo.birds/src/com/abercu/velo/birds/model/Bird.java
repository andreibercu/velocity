package com.abercu.velo.birds.model;

public class Bird {

	private Long id;
    private String name;
    private String color;
    private Double weight;
    private Double height;
    
    public Bird() {}

    public Bird(Long id, String name, String color, Double weight, Double height) {
        this.id = id;
    	this.name = name;
        this.color = color;
        this.weight = weight;
        this.height = height;
    }
    
    public Long getId() {
    	return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getHeight() {
        return height;
    }
}
