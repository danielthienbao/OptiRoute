package com.optiroute.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
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
@Schema(description = "Delivery update request")
public class DeliveryUpdateRequest {

    @Schema(description = "Customer ID", example = "1")
    private Long customerId;

    @Schema(description = "Driver ID", example = "2")
    private Long driverId;

    @Schema(description = "Pickup location ID", example = "1")
    private Long pickupLocationId;

    @Schema(description = "Delivery location ID", example = "2")
    private Long deliveryLocationId;

    @Size(max = 500, message = "Package description must not exceed 500 characters")
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

    @Size(max = 1000, message = "Special instructions must not exceed 1000 characters")
    @Schema(description = "Special instructions", example = "Leave with doorman if no answer")
    private String specialInstructions;

    @Schema(description = "Requested pickup date and time")
    private LocalDateTime requestedPickupDate;

    @Schema(description = "Requested delivery date and time")
    private LocalDateTime requestedDeliveryDate;

    @Schema(description = "Priority level", example = "1")
    private Integer priority;

    @Schema(description = "Tags for categorization", example = "[\"express\", \"fragile\"]")
    private String[] tags;
} 