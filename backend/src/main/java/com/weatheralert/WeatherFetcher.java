package com.weatheralert;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetcher {
    private static final String API_KEY = "970eb744cabfc03394a171c23fbd6829"; // Replace with your actual API key
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    // Fetch weather data by city name
    public static String fetchWeatherByCity(String city) {
        try {
            // Construct the URL with the correct format using the city name
            String urlString = BASE_URL + "?q=" + city + "&appid=" + API_KEY + "&units=metric";
            System.out.println("Constructed URL: " + urlString);

            // Open connection
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Log raw JSON response (for debugging)
            System.out.println("Raw API Response: " + response);

            // Parse and extract weather details
            return parseWeatherData(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Unable to fetch weather data.\"}";
        }
    }

    private static String parseWeatherData(String jsonData) {
        try {
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract main weather details
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("city", json.get("name").getAsString());
            JsonObject main = json.getAsJsonObject("main");
            responseJson.addProperty("temperature", main.get("temp").getAsDouble());
            responseJson.addProperty("feels_like", main.get("feels_like").getAsDouble());
            responseJson.addProperty("humidity", main.get("humidity").getAsInt());

            // Extract weather description
            String weatherDescription = json.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
            responseJson.addProperty("weather", weatherDescription);

            // Extract wind information
            JsonObject wind = json.getAsJsonObject("wind");
            responseJson.addProperty("wind_speed", wind.get("speed").getAsDouble());
            
            return responseJson.toString(); // Return JSON response as a string
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Error parsing weather data.\"}";
        }
    }
}
