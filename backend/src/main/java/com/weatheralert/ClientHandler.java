package com.weatheralert;


import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

            String requestLine = in.readLine(); // Read the first request line
            if (requestLine == null) {
                sendResponse(out, 400, "{\"error\": \"Bad Request\"}");
                return;
            }

            System.out.println("Request: " + requestLine);

            // Extract the method (GET, POST, etc.)
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                sendResponse(out, 400, "{\"error\": \"Bad Request\"}");
                return;
            }

            String method = requestParts[0]; // GET or OPTIONS
            String path = requestParts[1]; // /weather?city=london

            // Handle OPTIONS request (CORS Preflight)
            if ("OPTIONS".equalsIgnoreCase(method)) {
                out.println("HTTP/1.1 204 No Content");
                out.println("Access-Control-Allow-Origin: *");
                out.println("Access-Control-Allow-Methods: GET, OPTIONS");
                out.println("Access-Control-Allow-Headers: Content-Type");
                out.println("Connection: close");
                out.println();
                return;
            }

            // Extract city from query string
            String city = getCityFromQuery(path);
            if (city == null || city.isEmpty()) {
                sendResponse(out, 400, "{\"error\": \"Missing city parameter.\"}");
                return;
            }

            System.out.println("City: " + city);

            // Fetch weather data
            String weatherData = WeatherFetcher.fetchWeatherByCity(city);

            // Process weather data into JSON format before sending
            String processedWeatherData = WeatherProcessor.process(weatherData);
            sendResponse(out, 200, processedWeatherData);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Extract city from the request URL
    private String getCityFromQuery(String path) {
        try {
            if (path.contains("?city=")) {
                String[] parts = path.split("\\?city=");
                if (parts.length > 1) {
                    return URLDecoder.decode(parts[1].split("&")[0], StandardCharsets.UTF_8.name());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Unified method to send responses with CORS headers
    private void sendResponse(PrintWriter out, int statusCode, String jsonResponse) {
        out.println("HTTP/1.1 " + statusCode + " " + getStatusMessage(statusCode));
        out.println("Content-Type: application/json");
        out.println("Access-Control-Allow-Origin: *");  // ✅ Fixes CORS issue
        out.println("Connection: close");
        out.println();
        out.println(jsonResponse);
    }

    // Map status codes to messages
    private String getStatusMessage(int code) {
        switch (code) {
            case 200:
                return "OK";
            case 204:
                return "No Content";
            case 400:
                return "Bad Request";
            case 405:
                return "Method Not Allowed";
            default:
                return "Error";
        }
    }
}
