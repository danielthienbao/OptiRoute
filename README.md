# OptiRoute – Intelligent Route Planner

OptiRoute is a route planning system that combines geolocation data with advanced algorithms to optimize delivery and ride-sharing routes.

## Features

- **Authentication & Authorization**
  - JWT-based authentication
  - Role-based access (Admin/Driver)
  - Secure API endpoints

- **Route Optimization**
  - TSP/VRP algorithms for optimal routing
  - Real-time route updates
  - Multi-stop optimization

- **Geolocation & Mapping**
  - PostGIS integration for spatial data
  - Precise location tracking
  - Route visualization

## Tech Stack

- **Backend**: Spring Boot, Spring Security
- **Database**: PostgreSQL + PostGIS
- **Authentication**: JWT
- **API Documentation**: Swagger/OpenAPI
- **Testing**: JUnit, Mockito

## Project Structure

```
optiroute/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/optiroute/
│   │   │       ├── config/         # Configuration classes
│   │   │       ├── controller/     # REST controllers
│   │   │       ├── model/          # Entity classes
│   │   │       ├── repository/     # Data repositories
│   │   │       ├── service/        # Business logic
│   │   │       ├── security/       # Security configuration
│   │   │       └── util/           # Utility classes
│   │   └── resources/
│   │       ├── application.yml     # Application configuration
│   │       └── db/                 # Database migrations
│   └── test/                       # Test classes
├── docs/                           # Documentation
└── pom.xml                         # Maven configuration
```

## Getting Started

1. Clone the repository
2. Set up PostgreSQL with PostGIS extension
3. Configure application.yml with your database credentials
4. Run `mvn spring-boot:run`

## API Documentation

API documentation is available at `/swagger-ui.html` when running the application.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
