import React, { useState } from "react";

function WeatherClient() {
  const [lat, setLat] = useState("");
  const [lon, setLon] = useState("");
  const [weatherData, setWeatherData] = useState(null);
  const [error, setError] = useState("");

  const fetchWeather = async () => {
    try {
      setError("");
      setWeatherData(null);

      const response = await fetch("http://localhost:8080/weather", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ lat, lon }),
      });

      if (!response.ok) {
        throw new Error("Failed to fetch weather data.");
      }

      const data = await response.text(); // Get raw text response
      setWeatherData(data);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div style={{ padding: "20px", fontFamily: "Arial, sans-serif" }}>
      <h1>Weather Client</h1>
      <div style={{ marginBottom: "20px" }}>
        <input
          type="text"
          placeholder="Enter Latitude"
          value={lat}
          onChange={(e) => setLat(e.target.value)}
          style={{
            marginRight: "10px",
            padding: "8px",
            border: "1px solid #ccc",
            borderRadius: "4px",
          }}
        />
        <input
          type="text"
          placeholder="Enter Longitude"
          value={lon}
          onChange={(e) => setLon(e.target.value)}
          style={{
            marginRight: "10px",
            padding: "8px",
            border: "1px solid #ccc",
            borderRadius: "4px",
          }}
        />
        <button
          onClick={fetchWeather}
          style={{
            padding: "8px 16px",
            backgroundColor: "#007bff",
            color: "#fff",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          Get Weather
        </button>
      </div>

      {error && (
        <p style={{ color: "red", fontWeight: "bold" }}>
          Error: {error}
        </p>
      )}

      {weatherData && (
        <div
          style={{
            whiteSpace: "pre-wrap",
            backgroundColor: "#f4f4f4",
            padding: "10px",
            border: "1px solid #ccc",
            borderRadius: "4px",
          }}
        >
          <strong>Weather Data:</strong>
          <br />
          {weatherData}
        </div>
      )}
    </div>
  );
}

export default WeatherClient;