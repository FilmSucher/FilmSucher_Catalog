package film_sucher.catalog.controller;

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

import film_sucher.catalog.dto.ApiResponseDTO;
import film_sucher.catalog.entity.Film;
import film_sucher.catalog.entity.User;
import film_sucher.catalog.exceptions.DatabaseException;
import film_sucher.catalog.exceptions.ElasticException;
import film_sucher.catalog.service.AdminService;
import film_sucher.catalog.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;


@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {
    
    private final String title = "Testfilm";
    private final Long id = 1L;
    private final String description = "Description for Testfilm.";
    private Film film;
    private final User user = new User(1L, "user", User.Role.USER);
    
    @Mock
    private AdminService service;
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private AdminController controller;

    @BeforeEach
    public void setUp(){
        film = new Film();
        film.setTitle(title);
        film.setDescription(description);

        when(jwtUtil.getUserFromToken()).thenReturn(user);
    }

    // add film
    // ---------------------------------------------------------------
    @Test
    public void getSuccessfullAdd(){
        // when(jwtUtil.getUserFromToken()).thenReturn(user);
        doNothing().when(service).addFilm(film);
        ResponseEntity<?> result = controller.addFilm(film);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Film successfully added", body.getMessage());
        assertEquals(null, body.getE());
        assertEquals(HttpStatus.CREATED, body.getStatus());
    }

    @Test
    public void getDBErrorAdd(){
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).addFilm(film);
        ResponseEntity<?> result = controller.addFilm(film);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error saving movie to DB", body.getMessage());
        assertTrue(body.getE() instanceof DatabaseException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getElasticErrorAdd(){
        doThrow(new ElasticException("Elastic Error", new RuntimeException())).when(service).addFilm(film);
        ResponseEntity<?> result = controller.addFilm(film);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error saving movie to Elastic", body.getMessage());
        assertTrue(body.getE() instanceof ElasticException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getUnexpectedAdd(){
        doThrow(new RuntimeException("Error!")).when(service).addFilm(film);
        ResponseEntity<?> result = controller.addFilm(film);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Unexpected error", body.getMessage());
        assertTrue(body.getE() instanceof Exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    // modify film
    // ---------------------------------------------------------------
    @Test
    public void getSuccessfullChange(){
        doNothing().when(service).changeFilm(id, film);
        ResponseEntity<?> result = controller.changeFilm(id, film);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Film successfully changed", body.getMessage());
        assertEquals(null, body.getE());
        assertEquals(HttpStatus.OK, body.getStatus());
    }

    @Test
    public void getNotFoundErrorChange(){
        doThrow(new EntityNotFoundException("NotFound Error", new RuntimeException())).when(service).changeFilm(id, film);
        ResponseEntity<?> result = controller.changeFilm(id, film);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Not Found Error", body.getMessage());
        assertTrue(body.getE() instanceof EntityNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND, body.getStatus());

    }

    @Test
    public void getDBErrorChange(){
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).changeFilm(id, film);
        ResponseEntity<?> result = controller.changeFilm(id, film);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error updating movie to DB", body.getMessage());
        assertTrue(body.getE() instanceof DatabaseException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getElasticErrorChange(){
        doThrow(new ElasticException("Elastic Error", new RuntimeException())).when(service).changeFilm(id, film);
        ResponseEntity<?> result = controller.changeFilm(id, film);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error updating movie to Elastic", body.getMessage());
        assertTrue(body.getE() instanceof ElasticException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getUnexpectedChange(){
        doThrow(new RuntimeException("Error!")).when(service).changeFilm(id, film);
        ResponseEntity<?> result = controller.changeFilm(id, film);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Unexpected error", body.getMessage());
        assertTrue(body.getE() instanceof Exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    // delete film
    // ---------------------------------------------------------------
    @Test
    public void getSuccessfullDel(){
        doNothing().when(service).delFilm(id);
        ResponseEntity<?> result = controller.deleteFilm(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Film successfully deleted", body.getMessage());
        assertEquals(null, body.getE());
        assertEquals(HttpStatus.NO_CONTENT, body.getStatus());
    }

    @Test
    public void getNotFoundErrorDel(){
        doThrow(new EntityNotFoundException("NotFound Error", new RuntimeException())).when(service).delFilm(id);
        ResponseEntity<?> result = controller.deleteFilm(id);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Not Found Error", body.getMessage());
        assertTrue(body.getE() instanceof EntityNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND, body.getStatus());
    }

    @Test
    public void getDBErrorDel(){
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).delFilm(id);
        ResponseEntity<?> result = controller.deleteFilm(id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error deleting movie to DB", body.getMessage());
        assertTrue(body.getE() instanceof DatabaseException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getElasticErrorDel(){
        doThrow(new ElasticException("Elastic Error", new RuntimeException())).when(service).delFilm(id);
        ResponseEntity<?> result = controller.deleteFilm(id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error deleting movie to Elastic", body.getMessage());
        assertTrue(body.getE() instanceof ElasticException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getUnexpectedDel(){
        doThrow(new RuntimeException("Error!")).when(service).delFilm(id);
        ResponseEntity<?> result = controller.deleteFilm(id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Unexpected error", body.getMessage());
        assertTrue(body.getE() instanceof Exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }
}
