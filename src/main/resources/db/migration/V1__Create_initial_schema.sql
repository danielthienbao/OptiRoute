-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;

-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'DRIVER',
    phone_number VARCHAR(20),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create locations table with PostGIS geometry
CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    address VARCHAR(255) NOT NULL,
    postal_code VARCHAR(20),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    coordinates GEOMETRY(Point, 4326),
    location_type VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create routes table
CREATE TABLE routes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    driver_id BIGINT REFERENCES users(id),
    start_location_id BIGINT REFERENCES locations(id),
    end_location_id BIGINT REFERENCES locations(id),
    total_distance DOUBLE PRECISION,
    estimated_duration INTEGER,
    actual_duration INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    optimization_strategy VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP
);

-- Create route_waypoints table
CREATE TABLE route_waypoints (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    location_id BIGINT NOT NULL REFERENCES locations(id),
    waypoint_order INTEGER NOT NULL,
    estimated_arrival TIMESTAMP,
    actual_arrival TIMESTAMP,
    estimated_departure TIMESTAMP,
    actual_departure TIMESTAMP,
    distance_from_previous DOUBLE PRECISION,
    duration_from_previous INTEGER,
    status VARCHAR(20) DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create delivery_requests table
CREATE TABLE delivery_requests (
    id BIGSERIAL PRIMARY KEY,
    request_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT REFERENCES users(id),
    driver_id BIGINT REFERENCES users(id),
    pickup_location_id BIGINT NOT NULL REFERENCES locations(id),
    delivery_location_id BIGINT NOT NULL REFERENCES locations(id),
    pickup_time TIMESTAMP,
    delivery_time TIMESTAMP,
    requested_pickup_time TIMESTAMP,
    requested_delivery_time TIMESTAMP,
    package_weight DOUBLE PRECISION,
    package_dimensions VARCHAR(50),
    package_description TEXT,
    special_instructions TEXT,
    estimated_distance DOUBLE PRECISION,
    estimated_duration INTEGER,
    actual_distance DOUBLE PRECISION,
    actual_duration INTEGER,
    base_fare DECIMAL(10,2),
    distance_fare DECIMAL(10,2),
    total_fare DECIMAL(10,2),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(20) DEFAULT 'NORMAL',
    is_urgent BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_at TIMESTAMP,
    picked_up_at TIMESTAMP,
    delivered_at TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(is_active);

CREATE INDEX idx_locations_coordinates ON locations USING GIST(coordinates);
CREATE INDEX idx_locations_type ON locations(location_type);
CREATE INDEX idx_locations_city ON locations(city);
CREATE INDEX idx_locations_active ON locations(is_active);

CREATE INDEX idx_routes_driver ON routes(driver_id);
CREATE INDEX idx_routes_status ON routes(status);
CREATE INDEX idx_routes_created_at ON routes(created_at);

CREATE INDEX idx_route_waypoints_route ON route_waypoints(route_id);
CREATE INDEX idx_route_waypoints_order ON route_waypoints(route_id, waypoint_order);

CREATE INDEX idx_delivery_requests_number ON delivery_requests(request_number);
CREATE INDEX idx_delivery_requests_customer ON delivery_requests(customer_id);
CREATE INDEX idx_delivery_requests_driver ON delivery_requests(driver_id);
CREATE INDEX idx_delivery_requests_status ON delivery_requests(status);
CREATE INDEX idx_delivery_requests_priority ON delivery_requests(priority);
CREATE INDEX idx_delivery_requests_urgent ON delivery_requests(is_urgent);
CREATE INDEX idx_delivery_requests_created_at ON delivery_requests(created_at);

-- Create spatial indexes for PostGIS
CREATE INDEX idx_locations_spatial ON locations USING GIST(coordinates);

-- Insert sample data
INSERT INTO users (username, email, password, first_name, last_name, role, phone_number) VALUES
('admin', 'admin@optiroute.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Admin', 'User', 'ADMIN', '+1234567890'),
('driver1', 'driver1@optiroute.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'John', 'Driver', 'DRIVER', '+1234567891'),
('driver2', 'driver2@optiroute.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Jane', 'Driver', 'DRIVER', '+1234567892');

-- Insert sample locations (New York City area)
INSERT INTO locations (name, description, address, postal_code, city, state, country, coordinates, location_type) VALUES
('Central Park', 'Famous urban park in Manhattan', 'Central Park, New York, NY', '10024', 'New York', 'NY', 'USA', ST_SetSRID(ST_MakePoint(-73.9654, 40.7829), 4326), 'PICKUP'),
('Times Square', 'Major commercial intersection', 'Times Square, New York, NY', '10036', 'New York', 'NY', 'USA', ST_SetSRID(ST_MakePoint(-73.9855, 40.7580), 4326), 'DELIVERY'),
('Brooklyn Bridge', 'Historic bridge connecting Manhattan and Brooklyn', 'Brooklyn Bridge, New York, NY', '10038', 'New York', 'NY', 'USA', ST_SetSRID(ST_MakePoint(-73.9969, 40.7061), 4326), 'PICKUP'),
('Empire State Building', 'Iconic skyscraper', '350 5th Ave, New York, NY', '10118', 'New York', 'NY', 'USA', ST_SetSRID(ST_MakePoint(-73.9857, 40.7484), 4326), 'DELIVERY'),
('Statue of Liberty', 'Famous landmark', 'Liberty Island, New York, NY', '10004', 'New York', 'NY', 'USA', ST_SetSRID(ST_MakePoint(-74.0445, 40.6892), 4326), 'PICKUP'),
('Metropolitan Museum', 'Art museum', '1000 5th Ave, New York, NY', '10028', 'New York', 'NY', 'USA', ST_SetSRID(ST_MakePoint(-73.9632, 40.7794), 4326), 'DELIVERY'); 