package ru.belanovpn.Athought.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@ToString
public class User {
    @Id
    private long chatId;

    private String firstName;

    private String lastName;

    private String userName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

}
