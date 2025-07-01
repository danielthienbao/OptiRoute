-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;

-- Create locations table with spatial data
CREATE TABLE IF NOT EXISTS locations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    geom GEOMETRY(POINT, 4326),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create routes table
CREATE TABLE IF NOT EXISTS routes (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    origin_id INTEGER REFERENCES locations(id),
    destination_id INTEGER REFERENCES locations(id),
    total_distance DOUBLE PRECISION,
    total_duration INTEGER, -- in seconds
    waypoints JSONB,
    route_geometry GEOMETRY(LINESTRING, 4326),
    is_round_trip BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create route_waypoints junction table
CREATE TABLE IF NOT EXISTS route_waypoints (
    id SERIAL PRIMARY KEY,
    route_id INTEGER REFERENCES routes(id) ON DELETE CASCADE,
    location_id INTEGER REFERENCES locations(id),
    waypoint_order INTEGER NOT NULL,
    distance_from_previous DOUBLE PRECISION,
    duration_from_previous INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_locations_geom ON locations USING GIST(geom);
CREATE INDEX IF NOT EXISTS idx_locations_lat_lng ON locations(latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_routes_geometry ON routes USING GIST(route_geometry);
CREATE INDEX IF NOT EXISTS idx_route_waypoints_route_id ON route_waypoints(route_id);
CREATE INDEX IF NOT EXISTS idx_route_waypoints_order ON route_waypoints(route_id, waypoint_order);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_locations_updated_at BEFORE UPDATE ON locations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_routes_updated_at BEFORE UPDATE ON routes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert some sample data
INSERT INTO locations (name, address, latitude, longitude, geom) VALUES
('San Francisco City Hall', '1 Dr Carlton B Goodlett Pl, San Francisco, CA 94102', 37.7793, -122.4193, ST_SetSRID(ST_MakePoint(-122.4193, 37.7793), 4326)),
('Golden Gate Bridge', 'Golden Gate Bridge, San Francisco, CA', 37.8199, -122.4783, ST_SetSRID(ST_MakePoint(-122.4783, 37.8199), 4326)),
('Fisherman''s Wharf', 'Fisherman''s Wharf, San Francisco, CA', 37.8080, -122.4177, ST_SetSRID(ST_MakePoint(-122.4177, 37.8080), 4326)),
('Alcatraz Island', 'Alcatraz Island, San Francisco, CA', 37.8270, -122.4230, ST_SetSRID(ST_MakePoint(-122.4230, 37.8270), 4326)),
('Palo Alto Downtown', 'Palo Alto, CA', 37.4419, -122.1430, ST_SetSRID(ST_MakePoint(-122.1430, 37.4419), 4326)),
('San Jose Downtown', 'San Jose, CA', 37.3382, -121.8863, ST_SetSRID(ST_MakePoint(-121.8863, 37.3382), 4326)),
('Los Angeles Downtown', 'Los Angeles, CA', 34.0522, -118.2437, ST_SetSRID(ST_MakePoint(-118.2437, 34.0522), 4326))
ON CONFLICT DO NOTHING; 