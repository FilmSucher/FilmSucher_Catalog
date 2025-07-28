package film_sucher.catalog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import film_sucher.catalog.entity.FavorFilm;
import film_sucher.catalog.entity.FavorFilmId;
import film_sucher.catalog.entity.Film;
import film_sucher.catalog.entity.User;
import film_sucher.catalog.exceptions.DatabaseException;
import film_sucher.catalog.repository.SqlFavorsRepo;
import film_sucher.catalog.repository.SqlSuchRepo;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class FavorsServiceTest {
    private final String title = "Testfilm";
    private final Long filmId = 1L;
    private final String description = "Description for Testfilm.";
    private final String genre = "TestGenre";
    private final String country = "USA";
    private Film film;
    private FavorFilm favorFilm;

    private final Long userId = 10L;
    private final String username = "User1";
    private final User.Role role = User.Role.USER;
    private User user = new User(userId, username, role);
    
    

    @Mock
    private SqlSuchRepo suchRepo;
    @Mock
    private SqlFavorsRepo favorsRepo;

    @InjectMocks
    private FavorsService service;

    @BeforeEach
    public void setUp(){
        film = new Film();
        film.setId(filmId);
        film.setTitle(title);
        film.setDescription(description);
        film.setGenre(genre);
        film.setCountry(country);

        favorFilm = new FavorFilm(new FavorFilmId(userId, filmId), film, user, LocalDateTime.now());
    }
    // get list
    // ---------------------------------------------------------------------
    @Test
    public void getSuccessfullGetList(){
        when(favorsRepo.findByUser(user)).thenReturn(List.of(favorFilm));
        
        List<Film> result = service.getAllFavorsFilms(user);
        assertEquals(1, result.size());

        assertEquals(filmId, result.get(0).getId());
        assertEquals(title, result.get(0).getTitle());
        assertEquals(description, result.get(0).getDescription());
        assertEquals(genre, result.get(0).getGenre());
        assertEquals(country, result.get(0).getCountry());
    }
    @Test
    public void getFavorsDBErrorGetList(){
        when(favorsRepo.findByUser(user)).thenThrow(new DataAccessException("DB Error"){});
        DatabaseException ex = assertThrows(DatabaseException.class, () ->
            service.getAllFavorsFilms(user));

        assertEquals("Error receiving movies from MyList in PostgresSQL", ex.getMessage());
    }
    @Test
    public void getNotFoundErrorGetList(){
        when(favorsRepo.findByUser(user)).thenReturn(List.of());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
            service.getAllFavorsFilms(user));
            
        assertEquals("MyList is empty", ex.getMessage());
    }
    // add
    // ---------------------------------------------------------------------
    @Test
    public void getSuccessfullAdd(){
        when(suchRepo.findById(filmId)).thenReturn(Optional.of(film));
        service.addFavorsFilms(user, filmId);
        verify(favorsRepo).save(any(FavorFilm.class));
    }
    @Test
    public void getDBErrorByFindAdd(){
        when(suchRepo.findById(filmId)).thenThrow(new DataAccessException("DB Error"){});
        DatabaseException ex = assertThrows(DatabaseException.class, () ->{
                service.addFavorsFilms(user, filmId);}
            ); 
        assertEquals("Error receiving movie by Id from PostgreSQL", ex.getMessage());
    }
    @Test
    public void getDBErrorByOptionalAdd(){
        when(suchRepo.findById(filmId)).thenReturn(Optional.empty());
        DatabaseException ex = assertThrows(DatabaseException.class, () ->{
                service.addFavorsFilms(user, filmId);}
            ); 
        assertEquals("Movie with id: " + filmId +" not exists.", ex.getMessage());
    }
    @Test
    public void getIntegrityViolationAdd(){
        when(suchRepo.findById(filmId)).thenReturn(Optional.of(film));
        when(favorsRepo.save(any(FavorFilm.class))).thenThrow(new DataIntegrityViolationException("Duplicate Error"){});
        
        assertDoesNotThrow(() -> service.addFavorsFilms(user, filmId));

        verify(favorsRepo).save(any(FavorFilm.class));
    }
    @Test
    public void getDBErrorBySaveAdd(){
         when(suchRepo.findById(filmId)).thenReturn(Optional.of(film));
        when(favorsRepo.save(any(FavorFilm.class))).thenThrow(new DataAccessException("DB Error"){});
        DatabaseException ex = assertThrows(DatabaseException.class, () ->{
                service.addFavorsFilms(user, filmId);}
            ); 
        assertEquals("Error save movie in MyList in PostgresSQL", ex.getMessage());
    }

    // del
    // ---------------------------------------------------------------------
    @Test
    public void getSuccessfullDel(){
        when(favorsRepo.existsByUserAndIdFilmId(user, filmId)).thenReturn(true);
        service.delFavorsFilms(user, filmId);
        
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(favorsRepo).deleteByUserIdAndFilmId(captor.capture(), eq(filmId));

        assertEquals(userId, captor.getValue());
    }
    @Test
    public void getNotFoundErrorDel(){
        when(favorsRepo.existsByUserAndIdFilmId(user, filmId)).thenReturn(false);
        
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->{
                service.delFavorsFilms(user, filmId);}
            );
        assertEquals("Film is not in MyList", ex.getMessage());
        verify(favorsRepo, never()).deleteByUserIdAndFilmId(userId, filmId);
    }
    @Test
    public void getDBErrorDel(){
        when(favorsRepo.existsByUserAndIdFilmId(user, filmId)).thenReturn(true);
        doThrow(new DataAccessException("DB Error"){}).when(favorsRepo).deleteByUserIdAndFilmId(userId, filmId);
        DatabaseException ex = assertThrows(DatabaseException.class, () ->{
                service.delFavorsFilms(user, filmId);}
            );
        assertEquals("Error delete movie from MyList in PostgresSQL", ex.getMessage());
    }
}
