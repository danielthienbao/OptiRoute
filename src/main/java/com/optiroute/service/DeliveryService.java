package com.optiroute.service;

import com.optiroute.dto.DeliveryRequestDto;
import com.optiroute.dto.DeliveryCreateRequest;
import com.optiroute.dto.DeliveryUpdateRequest;
import com.optiroute.exception.ResourceNotFoundException;
import com.optiroute.model.*;
import com.optiroute.repository.DeliveryRequestRepository;
import com.optiroute.repository.UserRepository;
import com.optiroute.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryService {

    private final DeliveryRequestRepository deliveryRequestRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    /**
     * Get all deliveries with optional filtering
     */
    public Page<DeliveryRequestDto> getAllDeliveries(DeliveryStatus status, Long driverId, Long customerId, Pageable pageable) {
        Page<DeliveryRequest> deliveries;
        
        if (status != null && driverId != null && customerId != null) {
            deliveries = deliveryRequestRepository.findByStatusAndDriverIdAndCustomerId(status, driverId, customerId, pageable);
        } else if (status != null && driverId != null) {
            deliveries = deliveryRequestRepository.findByStatusAndDriverId(status, driverId, pageable);
        } else if (status != null && customerId != null) {
            deliveries = deliveryRequestRepository.findByStatusAndCustomerId(status, customerId, pageable);
        } else if (driverId != null && customerId != null) {
            deliveries = deliveryRequestRepository.findByDriverIdAndCustomerId(driverId, customerId, pageable);
        } else if (status != null) {
            deliveries = deliveryRequestRepository.findByStatus(status, pageable);
        } else if (driverId != null) {
            deliveries = deliveryRequestRepository.findByDriverId(driverId, pageable);
        } else if (customerId != null) {
            deliveries = deliveryRequestRepository.findByCustomerId(customerId, pageable);
        } else {
            deliveries = deliveryRequestRepository.findAll(pageable);
        }
        
        return deliveries.map(this::convertToDto);
    }

    /**
     * Get delivery by ID
     */
    public DeliveryRequestDto getDeliveryById(Long id) {
        DeliveryRequest delivery = deliveryRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        return convertToDto(delivery);
    }

    /**
     * Create new delivery request
     */
    public DeliveryRequestDto createDelivery(DeliveryCreateRequest request) {
        // Validate customer exists
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
        
        // Validate driver if provided
        User driver = null;
        if (request.getDriverId() != null) {
            driver = userRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + request.getDriverId()));
            
            // Verify driver role
            if (!driver.getRole().equals(User.Role.DRIVER)) {
                throw new IllegalArgumentException("User with ID " + request.getDriverId() + " is not a driver");
            }
        }
        
        // Validate locations
        Location pickupLocation = locationRepository.findById(request.getPickupLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Pickup location not found with id: " + request.getPickupLocationId()));
        
        Location deliveryLocation = locationRepository.findById(request.getDeliveryLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery location not found with id: " + request.getDeliveryLocationId()));
        
        // Generate tracking number
        String trackingNumber = generateTrackingNumber();
        
        DeliveryRequest delivery = DeliveryRequest.builder()
                .customer(customer)
                .driver(driver)
                .pickupLocation(pickupLocation)
                .deliveryLocation(deliveryLocation)
                .status(DeliveryStatus.PENDING)
                .packageDescription(request.getPackageDescription())
                .weight(request.getWeight())
                .dimensions(request.getDimensions())
                .value(request.getValue())
                .requiresSignature(request.getRequiresSignature())
                .isFragile(request.getIsFragile())
                .isUrgent(request.getIsUrgent())
                .specialInstructions(request.getSpecialInstructions())
                .requestedPickupDate(request.getRequestedPickupDate())
                .requestedDeliveryDate(request.getRequestedDeliveryDate())
                .trackingNumber(trackingNumber)
                .priority(request.getPriority())
                .tags(request.getTags())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        DeliveryRequest savedDelivery = deliveryRequestRepository.save(delivery);
        log.info("Created new delivery with tracking number: {}", trackingNumber);
        
        return convertToDto(savedDelivery);
    }

    /**
     * Update existing delivery request
     */
    public DeliveryRequestDto updateDelivery(Long id, DeliveryUpdateRequest request) {
        DeliveryRequest existingDelivery = deliveryRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        
        // Update fields if provided
        if (request.getCustomerId() != null) {
            User customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
            existingDelivery.setCustomer(customer);
        }
        
        if (request.getDriverId() != null) {
            User driver = userRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + request.getDriverId()));
            
            if (!driver.getRole().equals(User.Role.DRIVER)) {
                throw new IllegalArgumentException("User with ID " + request.getDriverId() + " is not a driver");
            }
            existingDelivery.setDriver(driver);
        }
        
        if (request.getPickupLocationId() != null) {
            Location pickupLocation = locationRepository.findById(request.getPickupLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pickup location not found with id: " + request.getPickupLocationId()));
            existingDelivery.setPickupLocation(pickupLocation);
        }
        
        if (request.getDeliveryLocationId() != null) {
            Location deliveryLocation = locationRepository.findById(request.getDeliveryLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Delivery location not found with id: " + request.getDeliveryLocationId()));
            existingDelivery.setDeliveryLocation(deliveryLocation);
        }
        
        if (request.getPackageDescription() != null) {
            existingDelivery.setPackageDescription(request.getPackageDescription());
        }
        if (request.getWeight() != null) {
            existingDelivery.setWeight(request.getWeight());
        }
        if (request.getDimensions() != null) {
            existingDelivery.setDimensions(request.getDimensions());
        }
        if (request.getValue() != null) {
            existingDelivery.setValue(request.getValue());
        }
        if (request.getRequiresSignature() != null) {
            existingDelivery.setRequiresSignature(request.getRequiresSignature());
        }
        if (request.getIsFragile() != null) {
            existingDelivery.setIsFragile(request.getIsFragile());
        }
        if (request.getIsUrgent() != null) {
            existingDelivery.setIsUrgent(request.getIsUrgent());
        }
        if (request.getSpecialInstructions() != null) {
            existingDelivery.setSpecialInstructions(request.getSpecialInstructions());
        }
        if (request.getRequestedPickupDate() != null) {
            existingDelivery.setRequestedPickupDate(request.getRequestedPickupDate());
        }
        if (request.getRequestedDeliveryDate() != null) {
            existingDelivery.setRequestedDeliveryDate(request.getRequestedDeliveryDate());
        }
        if (request.getPriority() != null) {
            existingDelivery.setPriority(request.getPriority());
        }
        if (request.getTags() != null) {
            existingDelivery.setTags(request.getTags());
        }
        
        existingDelivery.setUpdatedAt(LocalDateTime.now());
        
        DeliveryRequest updatedDelivery = deliveryRequestRepository.save(existingDelivery);
        log.info("Updated delivery with ID: {}", updatedDelivery.getId());
        
        return convertToDto(updatedDelivery);
    }

    /**
     * Delete delivery request
     */
    public void deleteDelivery(Long id) {
        if (!deliveryRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Delivery not found with id: " + id);
        }
        
        deliveryRequestRepository.deleteById(id);
        log.info("Deleted delivery with ID: {}", id);
    }

    /**
     * Assign delivery to driver
     */
    public DeliveryRequestDto assignDeliveryToDriver(Long deliveryId, Long driverId) {
        DeliveryRequest delivery = deliveryRequestRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));
        
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));
        
        if (!driver.getRole().equals(User.Role.DRIVER)) {
            throw new IllegalArgumentException("User with ID " + driverId + " is not a driver");
        }
        
        delivery.setDriver(driver);
        delivery.setUpdatedAt(LocalDateTime.now());
        
        DeliveryRequest savedDelivery = deliveryRequestRepository.save(delivery);
        log.info("Assigned delivery {} to driver {}", deliveryId, driverId);
        
        return convertToDto(savedDelivery);
    }

    /**
     * Update delivery status
     */
    public DeliveryRequestDto updateDeliveryStatus(Long id, DeliveryStatus status, String notes) {
        DeliveryRequest delivery = deliveryRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        
        delivery.setStatus(status);
        delivery.setUpdatedAt(LocalDateTime.now());
        
        // Update timestamps based on status
        if (status == DeliveryStatus.IN_PROGRESS && delivery.getActualPickupDate() == null) {
            delivery.setActualPickupDate(LocalDateTime.now());
        } else if (status == DeliveryStatus.COMPLETED && delivery.getActualDeliveryDate() == null) {
            delivery.setActualDeliveryDate(LocalDateTime.now());
            // Calculate actual delivery time
            if (delivery.getActualPickupDate() != null) {
                long minutes = ChronoUnit.MINUTES.between(delivery.getActualPickupDate(), delivery.getActualDeliveryDate());
                delivery.setActualDeliveryTime((int) minutes);
            }
        }
        
        if (notes != null) {
            delivery.setDriverNotes(notes);
        }
        
        DeliveryRequest savedDelivery = deliveryRequestRepository.save(delivery);
        log.info("Updated delivery {} status to {}", id, status);
        
        return convertToDto(savedDelivery);
    }

    /**
     * Mark delivery as picked up
     */
    public DeliveryRequestDto markAsPickedUp(Long id, String notes) {
        return updateDeliveryStatus(id, DeliveryStatus.IN_PROGRESS, notes);
    }

    /**
     * Mark delivery as delivered
     */
    public DeliveryRequestDto markAsDelivered(Long id, String notes, String signature) {
        DeliveryRequest delivery = deliveryRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        
        delivery.setStatus(DeliveryStatus.COMPLETED);
        delivery.setActualDeliveryDate(LocalDateTime.now());
        delivery.setRecipientSignature(signature);
        delivery.setUpdatedAt(LocalDateTime.now());
        
        if (notes != null) {
            delivery.setDriverNotes(notes);
        }
        
        // Calculate actual delivery time
        if (delivery.getActualPickupDate() != null) {
            long minutes = ChronoUnit.MINUTES.between(delivery.getActualPickupDate(), delivery.getActualDeliveryDate());
            delivery.setActualDeliveryTime((int) minutes);
        }
        
        DeliveryRequest savedDelivery = deliveryRequestRepository.save(delivery);
        log.info("Marked delivery {} as delivered", id);
        
        return convertToDto(savedDelivery);
    }

    /**
     * Mark delivery as failed
     */
    public DeliveryRequestDto markAsFailed(Long id, String reason, String notes) {
        DeliveryRequest delivery = deliveryRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        
        delivery.setStatus(DeliveryStatus.FAILED);
        delivery.setFailureReason(reason);
        delivery.setUpdatedAt(LocalDateTime.now());
        
        if (notes != null) {
            delivery.setDriverNotes(notes);
        }
        
        DeliveryRequest savedDelivery = deliveryRequestRepository.save(delivery);
        log.info("Marked delivery {} as failed: {}", id, reason);
        
        return convertToDto(savedDelivery);
    }

    /**
     * Get deliveries by driver
     */
    public List<DeliveryRequestDto> getDeliveriesByDriver(Long driverId) {
        List<DeliveryRequest> deliveries = deliveryRequestRepository.findByDriverId(driverId);
        return deliveries.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get deliveries by status
     */
    public List<DeliveryRequestDto> getDeliveriesByStatus(DeliveryStatus status) {
        List<DeliveryRequest> deliveries = deliveryRequestRepository.findByStatus(status);
        return deliveries.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get deliveries by customer
     */
    public List<DeliveryRequestDto> getDeliveriesByCustomer(Long customerId) {
        List<DeliveryRequest> deliveries = deliveryRequestRepository.findByCustomerId(customerId);
        return deliveries.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get urgent deliveries
     */
    public List<DeliveryRequestDto> getUrgentDeliveries() {
        List<DeliveryRequest> urgentDeliveries = deliveryRequestRepository.findByIsUrgentTrue();
        return urgentDeliveries.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get overdue deliveries
     */
    public List<DeliveryRequestDto> getOverdueDeliveries() {
        List<DeliveryRequest> overdueDeliveries = deliveryRequestRepository.findOverdueDeliveries(LocalDateTime.now());
        return overdueDeliveries.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Reschedule delivery
     */
    public DeliveryRequestDto rescheduleDelivery(Long id, String newPickupDate, String newDeliveryDate, String reason) {
        DeliveryRequest delivery = deliveryRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        
        // Parse dates (in a real implementation, you'd use proper date parsing)
        LocalDateTime pickupDate = LocalDateTime.parse(newPickupDate);
        LocalDateTime deliveryDate = LocalDateTime.parse(newDeliveryDate);
        
        delivery.setRequestedPickupDate(pickupDate);
        delivery.setRequestedDeliveryDate(deliveryDate);
        delivery.setUpdatedAt(LocalDateTime.now());
        
        if (reason != null) {
            delivery.setDriverNotes("Rescheduled: " + reason);
        }
        
        DeliveryRequest savedDelivery = deliveryRequestRepository.save(delivery);
        log.info("Rescheduled delivery {} to pickup: {}, delivery: {}", id, pickupDate, deliveryDate);
        
        return convertToDto(savedDelivery);
    }

    /**
     * Get delivery statistics
     */
    public DeliveryController.DeliveryStats getDeliveryStats() {
        long totalDeliveries = deliveryRequestRepository.count();
        long pendingDeliveries = deliveryRequestRepository.countByStatus(DeliveryStatus.PENDING);
        long inProgressDeliveries = deliveryRequestRepository.countByStatus(DeliveryStatus.IN_PROGRESS);
        long completedDeliveries = deliveryRequestRepository.countByStatus(DeliveryStatus.COMPLETED);
        long failedDeliveries = deliveryRequestRepository.countByStatus(DeliveryStatus.FAILED);
        long urgentDeliveries = deliveryRequestRepository.countByIsUrgentTrue();
        long overdueDeliveries = deliveryRequestRepository.countOverdueDeliveries(LocalDateTime.now());
        
        // Calculate average delivery time and success rate
        Double averageDeliveryTime = deliveryRequestRepository.getAverageDeliveryTime();
        Double successRate = totalDeliveries > 0 ? 
                (double) completedDeliveries / totalDeliveries * 100 : 0.0;
        
        return DeliveryController.DeliveryStats.builder()
                .totalDeliveries(totalDeliveries)
                .pendingDeliveries(pendingDeliveries)
                .inProgressDeliveries(inProgressDeliveries)
                .completedDeliveries(completedDeliveries)
                .failedDeliveries(failedDeliveries)
                .urgentDeliveries(urgentDeliveries)
                .overdueDeliveries(overdueDeliveries)
                .averageDeliveryTime(averageDeliveryTime)
                .successRate(successRate)
                .build();
    }

    /**
     * Generate unique tracking number
     */
    private String generateTrackingNumber() {
        return "TRK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Convert DeliveryRequest entity to DTO
     */
    private DeliveryRequestDto convertToDto(DeliveryRequest delivery) {
        return DeliveryRequestDto.builder()
                .id(delivery.getId())
                .customer(convertUserToDto(delivery.getCustomer()))
                .driver(delivery.getDriver() != null ? convertUserToDto(delivery.getDriver()) : null)
                .pickupLocation(convertLocationToDto(delivery.getPickupLocation()))
                .deliveryLocation(convertLocationToDto(delivery.getDeliveryLocation()))
                .status(delivery.getStatus())
                .packageDescription(delivery.getPackageDescription())
                .weight(delivery.getWeight())
                .dimensions(delivery.getDimensions())
                .value(delivery.getValue())
                .requiresSignature(delivery.getRequiresSignature())
                .isFragile(delivery.getIsFragile())
                .isUrgent(delivery.getIsUrgent())
                .specialInstructions(delivery.getSpecialInstructions())
                .requestedPickupDate(delivery.getRequestedPickupDate())
                .requestedDeliveryDate(delivery.getRequestedDeliveryDate())
                .actualPickupDate(delivery.getActualPickupDate())
                .actualDeliveryDate(delivery.getActualDeliveryDate())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .actualDeliveryTime(delivery.getActualDeliveryTime())
                .deliveryFee(delivery.getDeliveryFee())
                .driverNotes(delivery.getDriverNotes())
                .customerFeedback(delivery.getCustomerFeedback())
                .failureReason(delivery.getFailureReason())
                .recipientSignature(delivery.getRecipientSignature())
                .trackingNumber(delivery.getTrackingNumber())
                .routeId(delivery.getRoute() != null ? delivery.getRoute().getId() : null)
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .priority(delivery.getPriority())
                .tags(delivery.getTags())
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
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .active(user.getIsActive())
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