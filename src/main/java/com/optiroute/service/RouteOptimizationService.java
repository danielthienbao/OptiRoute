package com.optiroute.service;

import com.optiroute.dto.LocationDto;
import com.optiroute.model.Location;
import com.optiroute.model.Route;
import com.optiroute.model.RouteWaypoint;
import com.optiroute.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteOptimizationService {
    
    private final LocationRepository locationRepository;
    
    /**
     * Optimize route using Nearest Neighbor algorithm (simple TSP approximation)
     */
    public Route optimizeRoute(List<Location> locations, Route.OptimizationStrategy strategy) {
        if (locations == null || locations.size() < 2) {
            throw new IllegalArgumentException("At least 2 locations are required for route optimization");
        }
        
        List<Location> optimizedOrder = switch (strategy) {
            case DISTANCE_BASED -> optimizeByDistance(locations);
            case TIME_BASED -> optimizeByTime(locations);
            case LOAD_BALANCED -> optimizeByLoadBalance(locations);
        };
        
        // Calculate total distance and estimated duration
        double totalDistance = calculateTotalDistance(optimizedOrder);
        int estimatedDuration = (int) (totalDistance * 2); // Assuming 30 km/h average speed
        
        // Create route waypoints
        List<RouteWaypoint> waypoints = createWaypoints(optimizedOrder);
        
        Route route = Route.builder()
                .name("Optimized Route - " + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .description("Route optimized using " + strategy.name() + " strategy")
                .startLocation(optimizedOrder.get(0))
                .endLocation(optimizedOrder.get(optimizedOrder.size() - 1))
                .waypoints(waypoints)
                .totalDistance(totalDistance)
                .estimatedDuration(estimatedDuration)
                .status(Route.RouteStatus.PLANNED)
                .optimizationStrategy(strategy)
                .build();
        
        log.info("Route optimized with {} locations, total distance: {} km, estimated duration: {} minutes", 
                locations.size(), totalDistance, estimatedDuration);
        
        return route;
    }
    
    /**
     * Optimize by minimizing total distance (Nearest Neighbor algorithm)
     */
    private List<Location> optimizeByDistance(List<Location> locations) {
        List<Location> optimized = new ArrayList<>();
        Set<Location> unvisited = new HashSet<>(locations);
        
        // Start with the first location
        Location current = locations.get(0);
        optimized.add(current);
        unvisited.remove(current);
        
        // Find nearest neighbor for each remaining location
        while (!unvisited.isEmpty()) {
            Location nearest = findNearestLocation(current, unvisited);
            optimized.add(nearest);
            unvisited.remove(nearest);
            current = nearest;
        }
        
        return optimized;
    }
    
    /**
     * Optimize by minimizing total time (considering traffic patterns)
     */
    private List<Location> optimizeByTime(List<Location> locations) {
        // For now, use distance-based optimization with time multiplier
        // In a real implementation, this would consider traffic data, road conditions, etc.
        return optimizeByDistance(locations);
    }
    
    /**
     * Optimize by load balancing (distribute stops evenly)
     */
    private List<Location> optimizeByLoadBalance(List<Location> locations) {
        // For now, use distance-based optimization
        // In a real implementation, this would consider vehicle capacity, package sizes, etc.
        return optimizeByDistance(locations);
    }
    
    /**
     * Find the nearest location from a set of unvisited locations
     */
    private Location findNearestLocation(Location current, Set<Location> unvisited) {
        Location nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Location location : unvisited) {
            double distance = calculateDistance(current, location);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = location;
            }
        }
        
        return nearest;
    }
    
    /**
     * Calculate distance between two locations using PostGIS
     */
    private double calculateDistance(Location from, Location to) {
        if (from.getCoordinates() == null || to.getCoordinates() == null) {
            // Fallback to rough estimation if coordinates are not available
            return 0.0;
        }
        
        Double distance = locationRepository.calculateDistanceBetweenLocations(from.getId(), to.getId());
        return distance != null ? distance : 0.0;
    }
    
    /**
     * Calculate total distance for the entire route
     */
    private double calculateTotalDistance(List<Location> locations) {
        double totalDistance = 0.0;
        
        for (int i = 0; i < locations.size() - 1; i++) {
            totalDistance += calculateDistance(locations.get(i), locations.get(i + 1));
        }
        
        return totalDistance;
    }
    
    /**
     * Create waypoints for the optimized route
     */
    private List<RouteWaypoint> createWaypoints(List<Location> locations) {
        List<RouteWaypoint> waypoints = new ArrayList<>();
        
        for (int i = 0; i < locations.size(); i++) {
            Location location = locations.get(i);
            
            RouteWaypoint waypoint = RouteWaypoint.builder()
                    .location(location)
                    .waypointOrder(i + 1)
                    .status(RouteWaypoint.WaypointStatus.PENDING)
                    .build();
            
            waypoints.add(waypoint);
        }
        
        return waypoints;
    }
    
    /**
     * Optimize multiple routes for multiple vehicles (Vehicle Routing Problem)
     */
    public List<Route> optimizeMultipleRoutes(List<Location> locations, int numberOfVehicles, Route.OptimizationStrategy strategy) {
        if (locations.size() < numberOfVehicles) {
            throw new IllegalArgumentException("Number of locations must be greater than or equal to number of vehicles");
        }
        
        // Simple clustering: divide locations into equal groups
        List<List<Location>> clusters = clusterLocations(locations, numberOfVehicles);
        
        List<Route> routes = new ArrayList<>();
        for (int i = 0; i < clusters.size(); i++) {
            List<Location> cluster = clusters.get(i);
            if (cluster.size() >= 2) {
                Route route = optimizeRoute(cluster, strategy);
                route.setName("Vehicle " + (i + 1) + " Route");
                routes.add(route);
            }
        }
        
        return routes;
    }
    
    /**
     * Simple clustering algorithm to divide locations among vehicles
     */
    private List<List<Location>> clusterLocations(List<Location> locations, int numberOfClusters) {
        List<List<Location>> clusters = new ArrayList<>();
        for (int i = 0; i < numberOfClusters; i++) {
            clusters.add(new ArrayList<>());
        }
        
        // Simple round-robin distribution
        for (int i = 0; i < locations.size(); i++) {
            clusters.get(i % numberOfClusters).add(locations.get(i));
        }
        
        return clusters;
    }
} 