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
@Schema(description = "Route assignment request")
public class RouteAssignmentRequest {

    @NotNull(message = "Driver ID is required")
    @Schema(description = "Driver ID to assign the route to", example = "1")
    private Long driverId;

    @Schema(description = "Assignment notes", example = "High priority delivery route")
    private String notes;

    @Schema(description = "Whether to notify driver immediately", example = "true")
    private Boolean notifyDriver;
} 