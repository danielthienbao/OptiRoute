package com.optiroute.repository;

import com.optiroute.model.Location;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    List<Location> findByLocationType(Location.LocationType locationType);
    
    List<Location> findByIsActive(Boolean isActive);
    
    List<Location> findByCity(String city);
    
    List<Location> findByCityAndLocationType(String city, Location.LocationType locationType);
    
    @Query("SELECT l FROM Location l WHERE l.name LIKE %:searchTerm% OR l.address LIKE %:searchTerm% OR l.city LIKE %:searchTerm%")
    List<Location> searchLocations(@Param("searchTerm") String searchTerm);
    
    // PostGIS spatial queries
    @Query(value = "SELECT l.*, ST_Distance(l.coordinates, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) as distance " +
            "FROM locations l " +
            "WHERE ST_DWithin(l.coordinates, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :radiusKm * 1000) " +
            "AND l.is_active = true " +
            "ORDER BY distance", nativeQuery = true)
    List<Location> findNearbyLocations(@Param("latitude") Double latitude, 
                                      @Param("longitude") Double longitude, 
                                      @Param("radiusKm") Double radiusKm);
    
    @Query(value = "SELECT l.*, ST_Distance(l.coordinates, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) as distance " +
            "FROM locations l " +
            "WHERE ST_DWithin(l.coordinates, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :radiusKm * 1000) " +
            "AND l.location_type = :locationType " +
            "AND l.is_active = true " +
            "ORDER BY distance " +
            "LIMIT :limit", nativeQuery = true)
    List<Location> findNearbyLocationsByType(@Param("latitude") Double latitude, 
                                            @Param("longitude") Double longitude, 
                                            @Param("radiusKm") Double radiusKm,
                                            @Param("locationType") String locationType,
                                            @Param("limit") Integer limit);
    
    @Query(value = "SELECT ST_Distance(l1.coordinates, l2.coordinates) / 1000 as distance_km " +
            "FROM locations l1, locations l2 " +
            "WHERE l1.id = :location1Id AND l2.id = :location2Id", nativeQuery = true)
    Double calculateDistanceBetweenLocations(@Param("location1Id") Long location1Id, 
                                           @Param("location2Id") Long location2Id);
    
    @Query(value = "SELECT ST_Distance(ST_SetSRID(ST_MakePoint(:lon1, :lat1), 4326), ST_SetSRID(ST_MakePoint(:lon2, :lat2), 4326)) / 1000 as distance_km", 
            nativeQuery = true)
    Double calculateDistanceBetweenCoordinates(@Param("lat1") Double lat1, 
                                             @Param("lon1") Double lon1, 
                                             @Param("lat2") Double lat2, 
                                             @Param("lon2") Double lon2);
    
    @Query("SELECT l FROM Location l WHERE l.isActive = true ORDER BY l.name")
    Page<Location> findAllActive(Pageable pageable);
} 