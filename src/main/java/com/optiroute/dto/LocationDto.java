package com.optiroute.dto;

import com.optiroute.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

public class LocationDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateLocationRequest {
        private String name;
        private String description;
        private String address;
        private String postalCode;
        private String city;
        private String state;
        private String country;
        private Double latitude;
        private Double longitude;
        private Location.LocationType locationType;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateLocationRequest {
        private String name;
        private String description;
        private String address;
        private String postalCode;
        private String city;
        private String state;
        private String country;
        private Double latitude;
        private Double longitude;
        private Location.LocationType locationType;
        private Boolean isActive;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationResponse {
        private Long id;
        private String name;
        private String description;
        private String address;
        private String postalCode;
        private String city;
        private String state;
        private String country;
        private Double latitude;
        private Double longitude;
        private Location.LocationType locationType;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public static LocationResponse fromLocation(Location location) {
            Double latitude = null;
            Double longitude = null;
            
            if (location.getCoordinates() != null) {
                latitude = location.getCoordinates().getY();
                longitude = location.getCoordinates().getX();
            }
            
            return LocationResponse.builder()
                    .id(location.getId())
                    .name(location.getName())
                    .description(location.getDescription())
                    .address(location.getAddress())
                    .postalCode(location.getPostalCode())
                    .city(location.getCity())
                    .state(location.getState())
                    .country(location.getCountry())
                    .latitude(latitude)
                    .longitude(longitude)
                    .locationType(location.getLocationType())
                    .isActive(location.getIsActive())
                    .createdAt(location.getCreatedAt())
                    .updatedAt(location.getUpdatedAt())
                    .build();
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NearbyLocationRequest {
        private Double latitude;
        private Double longitude;
        private Double radiusKm = 10.0; // default 10km radius
        private Location.LocationType locationType;
        private Integer limit = 50;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistanceCalculationRequest {
        private Long fromLocationId;
        private Long toLocationId;
        private Double fromLatitude;
        private Double fromLongitude;
        private Double toLatitude;
        private Double toLongitude;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistanceResponse {
        private Double distanceKm;
        private Integer durationMinutes;
        private String routeType; // "driving", "walking", "cycling"
    }
} 