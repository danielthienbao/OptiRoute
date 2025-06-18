package com.optiroute.repository;

import com.optiroute.model.DeliveryRequest;
import com.optiroute.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeliveryRequestRepository extends JpaRepository<DeliveryRequest, Long> {
    
    DeliveryRequest findByRequestNumber(String requestNumber);
    
    List<DeliveryRequest> findByCustomer(User customer);
    
    List<DeliveryRequest> findByDriver(User driver);
    
    List<DeliveryRequest> findByStatus(DeliveryRequest.DeliveryStatus status);
    
    List<DeliveryRequest> findByPriority(DeliveryRequest.Priority priority);
    
    List<DeliveryRequest> findByIsUrgent(Boolean isUrgent);
    
    @Query("SELECT dr FROM DeliveryRequest dr WHERE dr.customer = :customer AND dr.status IN ('PENDING', 'ASSIGNED', 'PICKED_UP', 'IN_TRANSIT')")
    List<DeliveryRequest> findActiveRequestsByCustomer(@Param("customer") User customer);
    
    @Query("SELECT dr FROM DeliveryRequest dr WHERE dr.driver = :driver AND dr.status IN ('ASSIGNED', 'PICKED_UP', 'IN_TRANSIT')")
    List<DeliveryRequest> findActiveRequestsByDriver(@Param("driver") User driver);
    
    @Query("SELECT dr FROM DeliveryRequest dr WHERE dr.status = :status AND dr.createdAt >= :startDate")
    List<DeliveryRequest> findRequestsByStatusAndDateRange(@Param("status") DeliveryRequest.DeliveryStatus status, 
                                                          @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT dr FROM DeliveryRequest dr WHERE dr.driver = :driver AND dr.createdAt BETWEEN :startDate AND :endDate")
    List<DeliveryRequest> findRequestsByDriverAndDateRange(@Param("driver") User driver, 
                                                          @Param("startDate") LocalDateTime startDate, 
                                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT dr FROM DeliveryRequest dr WHERE dr.totalFare BETWEEN :minFare AND :maxFare")
    List<DeliveryRequest> findRequestsByFareRange(@Param("minFare") BigDecimal minFare, 
                                                 @Param("maxFare") BigDecimal maxFare);
    
    @Query("SELECT dr FROM DeliveryRequest dr WHERE dr.estimatedDistance BETWEEN :minDistance AND :maxDistance")
    List<DeliveryRequest> findRequestsByDistanceRange(@Param("minDistance") Double minDistance, 
                                                     @Param("maxDistance") Double maxDistance);
    
    @Query("SELECT dr FROM DeliveryRequest dr WHERE dr.packageWeight BETWEEN :minWeight AND :maxWeight")
    List<DeliveryRequest> findRequestsByWeightRange(@Param("minWeight") Double minWeight, 
                                                   @Param("maxWeight") Double maxWeight);
    
    @Query("SELECT dr FROM DeliveryRequest dr WHERE dr.requestNumber LIKE %:searchTerm% OR dr.packageDescription LIKE %:searchTerm%")
    List<DeliveryRequest> searchRequests(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT dr FROM DeliveryRequest dr WHERE dr.status = 'PENDING' AND dr.isUrgent = true ORDER BY dr.createdAt ASC")
    List<DeliveryRequest> findUrgentPendingRequests();
    
    @Query("SELECT dr FROM DeliveryRequest dr WHERE dr.status = 'PENDING' ORDER BY dr.priority DESC, dr.createdAt ASC")
    Page<DeliveryRequest> findPendingRequestsOrderedByPriority(Pageable pageable);
    
    @Query("SELECT dr FROM DeliveryRequest dr WHERE dr.status = 'COMPLETED' AND dr.deliveredAt >= :startDate ORDER BY dr.deliveredAt DESC")
    List<DeliveryRequest> findCompletedRequestsSince(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(dr) FROM DeliveryRequest dr WHERE dr.driver = :driver AND dr.status = 'DELIVERED' AND dr.deliveredAt >= :startDate")
    Long countCompletedDeliveriesByDriverSince(@Param("driver") User driver, 
                                              @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT AVG(dr.totalFare) FROM DeliveryRequest dr WHERE dr.driver = :driver AND dr.status = 'DELIVERED' AND dr.deliveredAt >= :startDate")
    BigDecimal getAverageFareByDriverSince(@Param("driver") User driver, 
                                          @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT SUM(dr.totalFare) FROM DeliveryRequest dr WHERE dr.driver = :driver AND dr.status = 'DELIVERED' AND dr.deliveredAt >= :startDate")
    BigDecimal getTotalEarningsByDriverSince(@Param("driver") User driver, 
                                            @Param("startDate") LocalDateTime startDate);
} 