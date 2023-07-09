package ru.ezuykow.ads.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ezuykow.ads.entities.User;

/**
 * @author ezuykow
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserByEmail(String email);
}
