package com.optiroute.dto;

import com.optiroute.model.OptimizationStrategy;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Route optimization request")
public class RouteOptimizationRequest {

    @Schema(description = "Route ID to optimize", example = "1")
    private Long routeId;

    @NotNull(message = "Optimization strategy is required")
    @Schema(description = "Optimization strategy to use", example = "DISTANCE_BASED")
    private OptimizationStrategy strategy;

    @Schema(description = "List of location IDs to include in optimization")
    @Size(min = 2, message = "At least 2 locations are required for optimization")
    private List<Long> locationIds;

    @Schema(description = "Maximum route distance in kilometers", example = "100.0")
    private Double maxDistance;

    @Schema(description = "Maximum route time in minutes", example = "480")
    private Integer maxTime;

    @Schema(description = "Vehicle capacity constraint", example = "1000")
    private Double capacityConstraint;

    @Schema(description = "Time window constraints")
    private List<TimeWindowConstraint> timeWindows;

    @Schema(description = "Priority weights for optimization", example = "{\"distance\": 0.4, \"time\": 0.3, \"cost\": 0.3}")
    private OptimizationWeights weights;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Time window constraint")
    public static class TimeWindowConstraint {
        @Schema(description = "Location ID", example = "1")
        private Long locationId;

        @Schema(description = "Earliest arrival time", example = "09:00")
        private String earliestArrival;

        @Schema(description = "Latest arrival time", example = "17:00")
        private String latestArrival;

        @Schema(description = "Service time in minutes", example = "30")
        private Integer serviceTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Optimization weights")
    public static class OptimizationWeights {
        @Schema(description = "Distance weight", example = "0.4")
        private Double distance;

        @Schema(description = "Time weight", example = "0.3")
        private Double time;

        @Schema(description = "Cost weight", example = "0.3")
        private Double cost;

        @Schema(description = "Priority weight", example = "0.2")
        private Double priority;
    }
} 