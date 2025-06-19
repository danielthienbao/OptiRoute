# OptiRoute – Intelligent Route Planner

OptiRoute is a route planning system that combines geolocation data with advanced algorithms to optimize delivery and ride-sharing routes.

## Features

### ✅ Implemented Features

- **Authentication & Authorization**
  - JWT-based authentication with refresh tokens
  - Role-based access control (ADMIN/DRIVER)
  - Secure API endpoints with Spring Security
  - User registration, login, and profile management
  - JWT token generation, validation, and filtering

- **User Management**
  - Complete user CRUD operations with pagination and filtering
  - Role-based user management (Admin/Driver)
  - Password change and reset functionality
  - User search and filtering by role, status, and search terms
  - User activation/deactivation
  - User statistics and dashboard data
  - Available drivers identification

- **Location Management**
  - PostGIS integration for spatial data storage
  - Location CRUD operations with coordinates
  - Nearby location search with radius-based queries
  - Distance calculation between locations
  - Location type categorization (PICKUP, DELIVERY, WAREHOUSE, etc.)

- **Route Management**
  - Complete route CRUD operations with pagination
  - Route optimization using TSP algorithms
  - Multiple optimization strategies (Distance-based, Time-based, Load-balanced)
  - Route waypoint management and reordering
  - Driver assignment to routes
  - Route status tracking (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED)
  - Batch route optimization
  - Route filtering by status, driver, and other criteria

- **Delivery Request Management**
  - Complete delivery request CRUD operations
  - Delivery status tracking (PENDING, IN_PROGRESS, COMPLETED, FAILED)
  - Driver assignment algorithms
  - Real-time status updates
  - Package tracking with unique tracking numbers
  - Delivery scheduling and rescheduling
  - Urgent and overdue delivery identification
  - Delivery statistics and analytics
  - Signature capture and delivery confirmation

- **Database & Infrastructure**
  - PostgreSQL with PostGIS extension
  - Complete database schema with indexes
  - Sample data for testing
  - JPA repositories with custom queries
  - Spatial indexing for efficient location queries

### 🚧 In Progress / Next Steps

- **Real-time Updates**
  - WebSocket implementation for live updates
  - Location tracking service
  - ETA calculation and route deviation detection

- **Advanced Route Optimization**
  - Integration with external mapping APIs
  - Traffic-aware routing
  - Dynamic route recalculation

- **Notification System**
  - Email service integration
  - SMS service integration
  - Push notification service

## Tech Stack

- **Backend**: Spring Boot 3.2.3, Spring Security, Spring Data JPA
- **Database**: PostgreSQL 15+ with PostGIS extension
- **Authentication**: JWT (JSON Web Tokens)
- **Spatial Data**: JTS (Java Topology Suite), PostGIS
- **API Documentation**: Swagger/OpenAPI 3
- **Testing**: JUnit, Mockito
- **Build Tool**: Maven
- **Java Version**: 17

## Project Structure

```
optiroute/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/optiroute/
│   │   │       ├── config/              # Configuration classes
│   │   │       │   ├── ApplicationConfig.java
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── GeometryConfig.java
│   │   │       ├── controller/          # REST controllers
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── LocationController.java
│   │   │       │   ├── RouteController.java
│   │   │       │   ├── UserController.java
│   │   │       │   └── DeliveryController.java
│   │   │       ├── dto/                 # Data Transfer Objects
│   │   │       │   ├── UserDto.java
│   │   │       │   ├── LocationDto.java
│   │   │       │   ├── RouteDto.java
│   │   │       │   ├── RouteWaypointDto.java
│   │   │       │   ├── RouteOptimizationRequest.java
│   │   │       │   ├── RouteAssignmentRequest.java
│   │   │       │   ├── DeliveryRequestDto.java
│   │   │       │   ├── DeliveryCreateRequest.java
│   │   │       │   ├── DeliveryUpdateRequest.java
│   │   │       │   ├── DeliveryAssignmentRequest.java
│   │   │       │   ├── UserCreateRequest.java
│   │   │       │   └── UserUpdateRequest.java
│   │   │       ├── model/               # Entity classes
│   │   │       │   ├── User.java
│   │   │       │   ├── Location.java
│   │   │       │   ├── Route.java
│   │   │       │   ├── RouteWaypoint.java
│   │   │       │   └── DeliveryRequest.java
│   │   │       ├── repository/          # Data repositories
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── LocationRepository.java
│   │   │       │   ├── RouteRepository.java
│   │   │       │   └── DeliveryRequestRepository.java
│   │   │       ├── service/             # Business logic
│   │   │       │   ├── UserService.java
│   │   │       │   ├── LocationService.java
│   │   │       │   ├── RouteService.java
│   │   │       │   ├── RouteOptimizationService.java
│   │   │       │   └── DeliveryService.java
│   │   │       ├── security/            # Security configuration
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   └── JwtService.java
│   │   │       └── exception/           # Exception handling
│   │   │           ├── ErrorResponse.java
│   │   │           ├── GlobalExceptionHandler.java
│   │   │           └── ResourceNotFoundException.java
│   │   └── resources/
│   │       ├── application.yml          # Application configuration
│   │       └── db/
│   │           └── migration/
│   │               └── V1__Create_initial_schema.sql
│   └── test/                            # Test classes
├── docs/                               # Documentation
└── pom.xml                             # Maven configuration
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 15+ with PostGIS extension
- Git

### Database Setup

1. **Install PostgreSQL and PostGIS**
   ```bash
   # Ubuntu/Debian
   sudo apt-get install postgresql postgresql-contrib postgis
   
   # macOS with Homebrew
   brew install postgresql postgis
   
   # Windows: Download from https://www.postgresql.org/download/windows/
   ```

2. **Create Database**
   ```sql
   CREATE DATABASE optiroute;
   CREATE EXTENSION postgis;
   ```

3. **Run Migration**
   ```bash
   # The application will automatically run the migration on startup
   # Or manually execute the SQL file in src/main/resources/db/migration/
   ```

### Application Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/OptiRoute.git
   cd OptiRoute
   ```

