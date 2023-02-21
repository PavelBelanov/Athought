package ru.belanovpn.Athought.weatherapi.dto;

import lombok.Builder;
import lombok.Data;
import ru.belanovpn.Athought.weatherapi.entity.WeatherEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link WeatherEntity} entity
 */
@Data
@Builder
public class WeatherEntityDto implements Serializable {
    private final long id;
    private final String cityName;
    private final int temperature;
    private final int feelsLike;
    private final int pressure;
    private final int humidity;
    private final int windSpeed;
    private final LocalDateTime createdAt;
}