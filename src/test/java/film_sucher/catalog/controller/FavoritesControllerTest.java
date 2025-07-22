package film_sucher.catalog.controller;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import film_sucher.catalog.entity.Film;
import film_sucher.catalog.entity.User;
import film_sucher.catalog.exceptions.DatabaseException;
import film_sucher.catalog.service.FavorsService;
import film_sucher.catalog.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class FavoritesControllerTest {
    private final String title = "Testfilm";
    private final Long id = 1L;
    private final String description = "Description for Testfilm.";
    private Film film;
    private User user = new User(12L, "username", User.Role.USER);
    
    @Mock
    private FavorsService service;
    @Mock
    private JwtUtil util;
    
    @InjectMocks
    private FavoritesController controller;

    @BeforeEach
    public void setUp(){
        film = new Film();
        film.setTitle(title);
        film.setDescription(description);

        when(util.getUserFromToken()).thenReturn(user);
    }

    // get list
    // ---------------------------------------------------------------
    @Test
    public void getSuccessfullGetList(){
        when(service.getAllFavorsFilms(user)).thenReturn(List.of(film));
        ResponseEntity<?> result = controller.getFavors();
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof List<?>);
        List<?> body = (List<?>) result.getBody();
        assertEquals(film, body.get(0));
    }

    @Test
    public void getNoContentGetList(){
        when(service.getAllFavorsFilms(user)).thenThrow(new EntityNotFoundException("NotFound Error", new RuntimeException()));
        ResponseEntity<?> result = controller.getFavors();
        
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertTrue(result.getBody() instanceof List<?>);
        List<?> body = (List<?>) result.getBody();
        assertTrue(body.isEmpty());
    }

    @Test
    public void getDBErrGetList(){
        when(service.getAllFavorsFilms(user)).thenThrow(new DatabaseException("DB Error", new RuntimeException()));
        ResponseEntity<?> result = controller.getFavors();
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error receiving movie from MyList", result.getBody());
    }

    @Test
    public void getUnexpectedErrGetList(){
        when(service.getAllFavorsFilms(user)).thenThrow(new RuntimeException("Error"));
        ResponseEntity<?> result = controller.getFavors();
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Unexpected error", result.getBody());
    }

    // add film
    // ---------------------------------------------------------------
    @Test
    public void getSuccessfullAdd(){
        doNothing().when(service).addFavorsFilms(user, id);
        ResponseEntity<?> result = controller.addFavors(id);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(null, result.getBody());
    }

    @Test
    public void getDBErrorAdd(){
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).addFavorsFilms(user, id);
        ResponseEntity<?> result = controller.addFavors(id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error adding movie in MyList", result.getBody());
    }

    @Test
    public void getUnexpectedAdd(){
        doThrow(new RuntimeException("Error!")).when(service).addFavorsFilms(user, id);
        ResponseEntity<?> result = controller.addFavors(id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Unexpected error", result.getBody());
    }

    // del film
    // ---------------------------------------------------------------
    @Test
    public void getSuccessfullDel(){
        doNothing().when(service).delFavorsFilms(user, id);
        ResponseEntity<?> result = controller.delFavors(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertEquals(null, result.getBody());
    }

    @Test
    public void getNotFoundErrorDel(){
        doThrow(new EntityNotFoundException("NotFound Error", new RuntimeException())).when(service).delFavorsFilms(user, id);
        ResponseEntity<?> result = controller.delFavors(id);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("NotFound Error", result.getBody());
    }

    @Test
    public void getDBErrorDel(){
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).delFavorsFilms(user, id);
        ResponseEntity<?> result = controller.delFavors(id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error deleting movie from MyList", result.getBody());
    }

    @Test
    public void getUnexpectedDel(){
        doThrow(new RuntimeException("Error!")).when(service).delFavorsFilms(user, id);
        ResponseEntity<?> result = controller.delFavors(id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Unexpected error", result.getBody());
    }
}
