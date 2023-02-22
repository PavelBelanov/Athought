package ru.belanovpn.Athought.weatherapi.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.belanovpn.Athought.weatherapi.entity.WeatherEntity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
@Slf4j
@Component
@RequiredArgsConstructor
public class GetWeatherContent {



    private String getURL(String cityName) {
        StringBuilder resultString = new StringBuilder();

        try {
            URL url = new URL(cityName);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String str;
            while ((str = reader.readLine()) != null) {
                resultString.append(str).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            log.error("Неверный URL: {}",cityName);
        }
        return resultString.toString();
    }

    private JSONObject getJson(String cityName) {
        String jsonObjectFromUrl = getURL(
                String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&appid=3b8f8d77b8801d4d38f90c3fcbbce50e&units=metric&lang=ru", cityName.trim()));
        return new JSONObject(jsonObjectFromUrl);
    }

    public WeatherEntity toWeatherEntityParser(String cityName) {
        JSONObject jsonData = getJson(cityName);
        return WeatherEntity.builder()
                .id(jsonData.getLong("id"))
                .cityName(jsonData.getString("name"))
                .temperature(jsonData.getJSONObject("main").getBigDecimal("temp").intValue())
                .feelsLike(jsonData.getJSONObject("main").getBigDecimal("feels_like").intValue())
                .pressure(jsonData.getJSONObject("main").getBigDecimal("pressure").intValue())
                .humidity(jsonData.getJSONObject("main").getBigDecimal("humidity").intValue())
                .windSpeed(jsonData.getJSONObject("wind").getBigDecimal("speed").intValue())
                .createdAt(LocalDateTime.now())
                .build();
    }


}
