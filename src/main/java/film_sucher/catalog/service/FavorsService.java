package film_sucher.catalog.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import film_sucher.catalog.entity.FavorFilm;
import film_sucher.catalog.entity.FavorFilmId;
import film_sucher.catalog.entity.Film;
import film_sucher.catalog.entity.User;
import film_sucher.catalog.exceptions.DatabaseException;
import film_sucher.catalog.repository.SqlFavorsRepo;
import film_sucher.catalog.repository.SqlSuchRepo;
import jakarta.persistence.EntityNotFoundException;
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

    public List<Film> getAllFavorsFilms(User user){
        // get List with Favorites
        List<FavorFilm> favorFilms = new ArrayList<>();
        try {
            favorFilms = favorsRepo.findByUser(user);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error receiving movies from MyList in PostgresSQL", e);
        }

        if (favorFilms.isEmpty()) throw new EntityNotFoundException("MyList is empty");
        // get films IDs
        List<Film> result = new ArrayList<>();
        for (FavorFilm favorFilm : favorFilms){
            result.add(favorFilm.getFilm());
        }
        return result;
    }

    @Transactional
    public void addFavorsFilms(User user, Long filmId){
        // try {
        //     if (!favorsRepo.existsByUserAndIdFilmId(user, filmId)){
        //         FavorFilm favorFilm = new FavorFilm(new FavorFilmId(user.getId(), filmId), user.getId(), filmId, LocalDateTime.now());
        //         favorsRepo.save(favorFilm);
        //     }
        // } catch (DataAccessException e) {
        //     throw new DatabaseException("Error save movie in MyList in PostgresSQL", e);
        // }

        Film film;
        Optional<Film> opFilm;
        try {
            opFilm = suchRepo.findById(filmId);
        } catch (DataAccessException e) {
             throw new DatabaseException("Error receiving movie by Id from PostgreSQL", e);
        }
        if (opFilm.isPresent()){
            film = opFilm.get();
        } else {
            throw new DatabaseException("Movie with id: " + filmId +" not exists.", null);
        }

        try {
            favorsRepo.save(new FavorFilm(new FavorFilmId(user.getId(), filmId), film, user, LocalDateTime.now()));
        } catch (DataIntegrityViolationException  e) {
            // ignorieren, this Film exist in DB
        } catch (DataAccessException e) {
            throw new DatabaseException("Error save movie in MyList in PostgresSQL", e);
        }
    }

    @Transactional
    public void delFavorsFilms(User user, Long filmId){
        try {
            if (!favorsRepo.existsByUserAndIdFilmId(user, filmId)) throw new EntityNotFoundException("Film is not in MyList");
            // get userId
            Long userId = user.getId();
            favorsRepo.deleteByUserIdAndFilmId(userId, filmId);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error delete movie from MyList in PostgresSQL", e);
        }
    }
}
