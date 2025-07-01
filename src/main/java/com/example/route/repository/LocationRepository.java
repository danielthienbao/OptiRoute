package com.example.route.repository;

import com.example.route.entity.Location;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    // Find locations by name (case-insensitive)
    List<Location> findByNameContainingIgnoreCase(String name);
    
    // Find locations by address (case-insensitive)
    List<Location> findByAddressContainingIgnoreCase(String address);
    
    // Find locations within a certain distance from a point
    @Query(value = "SELECT * FROM locations WHERE ST_DWithin(geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :distanceInMeters)", nativeQuery = true)
    List<Location> findLocationsWithinDistance(@Param("latitude") double latitude, 
                                              @Param("longitude") double longitude, 
                                              @Param("distanceInMeters") double distanceInMeters);
    
    // Find the nearest location to a point
    @Query(value = "SELECT * FROM locations ORDER BY ST_Distance(geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) LIMIT 1", nativeQuery = true)
    Location findNearestLocation(@Param("latitude") double latitude, @Param("longitude") double longitude);
    
    // Find locations within a bounding box
    @Query(value = "SELECT * FROM locations WHERE ST_Within(geom, ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326))", nativeQuery = true)
    List<Location> findLocationsInBoundingBox(@Param("minLat") double minLat, 
                                             @Param("minLon") double minLon, 
                                             @Param("maxLat") double maxLat, 
                                             @Param("maxLon") double maxLon);
    
    // Find locations along a route (within buffer distance)
    @Query(value = "SELECT * FROM locations WHERE ST_DWithin(geom, ST_GeomFromText(:routeLineString, 4326), :bufferDistance)", nativeQuery = true)
    List<Location> findLocationsAlongRoute(@Param("routeLineString") String routeLineString, 
                                          @Param("bufferDistance") double bufferDistance);
} 