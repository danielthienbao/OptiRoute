package com.optiroute.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "request_number", unique = true, nullable = false)
    private String requestNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private User driver;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_location_id", nullable = false)
    private Location pickupLocation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_location_id", nullable = false)
    private Location deliveryLocation;
    
    @Column(name = "pickup_time")
    private LocalDateTime pickupTime;
    
    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;
    
    @Column(name = "requested_pickup_time")
    private LocalDateTime requestedPickupTime;
    
    @Column(name = "requested_delivery_time")
    private LocalDateTime requestedDeliveryTime;
    
    @Column(name = "package_weight")
    private Double packageWeight; // in kg
    
    @Column(name = "package_dimensions")
    private String packageDimensions; // "LxWxH" in cm
    
    @Column(name = "package_description")
    private String packageDescription;
    
    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;
    
    @Column(name = "estimated_distance")
    private Double estimatedDistance; // in kilometers
    
    @Column(name = "estimated_duration")
    private Integer estimatedDuration; // in minutes
    
    @Column(name = "actual_distance")
    private Double actualDistance; // in kilometers
    
    @Column(name = "actual_duration")
    private Integer actualDuration; // in minutes
    
    @Column(name = "base_fare")
    private BigDecimal baseFare;
    
    @Column(name = "distance_fare")
    private BigDecimal distanceFare;
    
    @Column(name = "total_fare")
    private BigDecimal totalFare;
    
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    
    @Enumerated(EnumType.STRING)
    private Priority priority;
    
    @Column(name = "is_urgent")
    private Boolean isUrgent = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (requestNumber == null) {
            requestNumber = generateRequestNumber();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateRequestNumber() {
        return "DR" + System.currentTimeMillis();
    }
    
    public enum DeliveryStatus {
        PENDING, ASSIGNED, PICKED_UP, IN_TRANSIT, DELIVERED, CANCELLED, FAILED
    }
    
    public enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }
} 