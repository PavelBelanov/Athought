package ru.belanovpn.Athought.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity(name = "ads_table")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ads {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    String adText;

}
