package com.optiroute.controller;

import com.optiroute.dto.LocationDto;
import com.optiroute.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Locations", description = "Location management endpoints")
public class LocationController {
    
    private final LocationService locationService;
    
    @PostMapping
    @Operation(summary = "Create a new location", description = "Create a new location with coordinates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocationDto.LocationResponse> createLocation(@Valid @RequestBody LocationDto.CreateLocationRequest request) {
        LocationDto.LocationResponse location = locationService.createLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(location);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get location by ID", description = "Retrieve a specific location by its ID")
    public ResponseEntity<LocationDto.LocationResponse> getLocationById(@PathVariable Long id) {
        LocationDto.LocationResponse location = locationService.getLocationById(id);
        return ResponseEntity.ok(location);
    }
    
    @GetMapping
    @Operation(summary = "Get all locations", description = "Retrieve all locations with pagination")
    public ResponseEntity<Page<LocationDto.LocationResponse>> getAllLocations(Pageable pageable) {
        Page<LocationDto.LocationResponse> locations = locationService.getAllActiveLocations(pageable);
        return ResponseEntity.ok(locations);
    }
    
    @GetMapping("/type/{locationType}")
    @Operation(summary = "Get locations by type", description = "Retrieve locations filtered by type")
    public ResponseEntity<List<LocationDto.LocationResponse>> getLocationsByType(@PathVariable String locationType) {
        Location.LocationType type = Location.LocationType.valueOf(locationType.toUpperCase());
        List<LocationDto.LocationResponse> locations = locationService.getLocationsByType(type);
        return ResponseEntity.ok(locations);
    }
    
    @GetMapping("/city/{city}")
    @Operation(summary = "Get locations by city", description = "Retrieve locations filtered by city")
    public ResponseEntity<List<LocationDto.LocationResponse>> getLocationsByCity(@PathVariable String city) {
        List<LocationDto.LocationResponse> locations = locationService.getLocationsByCity(city);
        return ResponseEntity.ok(locations);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search locations", description = "Search locations by name, address, or city")
    public ResponseEntity<List<LocationDto.LocationResponse>> searchLocations(@RequestParam String query) {
        List<LocationDto.LocationResponse> locations = locationService.searchLocations(query);
        return ResponseEntity.ok(locations);
    }
    
    @PostMapping("/nearby")
    @Operation(summary = "Find nearby locations", description = "Find locations within a specified radius")
    public ResponseEntity<List<LocationDto.LocationResponse>> findNearbyLocations(@Valid @RequestBody LocationDto.NearbyLocationRequest request) {
        List<LocationDto.LocationResponse> locations = locationService.findNearbyLocations(request);
        return ResponseEntity.ok(locations);
    }
    
    @PostMapping("/distance")
    @Operation(summary = "Calculate distance", description = "Calculate distance between two locations or coordinates")
    public ResponseEntity<LocationDto.DistanceResponse> calculateDistance(@Valid @RequestBody LocationDto.DistanceCalculationRequest request) {
        LocationDto.DistanceResponse distance = locationService.calculateDistance(request);
        return ResponseEntity.ok(distance);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update location", description = "Update an existing location")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocationDto.LocationResponse> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody LocationDto.UpdateLocationRequest request) {
        LocationDto.LocationResponse location = locationService.updateLocation(id, request);
        return ResponseEntity.ok(location);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete location", description = "Deactivate a location (soft delete)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate location", description = "Reactivate a deactivated location")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateLocation(@PathVariable Long id) {
        locationService.activateLocation(id);
        return ResponseEntity.ok().build();
    }
} 