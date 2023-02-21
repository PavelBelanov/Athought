package ru.belanovpn.Athought.weatherapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.belanovpn.Athought.weatherapi.entity.WeatherEntity;

@Repository
public interface WeatherEntityRepository extends JpaRepository<WeatherEntity,Long> {

    WeatherEntity findByCityName(String name);
}
