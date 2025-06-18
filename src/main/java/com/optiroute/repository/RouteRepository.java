package com.optiroute.repository;

import com.optiroute.model.Route;
import com.optiroute.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    List<Route> findByDriver(User driver);
    
    List<Route> findByStatus(Route.RouteStatus status);
    
    List<Route> findByDriverAndStatus(User driver, Route.RouteStatus status);
    
    List<Route> findByOptimizationStrategy(Route.OptimizationStrategy strategy);
    
    @Query("SELECT r FROM Route r WHERE r.driver = :driver AND r.status IN ('PLANNED', 'IN_PROGRESS')")
    List<Route> findActiveRoutesByDriver(@Param("driver") User driver);
    
    @Query("SELECT r FROM Route r WHERE r.status = :status AND r.createdAt >= :startDate")
    List<Route> findRoutesByStatusAndDateRange(@Param("status") Route.RouteStatus status, 
                                              @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT r FROM Route r WHERE r.driver = :driver AND r.createdAt BETWEEN :startDate AND :endDate")
    List<Route> findRoutesByDriverAndDateRange(@Param("driver") User driver, 
                                              @Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT r FROM Route r WHERE r.totalDistance BETWEEN :minDistance AND :maxDistance")
    List<Route> findRoutesByDistanceRange(@Param("minDistance") Double minDistance, 
                                         @Param("maxDistance") Double maxDistance);
    
    @Query("SELECT r FROM Route r WHERE r.estimatedDuration BETWEEN :minDuration AND :maxDuration")
    List<Route> findRoutesByDurationRange(@Param("minDuration") Integer minDuration, 
                                         @Param("maxDuration") Integer maxDuration);
    
    @Query("SELECT r FROM Route r WHERE r.name LIKE %:searchTerm% OR r.description LIKE %:searchTerm%")
    List<Route> searchRoutes(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT r FROM Route r WHERE r.status = 'IN_PROGRESS' ORDER BY r.startedAt DESC")
    Page<Route> findActiveRoutes(Pageable pageable);
    
    @Query("SELECT r FROM Route r WHERE r.status = 'COMPLETED' AND r.completedAt >= :startDate ORDER BY r.completedAt DESC")
    List<Route> findCompletedRoutesSince(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(r) FROM Route r WHERE r.driver = :driver AND r.status = 'COMPLETED' AND r.completedAt >= :startDate")
    Long countCompletedRoutesByDriverSince(@Param("driver") User driver, 
                                          @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT AVG(r.totalDistance) FROM Route r WHERE r.driver = :driver AND r.status = 'COMPLETED' AND r.completedAt >= :startDate")
    Double getAverageDistanceByDriverSince(@Param("driver") User driver, 
                                          @Param("startDate") LocalDateTime startDate);
} 