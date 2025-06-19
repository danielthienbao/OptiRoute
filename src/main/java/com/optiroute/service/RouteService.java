package com.optiroute.service;

import com.optiroute.dto.RouteDto;
import com.optiroute.dto.RouteOptimizationRequest;
import com.optiroute.dto.RouteWaypointDto;
import com.optiroute.dto.UserDto;
import com.optiroute.exception.ResourceNotFoundException;
import com.optiroute.model.*;
import com.optiroute.repository.RouteRepository;
import com.optiroute.repository.UserRepository;
import com.optiroute.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RouteService {

    private final RouteRepository routeRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RouteOptimizationService routeOptimizationService;

    /**
     * Get all routes with optional filtering
     */
    public Page<RouteDto> getAllRoutes(RouteStatus status, Long driverId, Pageable pageable) {
        Page<Route> routes;
        
        if (status != null && driverId != null) {
            routes = routeRepository.findByStatusAndDriverId(status, driverId, pageable);
        } else if (status != null) {
            routes = routeRepository.findByStatus(status, pageable);
        } else if (driverId != null) {
            routes = routeRepository.findByDriverId(driverId, pageable);
        } else {
            routes = routeRepository.findAll(pageable);
        }
        
        return routes.map(this::convertToDto);
    }

    /**
     * Get route by ID
     */
    public RouteDto getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        return convertToDto(route);
    }

    /**
     * Create new route
     */
    public RouteDto createRoute(RouteDto routeDto) {
        Route route = convertToEntity(routeDto);
        route.setCreatedAt(LocalDateTime.now());
        route.setUpdatedAt(LocalDateTime.now());
        route.setActive(true);
        
        Route savedRoute = routeRepository.save(route);
        log.info("Created new route with ID: {}", savedRoute.getId());
        
        return convertToDto(savedRoute);
    }

    /**
     * Update existing route
     */
    public RouteDto updateRoute(Long id, RouteDto routeDto) {
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        
        // Update fields
        existingRoute.setName(routeDto.getName());
        existingRoute.setDescription(routeDto.getDescription());
        existingRoute.setStatus(routeDto.getStatus());
        existingRoute.setOptimizationStrategy(routeDto.getOptimizationStrategy());
        existingRoute.setTotalDistance(routeDto.getTotalDistance());
        existingRoute.setEstimatedDuration(routeDto.getEstimatedTime());
        existingRoute.setStartTime(routeDto.getStartTime());
        existingRoute.setEndTime(routeDto.getEndTime());
        existingRoute.setPriority(routeDto.getPriority());
        existingRoute.setNotes(routeDto.getNotes());
        existingRoute.setTags(routeDto.getTags());
        existingRoute.setUpdatedAt(LocalDateTime.now());
        
        Route updatedRoute = routeRepository.save(existingRoute);
        log.info("Updated route with ID: {}", updatedRoute.getId());
        
        return convertToDto(updatedRoute);
    }

    /**
     * Delete route
     */
    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Route not found with id: " + id);
        }
        
        routeRepository.deleteById(id);
        log.info("Deleted route with ID: {}", id);
    }

    /**
     * Optimize route
     */
    public RouteDto optimizeRoute(RouteOptimizationRequest request) {
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + request.getRouteId()));
        
        // Get locations for optimization
        List<Location> locations = locationRepository.findAllById(request.getLocationIds());
        
        // Optimize route using the optimization service
        Route optimizedRoute = routeOptimizationService.optimizeRoute(locations, request.getStrategy());
        
        // Update the existing route with optimized data
        route.setWaypoints(optimizedRoute.getWaypoints());
        route.setTotalDistance(optimizedRoute.getTotalDistance());
        route.setEstimatedDuration(optimizedRoute.getEstimatedDuration());
        route.setOptimizationStrategy(request.getStrategy());
        route.setUpdatedAt(LocalDateTime.now());
        
        Route savedRoute = routeRepository.save(route);
        log.info("Optimized route with ID: {}", savedRoute.getId());
        
        return convertToDto(savedRoute);
    }

    /**
     * Assign route to driver
     */
    public RouteDto assignRouteToDriver(Long routeId, Long driverId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + routeId));
        
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));
        
        // Verify driver role
        if (!driver.getRole().equals(User.Role.DRIVER)) {
            throw new IllegalArgumentException("User with ID " + driverId + " is not a driver");
        }
        
        route.setDriver(driver);
        route.setUpdatedAt(LocalDateTime.now());
        
        Route savedRoute = routeRepository.save(route);
        log.info("Assigned route {} to driver {}", routeId, driverId);
        
        return convertToDto(savedRoute);
    }

    /**
     * Update route status
     */
    public RouteDto updateRouteStatus(Long id, RouteStatus status) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        
        route.setStatus(status);
        route.setUpdatedAt(LocalDateTime.now());
        
        // Update start/end times based on status
        if (status == RouteStatus.IN_PROGRESS && route.getStartTime() == null) {
            route.setStartTime(LocalDateTime.now());
        } else if (status == RouteStatus.COMPLETED && route.getEndTime() == null) {
            route.setEndTime(LocalDateTime.now());
        }
        
        Route savedRoute = routeRepository.save(route);
        log.info("Updated route {} status to {}", id, status);
        
        return convertToDto(savedRoute);
    }

    /**
     * Get routes by driver
     */
    public List<RouteDto> getRoutesByDriver(Long driverId) {
        List<Route> routes = routeRepository.findByDriverId(driverId);
        return routes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get routes by status
     */
    public List<RouteDto> getRoutesByStatus(RouteStatus status) {
        List<Route> routes = routeRepository.findByStatus(status);
        return routes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Batch optimize routes
     */
    public List<RouteDto> batchOptimizeRoutes(List<Long> routeIds, OptimizationStrategy strategy) {
        List<Route> routes = routeRepository.findAllById(routeIds);
        List<RouteDto> optimizedRoutes = new ArrayList<>();
        
        for (Route route : routes) {
            try {
                // Get all locations from route waypoints
                List<Location> locations = route.getWaypoints().stream()
                        .map(RouteWaypoint::getLocation)
                        .collect(Collectors.toList());
                
                // Optimize route
                Route optimizedRoute = routeOptimizationService.optimizeRoute(locations, strategy);
                
                // Update route with optimized data
                route.setWaypoints(optimizedRoute.getWaypoints());
                route.setTotalDistance(optimizedRoute.getTotalDistance());
                route.setEstimatedDuration(optimizedRoute.getEstimatedDuration());
                route.setOptimizationStrategy(strategy);
                route.setUpdatedAt(LocalDateTime.now());
                
                Route savedRoute = routeRepository.save(route);
                optimizedRoutes.add(convertToDto(savedRoute));
                
            } catch (Exception e) {
                log.error("Failed to optimize route {}: {}", route.getId(), e.getMessage());
            }
        }
        
        log.info("Batch optimized {} routes", optimizedRoutes.size());
        return optimizedRoutes;
    }

    /**
     * Get route with waypoints
     */
    public RouteDto getRouteWithWaypoints(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        
        return convertToDto(route);
    }

    /**
     * Reorder waypoints
     */
    public RouteDto reorderWaypoints(Long routeId, List<Long> waypointIds) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + routeId));
        
        // Reorder waypoints based on the provided order
        List<RouteWaypoint> waypoints = route.getWaypoints();
        List<RouteWaypoint> reorderedWaypoints = new ArrayList<>();
        
        for (int i = 0; i < waypointIds.size(); i++) {
            Long waypointId = waypointIds.get(i);
            RouteWaypoint waypoint = waypoints.stream()
                    .filter(w -> w.getId().equals(waypointId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Waypoint not found with id: " + waypointId));
            
            waypoint.setWaypointOrder(i + 1);
            reorderedWaypoints.add(waypoint);
        }
        
        route.setWaypoints(reorderedWaypoints);
        route.setUpdatedAt(LocalDateTime.now());
        
        Route savedRoute = routeRepository.save(route);
        log.info("Reordered waypoints for route {}", routeId);
        
        return convertToDto(savedRoute);
    }

    /**
     * Convert Route entity to DTO
     */
    private RouteDto convertToDto(Route route) {
        return RouteDto.builder()
                .id(route.getId())
                .name(route.getName())
                .description(route.getDescription())
                .status(route.getStatus())
                .driverId(route.getDriver() != null ? route.getDriver().getId() : null)
                .driver(route.getDriver() != null ? convertUserToDto(route.getDriver()) : null)
                .optimizationStrategy(route.getOptimizationStrategy())
                .totalDistance(route.getTotalDistance())
                .estimatedTime(route.getEstimatedDuration())
                .waypoints(route.getWaypoints() != null ? 
                        route.getWaypoints().stream()
                                .map(this::convertWaypointToDto)
                                .collect(Collectors.toList()) : null)
                .startLocation(route.getStartLocation() != null ? 
                        convertLocationToDto(route.getStartLocation()) : null)
                .endLocation(route.getEndLocation() != null ? 
                        convertLocationToDto(route.getEndLocation()) : null)
                .createdAt(route.getCreatedAt())
                .updatedAt(route.getUpdatedAt())
                .startTime(route.getStartTime())
                .endTime(route.getEndTime())
                .priority(route.getPriority())
                .notes(route.getNotes())
                .active(route.getActive())
                .tags(route.getTags())
                .build();
    }

    /**
     * Convert DTO to Route entity
     */
    private Route convertToEntity(RouteDto routeDto) {
        return Route.builder()
                .name(routeDto.getName())
                .description(routeDto.getDescription())
                .status(routeDto.getStatus())
                .driver(routeDto.getDriverId() != null ? 
                        userRepository.findById(routeDto.getDriverId()).orElse(null) : null)
                .optimizationStrategy(routeDto.getOptimizationStrategy())
                .totalDistance(routeDto.getTotalDistance())
                .estimatedDuration(routeDto.getEstimatedTime())
                .startLocation(routeDto.getStartLocation() != null ? 
                        locationRepository.findById(routeDto.getStartLocation().getId()).orElse(null) : null)
                .endLocation(routeDto.getEndLocation() != null ? 
                        locationRepository.findById(routeDto.getEndLocation().getId()).orElse(null) : null)
                .startTime(routeDto.getStartTime())
                .endTime(routeDto.getEndTime())
                .priority(routeDto.getPriority())
                .notes(routeDto.getNotes())
                .active(routeDto.getActive())
                .tags(routeDto.getTags())
                .build();
    }

    /**
     * Convert RouteWaypoint to DTO
     */
    private RouteWaypointDto convertWaypointToDto(RouteWaypoint waypoint) {
        return RouteWaypointDto.builder()
                .id(waypoint.getId())
                .routeId(waypoint.getRoute() != null ? waypoint.getRoute().getId() : null)
                .location(convertLocationToDto(waypoint.getLocation()))
                .sequence(waypoint.getWaypointOrder())
                .estimatedArrival(waypoint.getEstimatedArrival())
                .actualArrival(waypoint.getActualArrival())
                .estimatedDeparture(waypoint.getEstimatedDeparture())
                .actualDeparture(waypoint.getActualDeparture())
                .distanceFromPrevious(waypoint.getDistanceFromPrevious())
                .timeFromPrevious(waypoint.getTimeFromPrevious())
                .status(waypoint.getStatus().name())
                .notes(waypoint.getNotes())
                .completed(waypoint.getCompleted())
                .priority(waypoint.getPriority())
                .build();
    }

    /**
     * Convert User to DTO
     */
    private UserDto convertUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .active(user.getActive())
                .build();
    }

    /**
     * Convert Location to DTO
     */
    private LocationDto convertLocationToDto(Location location) {
        return LocationDto.builder()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .city(location.getCity())
                .state(location.getState())
                .zipCode(location.getZipCode())
                .country(location.getCountry())
                .latitude(location.getCoordinates() != null ? location.getCoordinates().getY() : null)
                .longitude(location.getCoordinates() != null ? location.getCoordinates().getX() : null)
                .type(location.getType())
                .build();
    }
} 