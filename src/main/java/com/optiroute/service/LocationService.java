package com.optiroute.service;

import com.optiroute.dto.LocationDto;
import com.optiroute.exception.ResourceNotFoundException;
import com.optiroute.model.Location;
import com.optiroute.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LocationService {
    
    private final LocationRepository locationRepository;
    private final GeometryFactory geometryFactory;
    
    public LocationDto.LocationResponse createLocation(LocationDto.CreateLocationRequest request) {
        Point coordinates = null;
        if (request.getLatitude() != null && request.getLongitude() != null) {
            coordinates = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        }
        
        Location location = Location.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .postalCode(request.getPostalCode())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .coordinates(coordinates)
                .locationType(request.getLocationType())
                .isActive(true)
                .build();
        
        Location savedLocation = locationRepository.save(location);
        log.info("Created new location: {}", savedLocation.getName());
        
        return LocationDto.LocationResponse.fromLocation(savedLocation);
    }
    
    public LocationDto.LocationResponse getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        
        return LocationDto.LocationResponse.fromLocation(location);
    }
    
    public List<LocationDto.LocationResponse> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(LocationDto.LocationResponse::fromLocation)
                .toList();
    }
    
    public Page<LocationDto.LocationResponse> getAllActiveLocations(Pageable pageable) {
        return locationRepository.findAllActive(pageable)
                .map(LocationDto.LocationResponse::fromLocation);
    }
    
    public List<LocationDto.LocationResponse> getLocationsByType(Location.LocationType locationType) {
        return locationRepository.findByLocationType(locationType).stream()
                .map(LocationDto.LocationResponse::fromLocation)
                .toList();
    }
    
    public List<LocationDto.LocationResponse> getLocationsByCity(String city) {
        return locationRepository.findByCity(city).stream()
                .map(LocationDto.LocationResponse::fromLocation)
                .toList();
    }
    
    public List<LocationDto.LocationResponse> searchLocations(String searchTerm) {
        return locationRepository.searchLocations(searchTerm).stream()
                .map(LocationDto.LocationResponse::fromLocation)
                .toList();
    }
    
    public List<LocationDto.LocationResponse> findNearbyLocations(LocationDto.NearbyLocationRequest request) {
        List<Location> locations;
        
        if (request.getLocationType() != null) {
            locations = locationRepository.findNearbyLocationsByType(
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getRadiusKm(),
                    request.getLocationType().name(),
                    request.getLimit()
            );
        } else {
            locations = locationRepository.findNearbyLocations(
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getRadiusKm()
            );
        }
        
        return locations.stream()
                .map(LocationDto.LocationResponse::fromLocation)
                .toList();
    }
    
    public LocationDto.DistanceResponse calculateDistance(LocationDto.DistanceCalculationRequest request) {
        Double distanceKm = null;
        
        if (request.getFromLocationId() != null && request.getToLocationId() != null) {
            // Calculate distance between two stored locations
            distanceKm = locationRepository.calculateDistanceBetweenLocations(
                    request.getFromLocationId(),
                    request.getToLocationId()
            );
        } else if (request.getFromLatitude() != null && request.getFromLongitude() != null &&
                   request.getToLatitude() != null && request.getToLongitude() != null) {
            // Calculate distance between coordinates
            distanceKm = locationRepository.calculateDistanceBetweenCoordinates(
                    request.getFromLatitude(),
                    request.getFromLongitude(),
                    request.getToLatitude(),
                    request.getToLongitude()
            );
        } else {
            throw new IllegalArgumentException("Either location IDs or coordinates must be provided");
        }
        
        // Estimate duration (assuming average speed of 30 km/h for driving)
        Integer durationMinutes = distanceKm != null ? (int) (distanceKm * 2) : null;
        
        return LocationDto.DistanceResponse.builder()
                .distanceKm(distanceKm)
                .durationMinutes(durationMinutes)
                .routeType("driving")
                .build();
    }
    
    public LocationDto.LocationResponse updateLocation(Long id, LocationDto.UpdateLocationRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        
        if (request.getName() != null) {
            location.setName(request.getName());
        }
        if (request.getDescription() != null) {
            location.setDescription(request.getDescription());
        }
        if (request.getAddress() != null) {
            location.setAddress(request.getAddress());
        }
        if (request.getPostalCode() != null) {
            location.setPostalCode(request.getPostalCode());
        }
        if (request.getCity() != null) {
            location.setCity(request.getCity());
        }
        if (request.getState() != null) {
            location.setState(request.getState());
        }
        if (request.getCountry() != null) {
            location.setCountry(request.getCountry());
        }
        if (request.getLatitude() != null && request.getLongitude() != null) {
            Point coordinates = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
            location.setCoordinates(coordinates);
        }
        if (request.getLocationType() != null) {
            location.setLocationType(request.getLocationType());
        }
        if (request.getIsActive() != null) {
            location.setIsActive(request.getIsActive());
        }
        
        Location updatedLocation = locationRepository.save(location);
        log.info("Updated location: {}", updatedLocation.getName());
        
        return LocationDto.LocationResponse.fromLocation(updatedLocation);
    }
    
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        
        location.setIsActive(false);
        locationRepository.save(location);
        
        log.info("Deactivated location: {}", location.getName());
    }
    
    public void activateLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        
        location.setIsActive(true);
        locationRepository.save(location);
        
        log.info("Activated location: {}", location.getName());
    }
} 