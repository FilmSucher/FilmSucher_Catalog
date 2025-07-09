package film_sucher.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import film_sucher.catalog.entity.FavorFilm;
import film_sucher.catalog.entity.FavorFilmId;
import film_sucher.catalog.entity.User;

public interface SqlFavorsRepo extends CrudRepository<FavorFilm, FavorFilmId>{
    
    // get favors films
    Optional<List<FavorFilm>> findByUser(User user);

    // get favors film
    Optional<FavorFilm> findByUserAndIdFilmId(User user, Long filmId);

    // check film in users favors
    boolean existsByUserAndIdFilmId(User user, Long filmId);

    // delete film from users favors
    @Modifying
    @Query("DELETE FROM FavorFilm f WHERE f.id.userId = :userId AND f.id.filmId = :filmId")
    void deleteByUserIdAndFilmId(@Param("userId") Long userId, @Param("filmId") Long filmId);
}
