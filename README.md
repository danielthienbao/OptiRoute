# OptiRoute – Intelligent Route Planner

OptiRoute is a route planning system that combines geolocation data with advanced algorithms to optimize delivery and ride-sharing routes.

## Features

### ✅ Implemented Features

- **Authentication & Authorization**
  - JWT-based authentication with refresh tokens
  - Role-based access control (ADMIN/DRIVER)
  - Secure API endpoints with Spring Security
  - User registration, login, and profile management

- **User Management**
  - Complete user CRUD operations
  - Role-based user management (Admin/Driver)
  - Password change functionality
  - User search and filtering

- **Location Management**
  - PostGIS integration for spatial data storage
  - Location CRUD operations with coordinates
  - Nearby location search with radius-based queries
  - Distance calculation between locations
  - Location type categorization (PICKUP, DELIVERY, WAREHOUSE, etc.)

- **Route Optimization**
  - TSP (Traveling Salesman Problem) algorithm implementation
  - Multiple optimization strategies (Distance-based, Time-based, Load-balanced)
  - Route waypoint management
  - Multi-vehicle route optimization (VRP)

- **Database & Infrastructure**
  - PostgreSQL with PostGIS extension
  - Complete database schema with indexes
  - Sample data for testing
  - JPA repositories with custom queries

### 🚧 In Progress / Next Steps

- **Delivery Request Management**
  - Create, assign, and track delivery requests
  - Real-time status updates
  - Driver assignment algorithms

- **Real-time Updates**
  - WebSocket implementation for live updates
  - Location tracking service
  - ETA calculation and route deviation detection

- **Advanced Route Optimization**
  - Integration with external mapping APIs
  - Traffic-aware routing
  - Dynamic route recalculation

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
│   │   │       │   └── LocationController.java
│   │   │       ├── dto/                 # Data Transfer Objects
│   │   │       │   ├── UserDto.java
│   │   │       │   └── LocationDto.java
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
│   │   │       │   └── RouteOptimizationService.java
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
