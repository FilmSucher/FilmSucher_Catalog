package film_sucher.catalog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.springframework.test.context.ActiveProfiles;

import film_sucher.catalog.entity.ElasticFilm;
import film_sucher.catalog.entity.Film;
import film_sucher.catalog.exceptions.DatabaseException;
import film_sucher.catalog.exceptions.ElasticException;
import film_sucher.catalog.repository.SqlSuchRepo;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class SuchServiceTest {
    private final String prompt = "Test Prompt";
    private final String emptyPrompt = "";
    private final String title = "Testfilm";
    private final Long id = 1L;
    private final String description = "Description for Testfilm.";
    private final String genre = "TestGenre";
    private final String country = "USA";
    private Film film;
    private final ElasticFilm elasticFilm = new ElasticFilm(id, title, description, genre, country);

    @Mock
    private SqlSuchRepo sqlRepo;
    @Mock
    private ElasticSuchService elasticSuchService;

    @InjectMocks
    private SuchService service;

    @BeforeEach
    public void setUp(){
        film = new Film();
        film.setId(id);
        film.setTitle(title);
        film.setDescription(description);
        film.setGenre(genre);
        film.setCountry(country);
    }

    // get list
    // ---------------------------------------------------------------------------
    @Test
    public void getSuccessfullGetAll(){
        when(sqlRepo.findAllByOrderByRatingDesc()).thenReturn(List.of(film));

        List<Film> result = service.findFilms(emptyPrompt);
        assertEquals(1, result.size());

        verify(sqlRepo).findAllByOrderByRatingDesc();
        
        assertEquals(id, result.get(0).getId());
        assertEquals(title, result.get(0).getTitle());
        assertEquals(description, result.get(0).getDescription());
        assertEquals(genre, result.get(0).getGenre());
        assertEquals(country, result.get(0).getCountry());
    }
    
    @Test
    public void getDBErrorGetAll(){
        doThrow(new DataAccessException("PostgreSQL Error"){}).when(sqlRepo).findAllByOrderByRatingDesc();
        DatabaseException ex = assertThrows(DatabaseException.class, () ->
            service.findFilms(emptyPrompt));
            
        assertEquals("Error getting movies from PostgresSQL", ex.getMessage());
    }

    @Test
    public void getSuccessfullGetList(){
        when(elasticSuchService.search(prompt)).thenReturn(List.of(elasticFilm));
        when(sqlRepo.findAllById(List.of(id))).thenReturn(List.of(film));

        List<Film> result = service.findFilms(prompt);
        assertEquals(1, result.size());

        ArgumentCaptor<Iterable<Long>> captor = ArgumentCaptor.forClass(Iterable.class);
        verify(sqlRepo).findAllById(captor.capture());
        
        List<Long> capturedIds = new ArrayList<>();
        captor.getValue().forEach(capturedIds::add);
        assertEquals(List.of(1L), capturedIds);

        assertEquals(id, result.get(0).getId());
        assertEquals(title, result.get(0).getTitle());
        assertEquals(description, result.get(0).getDescription());
        assertEquals(genre, result.get(0).getGenre());
        assertEquals(country, result.get(0).getCountry());
    }
    @Test
    public void getElasticErrorGetList(){
        doThrow(new RuntimeException("Elastic Error")).when(elasticSuchService).search(prompt);
        ElasticException ex = assertThrows(ElasticException.class, () ->
            service.findFilms(prompt));
            
        assertEquals("Error getting movies from ElasticSearch", ex.getMessage());
        verify(sqlRepo, never()).findAllById(any());
    }
    @Test
    public void getDBErrorGetList(){
        when(elasticSuchService.search(prompt)).thenReturn(List.of(elasticFilm));

        doThrow(new DataAccessException("PostgreSQL Error"){}).when(sqlRepo).findAllById(List.of(id));
        DatabaseException ex = assertThrows(DatabaseException.class, () ->
            service.findFilms(prompt));
            
        assertEquals("Error getting movies from PostgresSQL", ex.getMessage());
    }
    // get film
    // ---------------------------------------------------------------------------
    @Test
    public void getSuccessfullGetOne(){
        when(sqlRepo.findById(id)).thenReturn(Optional.of(film));
        Film result = service.findFilm(id);

        assertEquals(id, result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(description, result.getDescription());
        assertEquals(genre, result.getGenre());
        assertEquals(country, result.getCountry());
    }

    @Test
    public void getDBErrorGetOne(){
        when(sqlRepo.findById(id)).thenThrow(new DataAccessException("DB Error"){});
        DatabaseException ex = assertThrows(DatabaseException.class, () ->
            service.findFilm(id));

        assertEquals("Error getting movie from PostgresSQL", ex.getMessage());
    }

    @Test
    public void getNotFoundErrorGetOne(){
        when(sqlRepo.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
            service.findFilm(id));

        assertEquals("Film with id: " + id + " not found", ex.getMessage());
    }
}
