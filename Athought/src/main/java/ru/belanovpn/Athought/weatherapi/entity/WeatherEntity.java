package ru.belanovpn.Athought.weatherapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="weather_table")
public class WeatherEntity {
    @Id
    private long id;

    private String cityName;

    private int temperature;

    private int feelsLike;

    private int pressure;

    private int humidity;

    private int windSpeed;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

}


