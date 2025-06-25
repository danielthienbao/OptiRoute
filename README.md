OptiRoute - Route Planner

This is a Spring Boot application that helps users find the best route between multiple locations using Google Maps.

Main Features

Add multiple stops

Choose between round trip or one way

Finds the most efficient order to visit locations

Shows the route on a map

Displays distance, travel time, and directions

Tech Stack

Backend: Java with Spring Boot

Frontend: HTML, CSS, JavaScript, and Thymeleaf

Maps: Google Maps JavaScript API and Directions API

How to Run

Install Java 17 and Maven

Get a Google Maps API key from Google Cloud

Enable Maps JavaScript API and Directions API

Add the API key to the application properties file and the index.html file

Run the application using the command
mvn spring-boot:run

Open a browser and go to http://localhost:8080

Example Usage

Input example:
Start at San Francisco
Stops: Palo Alto and San Jose
End at Los Angeles
Trip type: One way

The app returns the best stop order, total distance, total duration, and step-by-step directions.

API Routes

GET slash for the main page

POST slash api slash route for route optimization

POST slash route for form submission

Key Files

RouteController.java handles user input and requests

RouteService.java connects to the Google Maps API

index.html is the main web page

application.properties stores the API key and app settings

Let me know if you want help writing a resume bullet point or understanding any part of the code.
