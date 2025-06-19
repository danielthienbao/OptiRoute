package com.optiroute.controller;

import com.optiroute.dto.DeliveryRequestDto;
import com.optiroute.dto.DeliveryCreateRequest;
import com.optiroute.dto.DeliveryUpdateRequest;
import com.optiroute.dto.DeliveryAssignmentRequest;
import com.optiroute.model.DeliveryRequest;
import com.optiroute.model.DeliveryStatus;
import com.optiroute.service.DeliveryService;
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
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Delivery Management", description = "APIs for managing delivery requests and tracking")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    @Operation(summary = "Get all delivery requests", description = "Retrieve all delivery requests with pagination and filtering")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<Page<DeliveryRequestDto>> getAllDeliveries(
            @Parameter(description = "Status filter") @RequestParam(required = false) DeliveryStatus status,
            @Parameter(description = "Driver ID filter") @RequestParam(required = false) Long driverId,
            @Parameter(description = "Customer ID filter") @RequestParam(required = false) Long customerId,
            @Parameter(description = "Pageable parameters") Pageable pageable) {
        
        Page<DeliveryRequestDto> deliveries = deliveryService.getAllDeliveries(status, driverId, customerId, pageable);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get delivery by ID", description = "Retrieve a specific delivery request by its ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<DeliveryRequestDto> getDeliveryById(
            @Parameter(description = "Delivery ID") @PathVariable Long id) {
        
        DeliveryRequestDto delivery = deliveryService.getDeliveryById(id);
        return ResponseEntity.ok(delivery);
    }

    @PostMapping
    @Operation(summary = "Create new delivery request", description = "Create a new delivery request")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryRequestDto> createDelivery(
            @Parameter(description = "Delivery data") @Valid @RequestBody DeliveryCreateRequest request) {
        
        DeliveryRequestDto createdDelivery = deliveryService.createDelivery(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDelivery);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update delivery request", description = "Update an existing delivery request")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryRequestDto> updateDelivery(
            @Parameter(description = "Delivery ID") @PathVariable Long id,
            @Parameter(description = "Updated delivery data") @Valid @RequestBody DeliveryUpdateRequest request) {
        
        DeliveryRequestDto updatedDelivery = deliveryService.updateDelivery(id, request);
        return ResponseEntity.ok(updatedDelivery);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete delivery request", description = "Delete a delivery request by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDelivery(
            @Parameter(description = "Delivery ID") @PathVariable Long id) {
        
        deliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/assign")
    @Operation(summary = "Assign delivery to driver", description = "Assign a delivery request to a specific driver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryRequestDto> assignDeliveryToDriver(
            @Parameter(description = "Delivery ID") @PathVariable Long id,
            @Parameter(description = "Assignment request") @Valid @RequestBody DeliveryAssignmentRequest request) {
        
        DeliveryRequestDto assignedDelivery = deliveryService.assignDeliveryToDriver(id, request.getDriverId());
        return ResponseEntity.ok(assignedDelivery);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update delivery status", description = "Update the status of a delivery request")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<DeliveryRequestDto> updateDeliveryStatus(
            @Parameter(description = "Delivery ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam DeliveryStatus status,
            @Parameter(description = "Status notes") @RequestParam(required = false) String notes) {
        
        DeliveryRequestDto updatedDelivery = deliveryService.updateDeliveryStatus(id, status, notes);
        return ResponseEntity.ok(updatedDelivery);
    }

    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get deliveries by driver", description = "Get all delivery requests assigned to a specific driver")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<DeliveryRequestDto>> getDeliveriesByDriver(
            @Parameter(description = "Driver ID") @PathVariable Long driverId) {
        
        List<DeliveryRequestDto> deliveries = deliveryService.getDeliveriesByDriver(driverId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get deliveries by status", description = "Get all delivery requests with a specific status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<DeliveryRequestDto>> getDeliveriesByStatus(
            @Parameter(description = "Delivery status") @PathVariable DeliveryStatus status) {
        
        List<DeliveryRequestDto> deliveries = deliveryService.getDeliveriesByStatus(status);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get deliveries by customer", description = "Get all delivery requests for a specific customer")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<DeliveryRequestDto>> getDeliveriesByCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        
        List<DeliveryRequestDto> deliveries = deliveryService.getDeliveriesByCustomer(customerId);
        return ResponseEntity.ok(deliveries);
    }

    @PostMapping("/{id}/pickup")
    @Operation(summary = "Mark delivery as picked up", description = "Mark a delivery request as picked up by driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DeliveryRequestDto> markAsPickedUp(
            @Parameter(description = "Delivery ID") @PathVariable Long id,
            @Parameter(description = "Pickup notes") @RequestParam(required = false) String notes) {
        
        DeliveryRequestDto updatedDelivery = deliveryService.markAsPickedUp(id, notes);
        return ResponseEntity.ok(updatedDelivery);
    }

    @PostMapping("/{id}/delivered")
    @Operation(summary = "Mark delivery as delivered", description = "Mark a delivery request as successfully delivered")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DeliveryRequestDto> markAsDelivered(
            @Parameter(description = "Delivery ID") @PathVariable Long id,
            @Parameter(description = "Delivery notes") @RequestParam(required = false) String notes,
            @Parameter(description = "Recipient signature") @RequestParam(required = false) String signature) {
        
        DeliveryRequestDto updatedDelivery = deliveryService.markAsDelivered(id, notes, signature);
        return ResponseEntity.ok(updatedDelivery);
    }

    @PostMapping("/{id}/failed")
    @Operation(summary = "Mark delivery as failed", description = "Mark a delivery request as failed")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DeliveryRequestDto> markAsFailed(
            @Parameter(description = "Delivery ID") @PathVariable Long id,
            @Parameter(description = "Failure reason") @RequestParam String reason,
            @Parameter(description = "Failure notes") @RequestParam(required = false) String notes) {
        
        DeliveryRequestDto updatedDelivery = deliveryService.markAsFailed(id, reason, notes);
        return ResponseEntity.ok(updatedDelivery);
    }

    @GetMapping("/urgent")
    @Operation(summary = "Get urgent deliveries", description = "Get all urgent delivery requests")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<DeliveryRequestDto>> getUrgentDeliveries() {
        
        List<DeliveryRequestDto> urgentDeliveries = deliveryService.getUrgentDeliveries();
        return ResponseEntity.ok(urgentDeliveries);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue deliveries", description = "Get all overdue delivery requests")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<DeliveryRequestDto>> getOverdueDeliveries() {
        
        List<DeliveryRequestDto> overdueDeliveries = deliveryService.getOverdueDeliveries();
        return ResponseEntity.ok(overdueDeliveries);
    }

    @PostMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule delivery", description = "Reschedule a delivery request")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryRequestDto> rescheduleDelivery(
            @Parameter(description = "Delivery ID") @PathVariable Long id,
            @Parameter(description = "New pickup date") @RequestParam String newPickupDate,
            @Parameter(description = "New delivery date") @RequestParam String newDeliveryDate,
            @Parameter(description = "Reschedule reason") @RequestParam(required = false) String reason) {
        
        DeliveryRequestDto rescheduledDelivery = deliveryService.rescheduleDelivery(id, newPickupDate, newDeliveryDate, reason);
        return ResponseEntity.ok(rescheduledDelivery);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get delivery statistics", description = "Get delivery statistics for dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryStats> getDeliveryStats() {
        
        DeliveryStats stats = deliveryService.getDeliveryStats();
        return ResponseEntity.ok(stats);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryStats {
        private Long totalDeliveries;
        private Long pendingDeliveries;
        private Long inProgressDeliveries;
        private Long completedDeliveries;
        private Long failedDeliveries;
        private Long urgentDeliveries;
        private Long overdueDeliveries;
        private Double averageDeliveryTime;
        private Double successRate;
    }
} 