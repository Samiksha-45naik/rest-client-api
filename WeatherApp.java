import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Scanner;

public class WeatherApp {

    private static final String API_KEY = "a7d73cedfbce705abf2bbbba716713b5"; 
    private static final String BASE_URL = "[https://api.openweathermap.org/data/2.5/weather](https://api.openweathermap.org/data/2.5/weather)";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter city name: ");
        String city = scanner.nextLine();

        String apiUrl = String.format("%s?q=%s&appid=%s&units=metric", BASE_URL, city, API_KEY);

        try {
            String jsonData = fetchData(apiUrl);
            if (jsonData != null) {
                parseAndDisplayWeather(jsonData);
            }
        } catch (IOException e) {
            System.err.println("Error fetching weather data: " + e.getMessage());
        }
        scanner.close();
    }

    /**
     * Fetches JSON data from the given API URL.
     *
     * @param apiUrl The URL of the API endpoint.
     * @return A string containing the JSON response, or null if an error occurs.
     * @throws IOException If an error occurs during the HTTP request.
     */
    private static String fetchData(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                System.err.println("Error: API request failed with response code " + responseCode);
                return null;
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Parses the JSON weather data and displays it in a structured format.
     *
     * @param jsonData The JSON string containing the weather data.
     */
    private static void parseAndDisplayWeather(String jsonData) {
        JSONObject root = new JSONObject(jsonData);

        if (root.has("name")) {
            String cityName = root.getString("name");
            System.out.println("\n--- Weather in " + cityName + " ---");
        }

        if (root.has("main")) {
            JSONObject main = root.getJSONObject("main");
            double temperature = main.getDouble("temp");
            double feelsLike = main.getDouble("feels_like");
            int humidity = main.getInt("humidity");
            System.out.println("Temperature: " + temperature + " °C");
            System.out.println("Feels like: " + feelsLike + " °C");
            System.out.println("Humidity: " + humidity + "%");
        }

        if (root.has("weather")) {
            JSONArray weatherArray = root.getJSONArray("weather");
            if (weatherArray.length() > 0) {
                JSONObject weatherInfo = weatherArray.getJSONObject(0);
                String description = weatherInfo.getString("description");
                System.out.println("Description: " + description);
            }
        }

        if (root.has("wind")) {
            JSONObject wind = root.getJSONObject("wind");
            double windSpeed = wind.getDouble("speed");
            System.out.println("Wind Speed: " + windSpeed + " m/s");
        }

        // You can parse and display more data as needed (e.g., pressure, visibility, etc.)
    }
}
