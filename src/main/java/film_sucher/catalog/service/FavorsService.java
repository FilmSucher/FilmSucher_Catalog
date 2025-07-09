package film_sucher.catalog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import film_sucher.catalog.entity.FavorFilm;
import film_sucher.catalog.entity.FavorFilmId;
import film_sucher.catalog.entity.Film;
import film_sucher.catalog.entity.User;
import film_sucher.catalog.repository.SqlFavorsRepo;
import film_sucher.catalog.repository.SqlSuchRepo;
import jakarta.transaction.Transactional;

@Service
public class FavorsService {
    private final SqlFavorsRepo favorsRepo;
    private final SqlSuchRepo suchRepo;

    @Autowired
    public FavorsService(SqlFavorsRepo favorsRepo, SqlSuchRepo suchRepo){
        this.favorsRepo = favorsRepo;
        this.suchRepo = suchRepo;
    }

    public Optional<List<Film>> getAllFavorsFilms(User user){
        // get List with Favorites
        Optional<List<FavorFilm>> favorFilms = favorsRepo.findByUser(user);
        if (favorFilms.isEmpty()) return Optional.empty();
        // get films IDs
        List<Long> favorsId = List.of();
        for (FavorFilm favorFilm : favorFilms.get()){
            favorsId.add(favorFilm.getFilmId());
        }
        // get films und return
        return suchRepo.findAllById(favorsId);
    }

    @Transactional
    public Optional<FavorFilm> addFavorsFilms(User user, Long filmId){
        if (favorsRepo.existsByUserAndIdFilmId(user, filmId)) return favorsRepo.findByUserAndIdFilmId(user, filmId);
        FavorFilm favorFilm = new FavorFilm(new FavorFilmId(user.getId(), filmId), user.getId(), filmId, LocalDateTime.now());
        return Optional.of(favorsRepo.save(favorFilm));
    }

    @Transactional
    public void delFavorsFilms(User user, Long filmId){
        if (!favorsRepo.existsByUserAndIdFilmId(user, filmId)) throw new IllegalStateException("Film gibt es nicht in MyList");
        // get userId
        Long userId = user.getId();
        favorsRepo.deleteByUserIdAndFilmId(userId, filmId);
    }
}
