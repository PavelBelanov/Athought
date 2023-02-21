package ru.belanovpn.Athought.weatherapi.converter;

import org.springframework.stereotype.Component;
import ru.belanovpn.Athought.weatherapi.entity.WeatherEntity;
import ru.belanovpn.Athought.weatherapi.dto.WeatherEntityDto;
@Component
public class WeatherEntityDTOConverter {

   public WeatherEntityDto toWeatherEntityDto(WeatherEntity entity){
       return WeatherEntityDto.builder()
               .id(entity.getId())
               .cityName(entity.getCityName())
               .temperature(entity.getTemperature())
               .feelsLike(entity.getFeelsLike())
               .pressure(entity.getPressure())
               .humidity(entity.getHumidity())
               .windSpeed(entity.getWindSpeed())
               .createdAt(entity.getCreatedAt())
               .build();
   }
   public WeatherEntity fromDTOtoWeatherEntity(WeatherEntityDto entityDto){
       return WeatherEntity.builder()
               .id(entityDto.getId())
               .cityName(entityDto.getCityName())
               .temperature(entityDto.getTemperature())
               .feelsLike(entityDto.getFeelsLike())
               .pressure(entityDto.getPressure())
               .humidity(entityDto.getHumidity())
               .windSpeed(entityDto.getWindSpeed())
               .createdAt(entityDto.getCreatedAt())
               .build();
   }
}
