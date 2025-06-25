# OptiRoute - Route Planner

This is a Spring Boot application that helps users find the best route between multiple locations using Google Maps.

## **Main Features**

- Add multiple stops  
- Choose between round trip or one way  
- Finds the most efficient order to visit locations  
- Shows the route on a map  
- Displays distance, travel time, and directions

## **Tech Stack**

- **Backend**: Java with Spring Boot  
- **Frontend**: HTML, CSS, JavaScript, and Thymeleaf  
- **Maps**: Google Maps JavaScript API and Directions API

## **How to Run**

1. Install Java 17 and Maven  
2. Get a Google Maps API key from Google Cloud  
3. Enable Maps JavaScript API and Directions API  
4. Add the API key to:
   - `application.properties` file
   - `index.html` file  
5. Run the application:  
mvn spring-boot:run

css
Copy
Edit
6. Open a browser and go to:  
http://localhost:8080

markdown
Copy
Edit

## **Example Usage**

**Input example**:  
Start at San Francisco  
Stops: Palo Alto and San Jose  
End at Los Angeles  
Trip type: One way  

**The app returns**:  
- Optimized stop order  
- Total distance  
- Total duration  
- Step-by-step directions

## **API Routes**

- `GET /` - Main page  
- `POST /api/route` - Route optimization via JSON  
- `POST /route` - Form submission

## **Key Files**

- `RouteController.java` - Handles user input and requests  
- `RouteService.java` - Connects to the Google Maps API  
- `index.html` - Main web page  
- `application.properties` - Stores API key and app settings