2. **Configure database connection**
   Edit `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/optiroute
       username: your_username
       password: your_password
   ```

3. **Build and run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - API Documentation: `http://localhost:8080/api-docs`

### Default Users

The application comes with sample users:
- **Admin**: `admin` / `admin`
- **Driver 1**: `driver1` / `driver1`
- **Driver 2**: `driver2` / `driver2`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - User logout
- `GET /api/auth/me` - Get current user
- `PUT /api/auth/me` - Update current user
- `POST /api/auth/change-password` - Change password

### User Management (Admin Only)
- `GET /api/users` - Get all users (paginated with filters)
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `PUT /api/users/{id}/activate` - Activate user
- `PUT /api/users/{id}/deactivate` - Deactivate user
- `PUT /api/users/{id}/role` - Change user role
- `GET /api/users/search` - Search users
- `GET /api/users/role/{role}` - Get users by role
- `GET /api/users/active` - Get active users
- `GET /api/users/inactive` - Get inactive users
- `POST /api/users/{id}/reset-password` - Reset user password
- `GET /api/users/drivers` - Get all drivers
- `GET /api/users/drivers/available` - Get available drivers
- `GET /api/users/stats` - Get user statistics

### Locations
- `GET /api/locations` - Get all locations (paginated)
- `GET /api/locations/{id}` - Get location by ID
- `POST /api/locations` - Create new location
- `PUT /api/locations/{id}` - Update location
- `DELETE /api/locations/{id}` - Delete location
- `GET /api/locations/type/{type}` - Get locations by type
- `GET /api/locations/city/{city}` - Get locations by city
- `GET /api/locations/search?query={query}` - Search locations
- `POST /api/locations/nearby` - Find nearby locations
- `POST /api/locations/distance` - Calculate distance

### Route Management
- `GET /api/routes` - Get all routes (paginated with filters)
- `GET /api/routes/{id}` - Get route by ID
- `POST /api/routes` - Create new route
- `PUT /api/routes/{id}` - Update route
- `DELETE /api/routes/{id}` - Delete route
- `POST /api/routes/optimize` - Optimize route
- `POST /api/routes/{id}/assign` - Assign route to driver
- `PUT /api/routes/{id}/status` - Update route status
- `GET /api/routes/driver/{driverId}` - Get routes by driver
- `GET /api/routes/status/{status}` - Get routes by status
- `POST /api/routes/batch-optimize` - Batch optimize routes
- `GET /api/routes/{id}/waypoints` - Get route with waypoints
- `POST /api/routes/{id}/waypoints/reorder` - Reorder waypoints

### Delivery Management
- `GET /api/deliveries` - Get all deliveries (paginated with filters)
- `GET /api/deliveries/{id}` - Get delivery by ID
- `POST /api/deliveries` - Create new delivery request
- `PUT /api/deliveries/{id}` - Update delivery request
- `DELETE /api/deliveries/{id}` - Delete delivery request
- `POST /api/deliveries/{id}/assign` - Assign delivery to driver
- `PUT /api/deliveries/{id}/status` - Update delivery status
- `GET /api/deliveries/driver/{driverId}` - Get deliveries by driver
- `GET /api/deliveries/status/{status}` - Get deliveries by status
- `GET /api/deliveries/customer/{customerId}` - Get deliveries by customer
- `POST /api/deliveries/{id}/pickup` - Mark as picked up
- `POST /api/deliveries/{id}/delivered` - Mark as delivered
- `POST /api/deliveries/{id}/failed` - Mark as failed
- `GET /api/deliveries/urgent` - Get urgent deliveries
- `GET /api/deliveries/overdue` - Get overdue deliveries
- `POST /api/deliveries/{id}/reschedule` - Reschedule delivery
- `GET /api/deliveries/stats` - Get delivery statistics

## Database Schema

### Core Tables
- **users** - User accounts and authentication
- **locations** - Geographic locations with PostGIS coordinates
- **routes** - Optimized routes with waypoints
- **route_waypoints** - Individual stops along routes
- **delivery_requests** - Delivery requests and tracking

### Key Features
- Spatial indexing for efficient location queries
- Foreign key relationships for data integrity
- Timestamp tracking for audit trails
- Soft delete functionality
- Comprehensive status tracking for routes and deliveries

## Development

### Running Tests
```bash
mvn test
```

### Code Style
The project uses Lombok for reducing boilerplate code and follows Spring Boot conventions.

### Adding New Features
1. Create entity classes in `model/` package
2. Create DTOs in `dto/` package
3. Create repository interfaces in `repository/` package
4. Implement business logic in `service/` package
5. Create REST controllers in `controller/` package
6. Add appropriate tests

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:
- Email: support@optiroute.com
- Issues: [GitHub Issues](https://github.com/yourusername/OptiRoute/issues)
- Documentation: [API Docs](http://localhost:8080/swagger-ui.html)
