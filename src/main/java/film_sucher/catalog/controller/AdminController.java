package film_sucher.catalog.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import film_sucher.catalog.dto.ApiResponseDTO;
import film_sucher.catalog.entity.Film;
import film_sucher.catalog.entity.User;
import film_sucher.catalog.exceptions.DatabaseException;
import film_sucher.catalog.exceptions.ElasticException;
import film_sucher.catalog.service.AdminService;
import film_sucher.catalog.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/admin_films")
@Tag(name = "Admin Controller", 
    description = "Manage movies in the database. Access only for authorized administrators")
public class AdminController {
    private final AdminService service;
    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    public AdminController(AdminService service, JwtUtil jwtUtil){
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    // post   jwtUtil.getUserFromToken()
    @Operation(summary = "Add Movie", description = "Creates a new movie in the DB. For admins only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode="201", description="Movie successfully created"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @PostMapping
    public ResponseEntity<?> addFilm(@RequestBody Film newFilm){
        User user = jwtUtil.getUserFromToken();
        logger.info("User successfully received from token");
        
        try {
            logger.info("Attempt to add new movie from user: {} with id: {}", user.getUsername(), user.getId());
            service.addFilm(newFilm);
            logger.info("Successfully adding new movie");
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO("Film successfully added", null, HttpStatus.CREATED));
        } catch (DatabaseException e) {
            logger.warn("DatabaseException while adding new movie: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error saving movie to DB", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (ElasticException e) {
            logger.warn("ElasticException while adding new movie: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error saving movie to Elastic", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.warn("UnexpectedException while adding new movie: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    } 

    // put
    @Operation(summary = "Modify Movie", description = "Modifies a movie in the DB. For admins only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode="200", description="Movie successfully modified"),
        @ApiResponse(responseCode="404", description="Movie is missing"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> changeFilm(@PathVariable("id") Long filmId, @RequestBody Film newFilm){
        User user = jwtUtil.getUserFromToken();
        logger.info("User successfully received from token");

        try {
            logger.info("Attempt to change movie with id: {} from user: {} with userId: {}", filmId, user.getUsername(), user.getId());
            service.changeFilm(filmId, newFilm);
            logger.info("Successfully changing movie with id: {}", filmId);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO("Film successfully changed", null, HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            logger.warn("Film with id: {} not found", filmId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO("Not Found Error", e, HttpStatus.NOT_FOUND));
        } catch (DatabaseException e) {
            logger.warn("DatabaseException while changing movie: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error updating movie to DB", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (ElasticException e) {
            logger.warn("ElasticException while changing movie: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error updating movie to Elastic", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.warn("UnexpectedException while changing movie: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    } 

    // delete
    @Operation(summary = "Delete Movie", description = "Deletes a movie from the DB. For admins only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode="204", description="Movie successfully deleted"),
        @ApiResponse(responseCode="404", description="Movie is missing"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFilm(@PathVariable("id") Long filmId){
        User user = jwtUtil.getUserFromToken();
        logger.info("User successfully received from token");

        try {
            logger.info("Attempt to delete movie with id: {} from user: {} with userId: {}", filmId, user.getUsername(), user.getId());
            service.delFilm(filmId);
            logger.info("Successfully deleting movie with id: {}", filmId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponseDTO("Film successfully deleted", null, HttpStatus.NO_CONTENT));
        } catch (EntityNotFoundException e) {
            logger.warn("Film with id: {} not found", filmId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO("Not Found Error", e, HttpStatus.NOT_FOUND));
        } catch (DatabaseException e) {
            logger.warn("DatabaseException while deleting movie: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error deleting movie to DB", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (ElasticException e) {
            logger.warn("ElasticException while deleting movie: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error deleting movie to Elastic", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.warn("UnexpectedException while deleting movie: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
