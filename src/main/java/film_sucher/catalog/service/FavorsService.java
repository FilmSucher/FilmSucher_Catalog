package film_sucher.catalog.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        List<Long> favorsId = new ArrayList<>();
        for (FavorFilm favorFilm : favorFilms){
            favorsId.add(favorFilm.getFilmId());
        }
        // get films und return
        List<Film> result = new ArrayList<>();
        try {
            result = (List<Film>) suchRepo.findAllById(favorsId);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error receiving movies from PostgresSQL", e);
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

        
        try {
            favorsRepo.save(new FavorFilm(new FavorFilmId(user.getId(), filmId), user.getId(), filmId, LocalDateTime.now()));
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
