package film_sucher.catalog.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import film_sucher.catalog.entity.Film;
import film_sucher.catalog.exceptions.DatabaseException;
import film_sucher.catalog.exceptions.ElasticException;
import film_sucher.catalog.service.AdminService;
import jakarta.persistence.EntityNotFoundException;


@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {
    
    private final String title = "Testfilm";
    private final Long id = 1L;
    private final String description = "Description for Testfilm.";
    private Film film;
    
    @Mock
    private AdminService service;
    
    @InjectMocks
    private AdminController controller;

    @BeforeEach
    public void setUp(){
        film = new Film();
        film.setTitle(title);
        film.setDescription(description);
    }

    // add film
    // ---------------------------------------------------------------
    @Test
    public void getSuccessfullAdd(){
        doNothing().when(service).addFilm(film);
        ResponseEntity<?> result = controller.addFilm(film);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Film successfully added", result.getBody());
    }

    @Test
    public void getDBErrorAdd(){
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).addFilm(film);
        ResponseEntity<?> result = controller.addFilm(film);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error saving movie to DB", result.getBody());
    }

    @Test
    public void getElasticErrorAdd(){
        doThrow(new ElasticException("Elastic Error", new RuntimeException())).when(service).addFilm(film);
        ResponseEntity<?> result = controller.addFilm(film);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error saving movie to Elastic", result.getBody());
    }

    @Test
    public void getUnexpectedAdd(){
        doThrow(new RuntimeException("Error!")).when(service).addFilm(film);
        ResponseEntity<?> result = controller.addFilm(film);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Unexpected error", result.getBody());
    }

    // modify film
    // ---------------------------------------------------------------
    @Test
    public void getSuccessfullChange(){
        doNothing().when(service).changeFilm(id, film);
        ResponseEntity<?> result = controller.changeFilm(id, film);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Film successfully changed", result.getBody());
    }

    @Test
    public void getNotFoundErrorChange(){
        doThrow(new EntityNotFoundException("NotFound Error", new RuntimeException())).when(service).changeFilm(id, film);
        ResponseEntity<?> result = controller.changeFilm(id, film);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("NotFound Error", result.getBody());
    }

    @Test
    public void getDBErrorChange(){
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).changeFilm(id, film);
        ResponseEntity<?> result = controller.changeFilm(id, film);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error updating movie to DB", result.getBody());
    }

    @Test
    public void getElasticErrorChange(){
        doThrow(new ElasticException("Elastic Error", new RuntimeException())).when(service).changeFilm(id, film);
        ResponseEntity<?> result = controller.changeFilm(id, film);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error updating movie to Elastic", result.getBody());
    }

    @Test
    public void getUnexpectedChange(){
        doThrow(new RuntimeException("Error!")).when(service).changeFilm(id, film);
        ResponseEntity<?> result = controller.changeFilm(id, film);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Unexpected error", result.getBody());
    }

    // delete film
    // ---------------------------------------------------------------
    @Test
    public void getSuccessfullDel(){
        doNothing().when(service).delFilm(id);
        ResponseEntity<?> result = controller.deleteFilm(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertEquals("Film successfully deleted", result.getBody());
    }

    @Test
    public void getNotFoundErrorDel(){
        doThrow(new EntityNotFoundException("NotFound Error", new RuntimeException())).when(service).delFilm(id);
        ResponseEntity<?> result = controller.deleteFilm(id);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("NotFound Error", result.getBody());
    }

    @Test
    public void getDBErrorDel(){
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).delFilm(id);
        ResponseEntity<?> result = controller.deleteFilm(id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error deleting movie to DB", result.getBody());
    }

    @Test
    public void getElasticErrorDel(){
        doThrow(new ElasticException("Elastic Error", new RuntimeException())).when(service).delFilm(id);
        ResponseEntity<?> result = controller.deleteFilm(id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error deleting movie to Elastic", result.getBody());
    }

    @Test
    public void getUnexpectedDel(){
        doThrow(new RuntimeException("Error!")).when(service).delFilm(id);
        ResponseEntity<?> result = controller.deleteFilm(id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Unexpected error", result.getBody());
    }
}
