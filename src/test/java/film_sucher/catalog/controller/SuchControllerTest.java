package film_sucher.catalog.controller;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import film_sucher.catalog.dto.ApiResponseDTO;
import film_sucher.catalog.entity.Film;
import film_sucher.catalog.exceptions.DatabaseException;
import film_sucher.catalog.exceptions.ElasticException;
import film_sucher.catalog.service.SuchService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class SuchControllerTest {

    private final String prompt = "Test prompt";
    private final String title = "Testfilm";
    private final Long id = 1L;
    private final String description = "Description for Testfilm.";
    private Film film;    
    
    @Mock
    private SuchService service;
    
    @InjectMocks
    private SuchController controller;
    
    @BeforeEach
    void setUp(){
        film = new Film();
        film.setId(id);
        film.setTitle(title);
        film.setDescription(description);
    }

    // get List
    // ---------------------------------------------------------------
    @Test
    public void getSuccessfullGetList(){
        when(service.findFilms(prompt)).thenReturn(List.of(film));
        ResponseEntity<?> result = controller.getList(prompt);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof List<?>);
        List<?> body = (List<?>) result.getBody();
        assertEquals(film, body.get(0));
    }

    @Test
    public void getNoContentGetList(){
        when(service.findFilms(prompt)).thenReturn(List.of());
        ResponseEntity<?> result = controller.getList(prompt);
        
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertTrue(result.getBody() instanceof List<?>);
        
        List<?> body = (List<?>) result.getBody();
        assertTrue(body.isEmpty());
    }

    @Test
    public void getDBErrGetList(){
        when(service.findFilms(prompt)).thenThrow(new DatabaseException("DB Error", new RuntimeException()));
        ResponseEntity<?> result = controller.getList(prompt);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error receiving movie from DB", body.getMessage());
        assertTrue(body.getE() instanceof DatabaseException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getElasticErrGetList(){
        when(service.findFilms(prompt)).thenThrow(new ElasticException("Elastic Error", new RuntimeException()));
        ResponseEntity<?> result = controller.getList(prompt);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error receiving movie from Elastic", body.getMessage());
        assertTrue(body.getE() instanceof ElasticException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getUnexpectedErrGetList(){
        when(service.findFilms(prompt)).thenThrow(new RuntimeException("Error"));
        ResponseEntity<?> result = controller.getList(prompt);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Unexpected error", body.getMessage());
        assertTrue(body.getE() instanceof Exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    // get one
    // ---------------------------------------------------------------
    @Test
    public void getSuccessfullGetFilm(){
        when(service.findFilm(id)).thenReturn(film);
        ResponseEntity<?> result = controller.getFilm(id);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof Film);
        assertEquals(film, result.getBody());
    }

    @Test
    public void getNotFoundErrGetFilm(){
        when(service.findFilm(id)).thenThrow(new EntityNotFoundException("Not Found Error", new RuntimeException()));
        ResponseEntity<?> result = controller.getFilm(id);
        
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Not Found Error", body.getMessage());
        assertTrue(body.getE() instanceof EntityNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND, body.getStatus());
    }

    @Test
    public void getDBErrGetFilm(){
        when(service.findFilm(id)).thenThrow(new DatabaseException("DB Error", new RuntimeException()));
        ResponseEntity<?> result = controller.getFilm(id);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error receiving movie from DB", body.getMessage());
        assertTrue(body.getE() instanceof DatabaseException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getUnexpectedErrGetFilm(){
        when(service.findFilm(id)).thenThrow(new RuntimeException("Error"));
        ResponseEntity<?> result = controller.getFilm(id);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Unexpected error", body.getMessage());
        assertTrue(body.getE() instanceof Exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }
}
