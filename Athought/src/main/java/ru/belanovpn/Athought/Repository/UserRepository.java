package ru.belanovpn.Athought.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.belanovpn.Athought.model.User;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}
