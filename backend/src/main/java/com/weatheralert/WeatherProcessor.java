package com.weatheralert;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherProcessor {
    public static String process(String weatherData) {
        try {
            JsonObject json = JsonParser.parseString(weatherData).getAsJsonObject();
            return json.toString(); // Return JSON directly

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Error processing weather data.\"}";
        }
    }
}
