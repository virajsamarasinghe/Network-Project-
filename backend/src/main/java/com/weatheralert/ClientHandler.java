package com.weatheralert;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

            StringBuilder headers = new StringBuilder();
            String line;
            String method = "";
            int contentLength = 0;

            // Read headers and determine HTTP method
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                headers.append(line).append("\n");
                if (line.startsWith("POST") || line.startsWith("OPTIONS")) {
                    method = line.split(" ")[0]; // Extract HTTP method
                }
                if (line.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            System.out.println("Request headers:\n" + headers);

            // Handle CORS preflight (OPTIONS) request
            if ("OPTIONS".equalsIgnoreCase(method)) {
                out.println("HTTP/1.1 204 No Content");
                out.println("Access-Control-Allow-Origin: *");
                out.println("Access-Control-Allow-Methods: POST, OPTIONS");
                out.println("Access-Control-Allow-Headers: Content-Type");
                out.println("Connection: close");
                out.println();
                return;
            }

            // Handle POST request
            if ("POST".equalsIgnoreCase(method)) {
                // Validate content length
                if (contentLength <= 0) {
                    System.err.println("Empty request body received.");
                    out.println("HTTP/1.1 400 Bad Request");
                    out.println("Content-Type: application/json");
                    out.println("Connection: close");
                    out.println();
                    out.println("{\"error\": \"Empty request body.\"}");
                    return;
                }

                // Read request body
                char[] body = new char[contentLength];
                in.read(body, 0, contentLength);
                String requestBody = new String(body).trim();

                System.out.println("Request body: " + requestBody);

                // Parse JSON
                JsonObject jsonObject;
                try {
                    jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
                } catch (Exception e) {
                    System.err.println("Invalid JSON payload: " + e.getMessage());
                    out.println("HTTP/1.1 400 Bad Request");
                    out.println("Content-Type: application/json");
                    out.println("Connection: close");
                    out.println();
                    out.println("{\"error\": \"Invalid JSON payload.\"}");
                    return;
                }

                // Extract latitude and longitude
                String lat = jsonObject.has("lat") ? jsonObject.get("lat").getAsString() : null;
                String lon = jsonObject.has("lon") ? jsonObject.get("lon").getAsString() : null;

                if (lat == null || lon == null) {
                    System.err.println("Missing latitude or longitude in request.");
                    out.println("HTTP/1.1 400 Bad Request");
                    out.println("Content-Type: application/json");
                    out.println("Connection: close");
                    out.println();
                    out.println("{\"error\": \"Missing latitude or longitude.\"}");
                    return;
                }

                System.out.println("Latitude: " + lat + ", Longitude: " + lon);

                // Fetch weather data
                String weatherData = WeatherFetcher.fetchWeather(lat, lon);

                // Send raw API response back to the client
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: application/json");
                out.println("Access-Control-Allow-Origin: *");
                out.println("Connection: close");
                out.println();
                out.println(weatherData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}