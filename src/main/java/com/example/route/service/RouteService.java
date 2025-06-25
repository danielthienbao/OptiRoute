package com.example.route.service;

import com.example.route.dto.RouteRequest;
import com.example.route.dto.RouteResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class RouteService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public RouteService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public RouteResponse getOptimizedRoute(RouteRequest request) {
        try {
            // Build the Google Directions API URL
            String url = buildDirectionsUrl(request);
            
            // Make the API call
            String response = restTemplate.getForObject(url, String.class);
            
            // Parse the response
            return parseDirectionsResponse(response, request);
            
        } catch (Exception e) {
            return RouteResponse.error("Error getting route: " + e.getMessage());
        }
    }

    private String buildDirectionsUrl(RouteRequest request) {
        StringBuilder url = new StringBuilder();
        url.append("https://maps.googleapis.com/maps/api/directions/json?");
        
        // Origin
        url.append("origin=").append(encodeParameter(request.getOrigin()));
        
        // Destination
        if (request.getDestination() != null && !request.getDestination().trim().isEmpty()) {
            url.append("&destination=").append(encodeParameter(request.getDestination()));
        } else {
            // If no destination, use the last waypoint as destination
            if (!request.getWaypoints().isEmpty()) {
                url.append("&destination=").append(encodeParameter(request.getWaypoints().get(request.getWaypoints().size() - 1)));
            }
        }
        
        // Waypoints with optimization
        if (!request.getWaypoints().isEmpty()) {
            url.append("&waypoints=optimize:true");
            for (String waypoint : request.getWaypoints()) {
                url.append("|").append(encodeParameter(waypoint));
            }
        }
        
        // API key
        url.append("&key=").append(apiKey);
        
        return url.toString();
    }

    private String encodeParameter(String parameter) {
        return URLEncoder.encode(parameter, StandardCharsets.UTF_8);
    }

    private RouteResponse parseDirectionsResponse(String response, RouteRequest request) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String status = root.get("status").asText();
            
            if (!"OK".equals(status)) {
                return RouteResponse.error("Google Maps API error: " + status);
            }
            
            JsonNode routes = root.get("routes");
            if (routes.isEmpty()) {
                return RouteResponse.error("No routes found");
            }
            
            JsonNode route = routes.get(0);
            JsonNode legs = route.get("legs");
            
            // Extract optimized waypoint order
            List<String> optimizedWaypoints = new ArrayList<>();
            JsonNode waypointOrder = route.get("waypoint_order");
            if (waypointOrder != null) {
                for (JsonNode index : waypointOrder) {
                    int waypointIndex = index.asInt();
                    if (waypointIndex < request.getWaypoints().size()) {
                        optimizedWaypoints.add(request.getWaypoints().get(waypointIndex));
                    }
                }
            }
            
            // Calculate total distance and duration
            long totalDistance = 0;
            long totalDuration = 0;
            List<RouteResponse.RouteLeg> routeLegs = new ArrayList<>();
            
            for (JsonNode leg : legs) {
                JsonNode distance = leg.get("distance");
                JsonNode duration = leg.get("duration");
                
                totalDistance += distance.get("value").asLong();
                totalDuration += duration.get("value").asLong();
                
                routeLegs.add(new RouteResponse.RouteLeg(
                    leg.get("start_address").asText(),
                    leg.get("end_address").asText(),
                    distance.get("text").asText(),
                    duration.get("text").asText()
                ));
            }
            
            // Convert to human-readable format
            String totalDistanceText = formatDistance(totalDistance);
            String totalDurationText = formatDuration(totalDuration);
            
            return new RouteResponse("OK", optimizedWaypoints, totalDistanceText, totalDurationText, routeLegs);
            
        } catch (Exception e) {
            return RouteResponse.error("Error parsing response: " + e.getMessage());
        }
    }

    private String formatDistance(long meters) {
        if (meters < 1000) {
            return meters + " m";
        } else {
            double km = meters / 1000.0;
            return String.format("%.1f km", km);
        }
    }

    private String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        
        if (hours > 0) {
            return String.format("%d hr %d min", hours, minutes);
        } else {
            return String.format("%d min", minutes);
        }
    }
} 