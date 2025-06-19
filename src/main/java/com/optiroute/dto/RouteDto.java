package com.optiroute.dto;

import com.optiroute.model.RouteStatus;
import com.optiroute.model.OptimizationStrategy;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Route data transfer object")
public class RouteDto {

    @Schema(description = "Route ID", example = "1")
    private Long id;

    @NotBlank(message = "Route name is required")
    @Size(min = 1, max = 255, message = "Route name must be between 1 and 255 characters")
    @Schema(description = "Route name", example = "Downtown Delivery Route")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Route description", example = "Daily delivery route for downtown area")
    private String description;

    @NotNull(message = "Route status is required")
    @Schema(description = "Current route status", example = "PLANNED")
    private RouteStatus status;

    @Schema(description = "Assigned driver ID", example = "1")
    private Long driverId;

    @Schema(description = "Driver information")
    private UserDto driver;

    @Schema(description = "Optimization strategy used", example = "DISTANCE_BASED")
    private OptimizationStrategy optimizationStrategy;

    @Schema(description = "Total route distance in kilometers", example = "25.5")
    private Double totalDistance;

    @Schema(description = "Estimated total time in minutes", example = "120")
    private Integer estimatedTime;

    @Schema(description = "Total fuel consumption in liters", example = "8.5")
    private Double fuelConsumption;

    @Schema(description = "Route waypoints")
    private List<RouteWaypointDto> waypoints;

    @Schema(description = "Route start location")
    private LocationDto startLocation;

    @Schema(description = "Route end location")
    private LocationDto endLocation;

    @Schema(description = "Route creation timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Route last update timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "Route start time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "Route end time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "Route priority level", example = "1")
    private Integer priority;

    @Schema(description = "Route notes", example = "Handle with care - fragile items")
    private String notes;

    @Schema(description = "Whether route is active", example = "true")
    private Boolean active;

    @Schema(description = "Route tags for categorization", example = "[\"urgent\", \"fragile\"]")
    private List<String> tags;
} 