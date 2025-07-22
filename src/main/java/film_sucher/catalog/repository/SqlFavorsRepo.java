package film_sucher.catalog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import film_sucher.catalog.entity.FavorFilm;
import film_sucher.catalog.entity.FavorFilmId;
import film_sucher.catalog.entity.User;

@Repository
public interface SqlFavorsRepo extends CrudRepository<FavorFilm, FavorFilmId>{
    
    // get favors films
    List<FavorFilm> findByUser(User user);

    // check film in users favors
    boolean existsByUserAndIdFilmId(User user, Long filmId);

    // delete film from users favors
    @Modifying
    @Query("DELETE FROM FavorFilm f WHERE f.id.userId = :userId AND f.id.filmId = :filmId")
    void deleteByUserIdAndFilmId(@Param("userId") Long userId, @Param("filmId") Long filmId);
}
