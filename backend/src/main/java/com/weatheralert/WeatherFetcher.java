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

    public static String fetchWeather(String lat, String lon) {
        try {
            // Construct the URL with the correct format
            String urlString = BASE_URL + "?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric";
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
            String cityName = json.get("name").getAsString();
            JsonObject main = json.getAsJsonObject("main");
            double temperature = main.get("temp").getAsDouble();
            double feelsLike = main.get("feels_like").getAsDouble();
            int humidity = main.get("humidity").getAsInt();

            // Extract weather description
            String weatherDescription = json.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();

            // Extract wind information
            JsonObject wind = json.getAsJsonObject("wind");
            double windSpeed = wind.get("speed").getAsDouble();

            // Format the output
            return String.format(
                    "City: %s\nTemperature: %.2f°C\nFeels Like: %.2f°C\nHumidity: %d%%\nWeather: %s\nWind Speed: %.2f m/s",
                    cityName, temperature, feelsLike, humidity, weatherDescription, windSpeed
            );
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing weather data.";
        }
    }
}