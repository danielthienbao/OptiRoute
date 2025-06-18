package com.optiroute.config;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeometryConfig {
    
    @Bean
    public GeometryFactory geometryFactory() {
        // Use SRID 4326 (WGS84) for GPS coordinates
        return new GeometryFactory(new PrecisionModel(), 4326);
    }
} 