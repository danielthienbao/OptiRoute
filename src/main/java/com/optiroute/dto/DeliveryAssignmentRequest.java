package com.optiroute.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Delivery assignment request")
public class DeliveryAssignmentRequest {

    @NotNull(message = "Driver ID is required")
    @Schema(description = "Driver ID to assign the delivery to", example = "1")
    private Long driverId;

    @Schema(description = "Assignment notes", example = "High priority delivery, handle with care")
    private String notes;

    @Schema(description = "Whether to notify driver immediately", example = "true")
    private Boolean notifyDriver = true;
} 