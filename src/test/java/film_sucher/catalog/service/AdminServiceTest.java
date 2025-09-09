package film_sucher.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import film_sucher.catalog.entity.ElasticFilm;
import film_sucher.catalog.entity.Film;
import film_sucher.catalog.exceptions.DatabaseException;
import film_sucher.catalog.exceptions.ElasticException;
import film_sucher.catalog.repository.ElasticRepo;
import film_sucher.catalog.repository.SqlSuchRepo;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    private final String title = "Testfilm";
    private final Long id = 1L;
    private final String description = "Description for Testfilm.";
    private final String genre = "Testgenre";
    private final String country = "USA";
    private Film filmInDB;
    private Film filmFromDB;

    @Mock
    private SqlSuchRepo sqlRepo;
    @Mock
    private ElasticRepo elRepo;

    @InjectMocks
    private AdminService service;

    @BeforeEach
    public void setUp(){
        filmInDB = new Film();
        filmInDB.setTitle(title);
        filmInDB.setDescription(description);
        filmInDB.setGenre(genre);
        filmInDB.setCountry(country);

        filmFromDB = new Film();
        filmFromDB.setId(id);
        filmFromDB.setTitle(title);
        filmFromDB.setDescription(description);
        filmFromDB.setGenre(genre);
        filmFromDB.setCountry(country);
    }

    // add film
    // ---------------------------------------------------------------------------
    @Test
    public void getSuccessfullAdd(){
        when(sqlRepo.save(filmInDB)).thenReturn(filmFromDB);
        service.addFilm(filmInDB);

        verify(sqlRepo).save(filmInDB);

        ArgumentCaptor<ElasticFilm> captor = ArgumentCaptor.forClass(ElasticFilm.class);
        verify(elRepo).save(captor.capture());

        ElasticFilm saved = captor.getValue();
        assertEquals(id, saved.getId());
        assertEquals(title, saved.getTitle());
        assertEquals(genre, saved.getGenre());
        assertEquals(description, saved.getDescription());
        assertEquals(country, saved.getCountry());
    }

    @Test
    public void getDBErrorAdd(){
        doThrow(new DataAccessException("SQL Error"){}).when(sqlRepo).save(any(Film.class));

        DatabaseException ex = assertThrows(DatabaseException.class, () ->
            service.addFilm(filmInDB));

        assertEquals("Error saving movie to DB", ex.getMessage());
        verify(elRepo, never()).save(any());
    }

    @Test
    public void getElasticErrorAdd(){
        when(sqlRepo.save(filmInDB)).thenReturn(filmFromDB);
        doThrow(new RuntimeException("Elastic Error"){}).when(elRepo).save(any(ElasticFilm.class));

        ElasticException ex = assertThrows(ElasticException.class, () ->
            service.addFilm(filmInDB));

        assertEquals("Error saving movie to Elastic", ex.getMessage());
    }
    
    // change film
    // ---------------------------------------------------------------------------
    @Test
    public void getSuccessfullChange(){
        when(sqlRepo.existsById(id)).thenReturn(true);
        when(sqlRepo.save(filmInDB)).thenReturn(filmFromDB);

        service.changeFilm(id, filmInDB);

        verify(sqlRepo).save(filmInDB);

        ArgumentCaptor<ElasticFilm> captor = ArgumentCaptor.forClass(ElasticFilm.class);
        verify(elRepo).save(captor.capture());

        ElasticFilm saved = captor.getValue();
        assertEquals(id, saved.getId());
        assertEquals(title, saved.getTitle());
        assertEquals(genre, saved.getGenre());
        assertEquals(description, saved.getDescription());
        assertEquals(country, saved.getCountry());
    }

    @Test
    public void getNotFoundErrorChange(){
        when(sqlRepo.existsById(id)).thenReturn(false);
        
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
            service.changeFilm(id, filmInDB));

        assertEquals("Film with id: " + id + " not found", ex.getMessage());
        verify(sqlRepo, never()).save(any());
        verify(elRepo, never()).save(any());
    }

    @Test
    public void getDBErrorChange(){
        when(sqlRepo.existsById(id)).thenReturn(true);
        doThrow(new DataAccessException("SQL Error"){}).when(sqlRepo).save(any(Film.class));

        DatabaseException ex = assertThrows(DatabaseException.class, () ->
            service.changeFilm(id, filmInDB));

        assertEquals("Error saving movie to PostgresSQL", ex.getMessage());
        verify(elRepo, never()).save(any());
    }

    @Test
    public void getElasticErrorChange(){
        when(sqlRepo.existsById(id)).thenReturn(true);
        when(sqlRepo.save(filmInDB)).thenReturn(filmFromDB);
        doThrow(new RuntimeException("Elastic Error"){}).when(elRepo).save(any(ElasticFilm.class));

        ElasticException ex = assertThrows(ElasticException.class, () ->
            service.changeFilm(id, filmInDB));

        assertEquals("Error updating movie to ElasticSearch", ex.getMessage());
    }

    // del film
    // ---------------------------------------------------------------------------
    @Test
    public void getSuccessfullDel(){
        when(sqlRepo.existsById(id)).thenReturn(true);
        service.delFilm(id);

        verify(sqlRepo).deleteById(id);
        verify(elRepo).deleteById(id);
    }

    @Test
    public void getNotFoundErrorDel(){
        when(sqlRepo.existsById(id)).thenReturn(false);
        
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
            service.delFilm(id));

        assertEquals("Film with id: " + id + " not found", ex.getMessage());
        verify(sqlRepo, never()).deleteById(any());
        verify(elRepo, never()).deleteById(any());
    }

    @Test
    public void getDBErrorDel(){
        when(sqlRepo.existsById(id)).thenReturn(true);
        doThrow(new DataAccessException("SQL Error"){}).when(sqlRepo).deleteById(any());

        DatabaseException ex = assertThrows(DatabaseException.class, () ->
            service.delFilm(id));

        assertEquals("Error deleting movie from PostgresSQL", ex.getMessage());
        verify(elRepo, never()).deleteById(any());
    }

    @Test
    public void getElasticErrorDel(){
        when(sqlRepo.existsById(id)).thenReturn(true);
        doThrow(new RuntimeException("Elastic Error"){}).when(elRepo).deleteById(any());
        ElasticException ex = assertThrows(ElasticException.class, () ->
            service.delFilm(id));

        assertEquals("Error deleting movie from Elastic", ex.getMessage());
    }
}
