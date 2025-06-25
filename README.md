# OptiRoute - Optimal Route Planner

A Spring Boot application that helps users find the most optimal route between multiple locations using Google Maps Directions API.

## 🚀 Features

- **Multiple Location Support**: Enter multiple stops (addresses or coordinates)
- **Trip Type Selection**: Choose between round-trip or one-way journeys
- **Route Optimization**: Automatically finds the most efficient route order
- **Interactive Map**: View the optimized route on Google Maps
- **Modern UI**: Beautiful, responsive web interface
- **Real-time Results**: Get distance, duration, and turn-by-turn directions

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.2.0 (Java 17)
- **Frontend**: HTML5, CSS3, JavaScript
- **Maps**: Google Maps JavaScript API
- **Routing**: Google Maps Directions API
- **Template Engine**: Thymeleaf

## 📁 Project Structure

```
optimal-route-app/
├── src/main/java/com/example/route/
│   ├── RouteApplication.java          # Main Spring Boot application
│   ├── controller/
│   │   └── RouteController.java       # REST endpoints and form handling
│   ├── service/
│   │   └── RouteService.java          # Google Maps API integration
│   └── dto/
│       ├── RouteRequest.java          # Request data transfer object
│       └── RouteResponse.java         # Response data transfer object
├── src/main/resources/
│   ├── application.properties         # Application configuration
│   └── templates/
│       └── index.html                 # Main web interface
├── pom.xml                           # Maven dependencies
└── README.md                         # This file
```

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Google Maps API key

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd OptiRoute
   ```

2. **Get a Google Maps API Key**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select an existing one
   - Enable the following APIs:
     - Maps JavaScript API
     - Directions API
   - Create credentials (API Key)
   - Restrict the API key to your domain for security

3. **Configure the API Key**
   - Open `src/main/resources/application.properties`
   - Replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your actual API key
   - Also update the API key in `src/main/resources/templates/index.html` (line with Google Maps script)

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Open your browser and go to `http://localhost:8080`
   - Start planning your optimal route!

## 📖 Usage

1. **Enter Starting Point**: Type the address of your starting location
2. **Add Stops**: Click "Add Stop" to add multiple waypoints
3. **Set End Point** (Optional): Enter a final destination
4. **Choose Trip Type**: Toggle "Round Trip" if you want to return to the start
5. **Find Route**: Click "Find Optimal Route" to get the optimized path
6. **View Results**: See the route on the map and detailed information below

## 🔧 Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Server port
server.port=8080

# Google Maps API Key
google.maps.api.key=YOUR_API_KEY_HERE

# Logging level
logging.level.com.example.route=DEBUG
```

### API Endpoints

- `GET /` - Main application page
- `POST /api/route` - REST API for route optimization
- `POST /route` - Form submission endpoint

## 🧪 Example Usage

### Sample Request
```json
{
  "origin": "San Francisco, CA",
  "waypoints": ["Palo Alto, CA", "San Jose, CA"],
  "destination": "Los Angeles, CA",
  "roundTrip": false
}
```

### Sample Response
```json
{
  "status": "OK",
  "optimizedWaypoints": ["Palo Alto, CA", "San Jose, CA"],
  "totalDistance": "612.3 km",
  "totalDuration": "5 hr 45 min",
  "legs": [
    {
      "startAddress": "San Francisco, CA, USA",
      "endAddress": "Palo Alto, CA, USA",
      "distance": "51.2 km",
      "duration": "45 min"
    }
  ]
}
```

## 🔒 Security Considerations

- **API Key Protection**: Always restrict your Google Maps API key to specific domains
- **Input Validation**: The application validates all user inputs
- **Error Handling**: Comprehensive error handling for API failures

## 🐛 Troubleshooting

### Common Issues

1. **"Google Maps API error"**
   - Check if your API key is correct
   - Verify that Directions API is enabled
   - Ensure your API key has proper restrictions

2. **"No routes found"**
   - Check if the addresses are valid
   - Ensure there are no typos in location names
   - Try using more specific addresses

3. **Application won't start**
   - Verify Java 17+ is installed: `java -version`
   - Check Maven installation: `mvn -version`
   - Ensure all dependencies are downloaded

### Debug Mode

Enable debug logging by setting in `application.properties`:
```properties
logging.level.com.example.route=DEBUG
logging.level.org.springframework.web=DEBUG
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Google Maps Platform for providing the routing APIs
- Spring Boot team for the excellent framework
- The open-source community for various tools and libraries

## 📞 Support

If you encounter any issues or have questions:

1. Check the troubleshooting section above
2. Review the application logs
3. Create an issue in the repository
4. Contact the development team

---

**Happy Routing! 🗺️** 