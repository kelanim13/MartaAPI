package com.tts.transitapp.model;

public class Geocoding 
{
	public Geometry geometry; 
	
	public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public String toString() {
        return "Geocoding [geometry=" + geometry + "]";
    }

}
