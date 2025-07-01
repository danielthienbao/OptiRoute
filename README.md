# OptiRoute

A simple app that finds the best route between multiple places using Google Maps.

## What it does

- Plan routes with multiple stops
- Choose round-trip or one-way
- Shows the fastest route order
- Displays route on a map
- Shows distance and time

## Quick Start

### What you need
- Java 17+
- Maven
- Google Maps API key

### Setup

1. **Get Google Maps API key**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a project
   - Enable Maps JavaScript API and Directions API
   - Create an API key

2. **Add your API key**
   - Open `src/main/resources/application.properties`
   - Replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your key
   - Also update the key in `src/main/resources/templates/index.html`

3. **Run the app**
   ```bash
   mvn spring-boot:run
   ```

4. **Open in browser**
   - Go to `http://localhost:8080`

## How to use

1. Enter your starting point
2. Add stops by clicking "Add Stop"
3. Enter an end point (optional)
4. Choose round-trip if needed
5. Click "Find Optimal Route"
6. View the route on the map

## Tech used

- Spring Boot (Java)
- Google Maps API
- HTML/CSS/JavaScript

## Troubleshooting

- **API errors**: Check your API key and make sure Directions API is enabled
- **No routes**: Check that addresses are correct
- **App won't start**: Make sure you have Java 17+ and Maven installed
