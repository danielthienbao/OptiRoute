package com.example.route.dto;

import java.util.List;

public class RouteResponse {
    
    private String status;
    private List<String> optimizedWaypoints;
    private String totalDistance;
    private String totalDuration;
    private List<RouteLeg> legs;
    private String errorMessage;

    public RouteResponse() {}

    public RouteResponse(String status, List<String> optimizedWaypoints, String totalDistance, 
                        String totalDuration, List<RouteLeg> legs) {
        this.status = status;
        this.optimizedWaypoints = optimizedWaypoints;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.legs = legs;
    }

    public static RouteResponse error(String errorMessage) {
        RouteResponse response = new RouteResponse();
        response.setStatus("ERROR");
        response.setErrorMessage(errorMessage);
        return response;
    }

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getOptimizedWaypoints() {
        return optimizedWaypoints;
    }

    public void setOptimizedWaypoints(List<String> optimizedWaypoints) {
        this.optimizedWaypoints = optimizedWaypoints;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public List<RouteLeg> getLegs() {
        return legs;
    }

    public void setLegs(List<RouteLeg> legs) {
        this.legs = legs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Inner class for route legs
    public static class RouteLeg {
        private String startAddress;
        private String endAddress;
        private String distance;
        private String duration;

        public RouteLeg() {}

        public RouteLeg(String startAddress, String endAddress, String distance, String duration) {
            this.startAddress = startAddress;
            this.endAddress = endAddress;
            this.distance = distance;
            this.duration = duration;
        }

        // Getters and setters
        public String getStartAddress() {
            return startAddress;
        }

        public void setStartAddress(String startAddress) {
            this.startAddress = startAddress;
        }

        public String getEndAddress() {
            return endAddress;
        }

        public void setEndAddress(String endAddress) {
            this.endAddress = endAddress;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
    }
} 