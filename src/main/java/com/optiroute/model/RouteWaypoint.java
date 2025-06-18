package com.optiroute.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "route_waypoints")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteWaypoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    
    @Column(name = "waypoint_order", nullable = false)
    private Integer waypointOrder;
    
    @Column(name = "estimated_arrival")
    private LocalDateTime estimatedArrival;
    
    @Column(name = "actual_arrival")
    private LocalDateTime actualArrival;
    
    @Column(name = "estimated_departure")
    private LocalDateTime estimatedDeparture;
    
    @Column(name = "actual_departure")
    private LocalDateTime actualDeparture;
    
    @Column(name = "distance_from_previous")
    private Double distanceFromPrevious; // in kilometers
    
    @Column(name = "duration_from_previous")
    private Integer durationFromPrevious; // in minutes
    
    @Enumerated(EnumType.STRING)
    private WaypointStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum WaypointStatus {
        PENDING, IN_PROGRESS, COMPLETED, SKIPPED
    }
} 