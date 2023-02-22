package ru.belanovpn.Athought.weatherapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belanovpn.Athought.weatherapi.converter.WeatherEntityDTOConverter;
import ru.belanovpn.Athought.weatherapi.dto.WeatherEntityDto;
import ru.belanovpn.Athought.weatherapi.util.GetWeatherContent;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    private final GetWeatherContent weatherContent;
    private final WeatherEntityDTOConverter converter;

    public String weatherInfo(String cityName) {
        WeatherEntityDto entityDto = null;
        if (!cityName.isEmpty()) {
            entityDto = converter.toWeatherEntityDto(weatherContent.toWeatherEntityParser(cityName));
        } else {
            log.error("argument is empty");
        }
        return String.format("Сейчас в городе %s, %d\u00B0C.\nОщущается как %d\u00B0C.\nСкорость ветра - %d м/с." +
                        "\nДавление %d гПа\nОтносительная влажность воздуха %d%%",
                entityDto.getCityName(), entityDto.getTemperature(), entityDto.getFeelsLike(),
                entityDto.getWindSpeed(), entityDto.getPressure(), entityDto.getHumidity());
    }
}
