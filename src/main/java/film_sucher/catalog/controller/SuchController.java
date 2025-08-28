package film_sucher.catalog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import film_sucher.catalog.dto.ApiResponseDTO;
import film_sucher.catalog.entity.Film;
import film_sucher.catalog.exceptions.DatabaseException;
import film_sucher.catalog.exceptions.ElasticException;
import film_sucher.catalog.service.SuchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/films")
@Tag(name = "Such Controller", 
    description = "Search movies on request or return by id")
public class SuchController {

    private final SuchService service;

    public SuchController(SuchService service){
        this.service = service;
    }
    
    @Operation(summary = "Get Film List", description = "Getting a list of movies that match a search query")
    @ApiResponses(value = {
        @ApiResponse(responseCode="200", description="Success, movies loaded"),
        @ApiResponse(responseCode="204", description="Success, list empty"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @GetMapping(params="prompt")
    public ResponseEntity<?> getList(@RequestParam String prompt){
        try{
            List<Film> films = service.findFilms(prompt);
            if (films.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(films);
            }
            return ResponseEntity.status(HttpStatus.OK).body(films);
        } catch(DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Error receiving movie from DB", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (ElasticException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Error receiving movie from Elastic", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @Operation(summary = "Get Film", description = "Getting a movie by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode="200", description="Success, movie loaded"),
        @ApiResponse(responseCode="404", description="Movie is missing"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getFilm(@PathVariable("id") Long filmId){
        try {
            Film film = service.findFilm(filmId);
            return ResponseEntity.status(HttpStatus.OK).body(film);
        } catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO("Not Found Error", e, HttpStatus.NOT_FOUND));
        } catch(DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Error receiving movie from DB", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
