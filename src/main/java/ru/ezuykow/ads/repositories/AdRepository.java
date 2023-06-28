package ru.ezuykow.ads.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ezuykow.ads.entities.Ad;

import java.util.List;

/**
 * @author ezuykow
 */
@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {

    List<Ad> findAllByAuthor(int author);
}
