package ru.belanovpn.Athought.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.belanovpn.Athought.model.Ads;
@Repository
public interface AdsRepository extends JpaRepository<Ads, Long> {
}