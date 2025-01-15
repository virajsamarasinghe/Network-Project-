package com.weatheralert;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherProcessor {
    public static String process(String weatherData) {
        JsonObject json = JsonParser.parseString(weatherData).getAsJsonObject();
        String city = json.get("name").getAsString();
        double temp = json.get("main").getAsJsonObject().get("temp").getAsDouble();
        String description = json.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString();

        return "City: " + city + ", Temp: " + temp + "°C, Description: " + description;
    }
}