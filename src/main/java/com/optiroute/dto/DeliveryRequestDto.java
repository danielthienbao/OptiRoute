package com.optiroute.dto;

import com.optiroute.model.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Delivery request data transfer object")
public class DeliveryRequestDto {

    @Schema(description = "Delivery request ID", example = "1")
    private Long id;

    @Schema(description = "Customer information")
    private UserDto customer;

    @Schema(description = "Assigned driver information")
    private UserDto driver;

    @Schema(description = "Pickup location")
    private LocationDto pickupLocation;

    @Schema(description = "Delivery location")
    private LocationDto deliveryLocation;

    @Schema(description = "Current delivery status", example = "PENDING")
    private DeliveryStatus status;

    @Schema(description = "Package description", example = "Fragile electronics package")
    private String packageDescription;

    @Schema(description = "Package weight in kilograms", example = "5.5")
    private BigDecimal weight;

    @Schema(description = "Package dimensions", example = "30x20x15 cm")
    private String dimensions;

    @Schema(description = "Package value", example = "299.99")
    private BigDecimal value;

    @Schema(description = "Whether package requires signature", example = "true")
    private Boolean requiresSignature;

    @Schema(description = "Whether package is fragile", example = "false")
    private Boolean isFragile;

    @Schema(description = "Whether delivery is urgent", example = "false")
    private Boolean isUrgent;

    @Schema(description = "Special instructions", example = "Leave with doorman if no answer")
    private String specialInstructions;

    @Schema(description = "Requested pickup date and time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestedPickupDate;

    @Schema(description = "Requested delivery date and time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestedDeliveryDate;

    @Schema(description = "Actual pickup date and time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualPickupDate;

    @Schema(description = "Actual delivery date and time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualDeliveryDate;

    @Schema(description = "Estimated delivery time in minutes", example = "120")
    private Integer estimatedDeliveryTime;

    @Schema(description = "Actual delivery time in minutes", example = "135")
    private Integer actualDeliveryTime;

    @Schema(description = "Delivery fee", example = "25.00")
    private BigDecimal deliveryFee;

    @Schema(description = "Driver notes", example = "Customer was not home, left with neighbor")
    private String driverNotes;

    @Schema(description = "Customer feedback", example = "Great service, very professional")
    private String customerFeedback;

    @Schema(description = "Failure reason if delivery failed", example = "Address not found")
    private String failureReason;

    @Schema(description = "Recipient signature")
    private String recipientSignature;

    @Schema(description = "Tracking number", example = "TRK123456789")
    private String trackingNumber;

    @Schema(description = "Route ID if assigned to a route", example = "1")
    private Long routeId;

    @Schema(description = "Creation timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "Priority level", example = "1")
    private Integer priority;

    @Schema(description = "Tags for categorization", example = "[\"express\", \"fragile\"]")
    private String[] tags;
} 