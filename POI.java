package com.nexd.tecenttest.CoreEngine;

public class POI {
	private String name;
	private int id;
	private float distance;
	private String description;
	
	public POI(String n, int i, float d, String des) {
		// TODO Auto-generated constructor stub
		this.name = n;
		this.id = i;
		this.distance = d;
		this.description = des;
	}
	
	public String getName() {
		return name;
	}
	
	public int getID() {
		return id;
	}
	
	public float getDistance() {
		return distance;
	}
	
	public String getDescription() {
		return description;
	}
}
