package com.tts.transitapp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.tts.transitapp.model.Bus;
import com.tts.transitapp.model.BusComparator;
import com.tts.transitapp.model.BusRequest;
import com.tts.transitapp.model.DistanceResponse;
import com.tts.transitapp.model.GeocodingResponse;
import com.tts.transitapp.model.Location;

@Service
public class TransitService 
{
    @Value("${transit_url}")
    public String transitUrl;
	
    @Value("${geocoding_url}")
    public String geocodingUrl;
	
    @Value("${distance_url}")
    public String distanceUrl;
	
    @Value("${google_api_key}")
    public String googleApiKey;
    
    //Queries MARTA to get all buses
    private List<Bus> getBuses()
    {
        RestTemplate restTemplate = new RestTemplate();
        Bus[] buses = restTemplate.getForObject(transitUrl, Bus[].class);
        return Arrays.asList(buses);
    }
    
    //Queries Google Geocoding API to get the latitude and longitude of a place
    private Location getCoordinates(String description) 
    {
        description = description.replace(" ", "+");
        String url = geocodingUrl + description + "+GA&key=" + googleApiKey;
        RestTemplate restTemplate = new RestTemplate();
        GeocodingResponse response = restTemplate.getForObject(url, GeocodingResponse.class);
        return response.results.get(0).geometry.location;
    }
    
    //Queries Google Distance Matrix API to get the distance between two places
    private double getDistance(Location origin, Location destination) 
    {
        String url = distanceUrl + "origins=" + origin.lat + "," + origin.lng 
        + "&destinations=" + destination.lat + "," + destination.lng 
        + "&key=" + googleApiKey;
        RestTemplate restTemplate = new RestTemplate();
        DistanceResponse response = restTemplate.getForObject(url, DistanceResponse.class);
        
        //The constant converts meters to miles
        return response.rows.get(0).elements.get(0).distance.value * 0.000621371;
        
    }
    
    // Get all the nearby buses, given the location in request
    public List<Bus> getNearbyBuses(BusRequest request)
    {
    	//Step 1: get ALL the buses from MARTA
    	List<Bus> allBuses = this.getBuses(); 
    	
    	//Step 2: Use the geocoding API to lookup the location (lat, lng) of the request.
    	Location personLocation = this.getCoordinates(request.address + " " + request.city);
    	
    	//Initialize nearbyBuses to empty arraylist
    	List<Bus> nearbyBuses = new ArrayList<>(); 
    	
    	//Step 3: loop through all buses to find nearby buses only and add them to nearbyBuses
    	for(Bus bus : allBuses)
    	{
    		Location busLocation = new Location(); 
    		busLocation.lat = bus.LATITUDE; 
    		busLocation.lng = bus.LONGITUDE; 
    		
    		//we are going to perform a fuzzy distance comparison between each bus and
        	//user to prefilter out buses that are too far away.
    		double latDistance = Double.parseDouble(busLocation.lat) - Double.parseDouble(personLocation.lat); 
    		double lngDistance = Double.parseDouble(busLocation.lng) - Double.parseDouble(personLocation.lng); 
    		if (Math.abs(latDistance) <= 0.02 && Math.abs(lngDistance) <= 0.02)
    		{
    			double distance = getDistance(busLocation, personLocation);
    			if (distance <= 1) 
    			{
    			    bus.distance = (double) Math.round(distance * 100) / 100;
    			    nearbyBuses.add(bus);
    			}
    		}
    	}
    	//Step 4: Sort buses
    	Collections.sort(nearbyBuses, new BusComparator());
    	return nearbyBuses;
    }
    
    public Location getPersonLocation(BusRequest request) {

    	Location personLocation = this.getCoordinates(request.address + " " + request.city);
    	return personLocation;
   }
}
