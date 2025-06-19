package com.optiroute.controller;

import com.optiroute.dto.RouteDto;
import com.optiroute.dto.RouteOptimizationRequest;
import com.optiroute.dto.RouteAssignmentRequest;
import com.optiroute.model.Route;
import com.optiroute.model.RouteStatus;
import com.optiroute.model.OptimizationStrategy;
import com.optiroute.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@Tag(name = "Route Management", description = "APIs for managing routes and route optimization")
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    @Operation(summary = "Get all routes", description = "Retrieve all routes with pagination and filtering")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<Page<RouteDto>> getAllRoutes(
            @Parameter(description = "Route status filter") @RequestParam(required = false) RouteStatus status,
            @Parameter(description = "Driver ID filter") @RequestParam(required = false) Long driverId,
            @Parameter(description = "Pageable parameters") Pageable pageable) {
        
        Page<RouteDto> routes = routeService.getAllRoutes(status, driverId, pageable);
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get route by ID", description = "Retrieve a specific route by its ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<RouteDto> getRouteById(
            @Parameter(description = "Route ID") @PathVariable Long id) {
        
        RouteDto route = routeService.getRouteById(id);
        return ResponseEntity.ok(route);
    }

    @PostMapping
    @Operation(summary = "Create new route", description = "Create a new route with waypoints")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteDto> createRoute(
            @Parameter(description = "Route data") @Valid @RequestBody RouteDto routeDto) {
        
        RouteDto createdRoute = routeService.createRoute(routeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoute);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update route", description = "Update an existing route")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteDto> updateRoute(
            @Parameter(description = "Route ID") @PathVariable Long id,
            @Parameter(description = "Updated route data") @Valid @RequestBody RouteDto routeDto) {
        
        RouteDto updatedRoute = routeService.updateRoute(id, routeDto);
        return ResponseEntity.ok(updatedRoute);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete route", description = "Delete a route by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoute(
            @Parameter(description = "Route ID") @PathVariable Long id) {
        
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/optimize")
    @Operation(summary = "Optimize route", description = "Optimize a route using specified strategy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteDto> optimizeRoute(
            @Parameter(description = "Optimization request") @Valid @RequestBody RouteOptimizationRequest request) {
        
        RouteDto optimizedRoute = routeService.optimizeRoute(request);
        return ResponseEntity.ok(optimizedRoute);
    }

    @PostMapping("/{id}/assign")
    @Operation(summary = "Assign route to driver", description = "Assign a route to a specific driver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteDto> assignRouteToDriver(
            @Parameter(description = "Route ID") @PathVariable Long id,
            @Parameter(description = "Assignment request") @Valid @RequestBody RouteAssignmentRequest request) {
        
        RouteDto assignedRoute = routeService.assignRouteToDriver(id, request.getDriverId());
        return ResponseEntity.ok(assignedRoute);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update route status", description = "Update the status of a route")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<RouteDto> updateRouteStatus(
            @Parameter(description = "Route ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam RouteStatus status) {
        
        RouteDto updatedRoute = routeService.updateRouteStatus(id, status);
        return ResponseEntity.ok(updatedRoute);
    }

    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get routes by driver", description = "Get all routes assigned to a specific driver")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<RouteDto>> getRoutesByDriver(
            @Parameter(description = "Driver ID") @PathVariable Long driverId) {
        
        List<RouteDto> routes = routeService.getRoutesByDriver(driverId);
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get routes by status", description = "Get all routes with a specific status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<RouteDto>> getRoutesByStatus(
            @Parameter(description = "Route status") @PathVariable RouteStatus status) {
        
        List<RouteDto> routes = routeService.getRoutesByStatus(status);
        return ResponseEntity.ok(routes);
    }

    @PostMapping("/batch-optimize")
    @Operation(summary = "Batch route optimization", description = "Optimize multiple routes simultaneously")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RouteDto>> batchOptimizeRoutes(
            @Parameter(description = "List of route IDs to optimize") @RequestBody List<Long> routeIds,
            @Parameter(description = "Optimization strategy") @RequestParam OptimizationStrategy strategy) {
        
        List<RouteDto> optimizedRoutes = routeService.batchOptimizeRoutes(routeIds, strategy);
        return ResponseEntity.ok(optimizedRoutes);
    }

    @GetMapping("/{id}/waypoints")
    @Operation(summary = "Get route waypoints", description = "Get all waypoints for a specific route")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<RouteDto> getRouteWithWaypoints(
            @Parameter(description = "Route ID") @PathVariable Long id) {
        
        RouteDto routeWithWaypoints = routeService.getRouteWithWaypoints(id);
        return ResponseEntity.ok(routeWithWaypoints);
    }

    @PostMapping("/{id}/waypoints/reorder")
    @Operation(summary = "Reorder route waypoints", description = "Reorder the waypoints in a route")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteDto> reorderWaypoints(
            @Parameter(description = "Route ID") @PathVariable Long id,
            @Parameter(description = "New waypoint order") @RequestBody List<Long> waypointIds) {
        
        RouteDto updatedRoute = routeService.reorderWaypoints(id, waypointIds);
        return ResponseEntity.ok(updatedRoute);
    }
} 