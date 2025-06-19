package com.optiroute.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Route waypoint data transfer object")
public class RouteWaypointDto {

    @Schema(description = "Waypoint ID", example = "1")
    private Long id;

    @Schema(description = "Route ID", example = "1")
    private Long routeId;

    @NotNull(message = "Location is required")
    @Schema(description = "Location information")
    private LocationDto location;

    @Schema(description = "Waypoint sequence order", example = "1")
    private Integer sequence;

    @Schema(description = "Estimated arrival time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedArrival;

    @Schema(description = "Actual arrival time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualArrival;

    @Schema(description = "Estimated departure time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedDeparture;

    @Schema(description = "Actual departure time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualDeparture;

    @Schema(description = "Distance from previous waypoint in kilometers", example = "5.2")
    private Double distanceFromPrevious;

    @Schema(description = "Time from previous waypoint in minutes", example = "15")
    private Integer timeFromPrevious;

    @Schema(description = "Waypoint status", example = "PENDING")
    private String status;

    @Schema(description = "Waypoint notes", example = "Customer prefers delivery between 9-11 AM")
    private String notes;

    @Schema(description = "Whether waypoint is completed", example = "false")
    private Boolean completed;

    @Schema(description = "Waypoint priority", example = "1")
    private Integer priority;
} 