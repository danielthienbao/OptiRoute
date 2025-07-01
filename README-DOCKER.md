# OptiRoute with PostgreSQL + PostGIS + Docker

This guide shows how to use OptiRoute with PostgreSQL and PostGIS for spatial data storage and advanced location queries.

## 🐳 Quick Start with Docker

### 1. Start the Database

```bash
# Start PostgreSQL with PostGIS
docker-compose up -d postgres

# Check if database is running
docker-compose ps
```

### 2. Access pgAdmin (Optional)

```bash
# Start pgAdmin
docker-compose up -d pgadmin
```

Then visit: http://localhost:8081
- Email: `admin@optiroute.com`
- Password: `admin123`

### 3. Run the Application

```bash
# Start the Spring Boot application
.\mvnw.cmd spring-boot:run
```

Visit: http://localhost:8080

## 📊 Database Schema

### Tables Created:

1. **locations** - Stores location data with spatial geometry
2. **routes** - Stores route information with line geometry
3. **route_waypoints** - Junction table for route waypoints

### Sample Data Included:
- San Francisco landmarks
- Bay Area cities
- Los Angeles

## 🔍 Spatial Query Examples

### Find Locations Near a Point

```sql
-- Find locations within 5km of San Francisco
SELECT name, address, 
       ST_Distance(geom, ST_SetSRID(ST_MakePoint(-122.4193, 37.7793), 4326)) as distance_meters
FROM locations 
WHERE ST_DWithin(geom, ST_SetSRID(ST_MakePoint(-122.4193, 37.7793), 4326), 5000)
ORDER BY distance_meters;
```

### Find Nearest Location

```sql
-- Find the nearest location to coordinates
SELECT name, address,
       ST_Distance(geom, ST_SetSRID(ST_MakePoint(-122.4193, 37.7793), 4326)) as distance_meters
FROM locations 
ORDER BY geom <-> ST_SetSRID(ST_MakePoint(-122.4193, 37.7793), 4326)
LIMIT 1;
```

### Find Locations in Bounding Box

```sql
-- Find locations in San Francisco area
SELECT name, address
FROM locations 
WHERE ST_Within(geom, ST_MakeEnvelope(-122.5, 37.7, -122.4, 37.8, 4326));
```

## 🚀 Advanced Features

### 1. Spatial Indexing
All spatial queries use GIST indexes for optimal performance.

### 2. Route Geometry Storage
Routes are stored as LineString geometries for spatial analysis.

### 3. Distance Calculations
PostGIS provides accurate distance calculations using geodesic formulas.

### 4. Spatial Joins
Perform complex spatial operations between routes and locations.

## 🔧 Configuration

### Database Connection
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/optiroute
spring.datasource.username=optiroute_user
spring.datasource.password=optiroute_password
```

### PostGIS Dialect
```properties
spring.jpa.properties.hibernate.types.spatial.dialect=org.hibernate.spatial.dialect.postgis.PostgisDialect
```

## 📈 Performance Tips

1. **Use Spatial Indexes**: All geometry columns are automatically indexed
2. **Limit Result Sets**: Use LIMIT clauses for large datasets
3. **Use Bounding Box Queries**: Filter by bounding box before distance calculations
4. **Batch Operations**: Use batch inserts for large datasets

## 🛠️ Development Commands

```bash
# View database logs
docker-compose logs postgres

# Connect to database directly
docker exec -it optiroute-postgres psql -U optiroute_user -d optiroute

# Reset database
docker-compose down -v
docker-compose up -d postgres

# Backup database
docker exec optiroute-postgres pg_dump -U optiroute_user optiroute > backup.sql

# Restore database
docker exec -i optiroute-postgres psql -U optiroute_user -d optiroute < backup.sql
```

## 🔍 Useful PostGIS Functions

```sql
-- Calculate distance between two points
SELECT ST_Distance(
    ST_SetSRID(ST_MakePoint(-122.4193, 37.7793), 4326),
    ST_SetSRID(ST_MakePoint(-122.1430, 37.4419), 4326)
);

-- Create a buffer around a point
SELECT ST_Buffer(ST_SetSRID(ST_MakePoint(-122.4193, 37.7793), 4326), 1000);

-- Check if point is within polygon
SELECT ST_Contains(
    ST_GeomFromText('POLYGON((-122.5 37.7, -122.4 37.7, -122.4 37.8, -122.5 37.8, -122.5 37.7))', 4326),
    ST_SetSRID(ST_MakePoint(-122.4193, 37.7793), 4326)
);
```

## 🚨 Troubleshooting

### Common Issues:

1. **Connection Refused**: Make sure Docker containers are running
2. **Authentication Failed**: Check username/password in application.properties
3. **PostGIS Extension Not Found**: Database initialization script should handle this
4. **Port Already in Use**: Change ports in docker-compose.yml

### Debug Commands:

```bash
# Check container status
docker-compose ps

# View container logs
docker-compose logs postgres

# Check database connectivity
docker exec optiroute-postgres pg_isready -U optiroute_user -d optiroute

# List all tables
docker exec -it optiroute-postgres psql -U optiroute_user -d optiroute -c "\dt"
```

## 📚 Next Steps

1. **Add More Spatial Queries**: Implement route optimization using PostGIS
2. **Geocoding Integration**: Add address geocoding services
3. **Real-time Updates**: Implement WebSocket for live route updates
4. **Caching**: Add Redis for caching frequently accessed routes
5. **Analytics**: Add route usage analytics and reporting

---

**Happy Spatial Programming! 🗺️🐘** 