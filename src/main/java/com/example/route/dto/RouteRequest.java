package com.example.route.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class RouteRequest {
    
    @NotBlank(message = "Origin is required")
    private String origin;
    
    @NotEmpty(message = "At least one waypoint is required")
    private List<String> waypoints;
    
    private String destination;
    
    private boolean roundTrip = false;

    // Default constructor
    public RouteRequest() {}

    // Constructor with all fields
    public RouteRequest(String origin, List<String> waypoints, String destination, boolean roundTrip) {
        this.origin = origin;
        this.waypoints = waypoints;
        this.destination = destination;
        this.roundTrip = roundTrip;
    }

    // Getters and setters
    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public List<String> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<String> waypoints) {
        this.waypoints = waypoints;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isRoundTrip() {
        return roundTrip;
    }

    public void setRoundTrip(boolean roundTrip) {
        this.roundTrip = roundTrip;
    }

    @Override
    public String toString() {
        return "RouteRequest{" +
                "origin='" + origin + '\'' +
                ", waypoints=" + waypoints +
                ", destination='" + destination + '\'' +
                ", roundTrip=" + roundTrip +
                '}';
    }
} 