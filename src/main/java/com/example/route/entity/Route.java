package com.example.route.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.LineString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "origin_id")
    private Location origin;
    
    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Location destination;
    
    @Column(name = "total_distance")
    private Double totalDistance;
    
    @Column(name = "total_duration")
    private Integer totalDuration; // in seconds
    
    @Column(name = "waypoints", columnDefinition = "jsonb")
    private String waypoints; // JSON string of waypoint data
    
    @Column(name = "route_geometry", columnDefinition = "geometry(LineString,4326)")
    private LineString routeGeometry;
    
    @Column(name = "is_round_trip")
    private Boolean isRoundTrip = false;
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RouteWaypoint> routeWaypoints;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Route() {}
    
    public Route(String name, Location origin, Location destination) {
        this.name = name;
        this.origin = origin;
        this.destination = destination;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Location getOrigin() {
        return origin;
    }
    
    public void setOrigin(Location origin) {
        this.origin = origin;
    }
    
    public Location getDestination() {
        return destination;
    }
    
    public void setDestination(Location destination) {
        this.destination = destination;
    }
    
    public Double getTotalDistance() {
        return totalDistance;
    }
    
    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }
    
    public Integer getTotalDuration() {
        return totalDuration;
    }
    
    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }
    
    public String getWaypoints() {
        return waypoints;
    }
    
    public void setWaypoints(String waypoints) {
        this.waypoints = waypoints;
    }
    
    public LineString getRouteGeometry() {
        return routeGeometry;
    }
    
    public void setRouteGeometry(LineString routeGeometry) {
        this.routeGeometry = routeGeometry;
    }
    
    public Boolean getIsRoundTrip() {
        return isRoundTrip;
    }
    
    public void setIsRoundTrip(Boolean isRoundTrip) {
        this.isRoundTrip = isRoundTrip;
    }
    
    public List<RouteWaypoint> getRouteWaypoints() {
        return routeWaypoints;
    }
    
    public void setRouteWaypoints(List<RouteWaypoint> routeWaypoints) {
        this.routeWaypoints = routeWaypoints;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", origin=" + (origin != null ? origin.getName() : "null") +
                ", destination=" + (destination != null ? destination.getName() : "null") +
                ", totalDistance=" + totalDistance +
                ", totalDuration=" + totalDuration +
                ", isRoundTrip=" + isRoundTrip +
                '}';
    }
} 