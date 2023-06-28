package ru.ezuykow.ads.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ezuykow.ads.entities.Comment;

import java.util.List;

/**
 * @author ezuykow
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByAdId(int adId);

    void deleteAllByAdId(int adId);
}
