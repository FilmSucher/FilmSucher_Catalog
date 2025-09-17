package film_sucher.catalog.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import film_sucher.catalog.dto.ApiResponseDTO;
import film_sucher.catalog.entity.Film;
import film_sucher.catalog.entity.User;
import film_sucher.catalog.exceptions.DatabaseException;
import film_sucher.catalog.service.FavorsService;
import film_sucher.catalog.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/favorites")
@Tag(name = "Favorites Controller", 
    description = "Managing Movies in Your Favorites List")
public class FavoritesController {

    private final FavorsService service;
    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(FavoritesController.class);

    @Autowired
    private FavoritesController(FavorsService service, JwtUtil jwtUtil){
        this.service=service;
        this.jwtUtil = jwtUtil;
    }

    //-----------------------------------------------------------------------
    //get all
    @Operation(summary = "Get Favorites", description = "Get all movies from favorites list.")
    @ApiResponses(value = {
        @ApiResponse(responseCode="200", description="Favorite movies list retrieved"),
        @ApiResponse(responseCode="204", description="Favorite movies list empty"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @GetMapping
    public ResponseEntity<?> getFavors(){
        // User from Token in Header
        logger.info("Attempt to get user from token");
        User user = jwtUtil.getUserFromToken();
        logger.info("User successfully received");
        List<Film> films;
        try {
            logger.info("Attempt to get all favorite movies");
            films = service.getAllFavorsFilms(user);
            logger.info("Successfully getting all favorite movies for user: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(films);
        } catch (EntityNotFoundException e) {
            logger.info("Favorite movies list is empty for user: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponseDTO("No movies in MyList", null, HttpStatus.NO_CONTENT));
        } catch (DatabaseException e) {
            logger.warn("DatabaseException while getting list of all favorite movies: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error receiving movie from MyList", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.warn("UnexpectedException while getting list of all favorite movies: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    //-----------------------------------------------------------------------
    //post
    @Operation(summary = "Add Movie", description = "Add movie to favorites")
    @ApiResponses(value = {
        @ApiResponse(responseCode="201", description="Movie added to favorites"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @PostMapping("/{id}")
    public ResponseEntity<?> addFavors(@PathVariable Long id){
        // User from Token in Header
        logger.info("Attempt to get user from token");
        User user = jwtUtil.getUserFromToken();
        logger.info("User successfully received");
        try {
            logger.info("Attempt to add favorite movie: {}", id);
            service.addFavorsFilms(user, id);
            logger.info("Successfully adding favorite movie: {} for user: {}", id, user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO("Movie added to favorites", null, HttpStatus.CREATED));
        } catch (EntityNotFoundException e) {
            logger.warn("Movie with id: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO("Not Found Error", e, HttpStatus.NOT_FOUND));
        } catch (DatabaseException e) {
            logger.warn("DatabaseException while adding in list of favorite movies: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error adding movie in MyList", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.warn("UnexpectedException while adding in list of favorite movies: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    //-----------------------------------------------------------------------
    //del
    @Operation(summary = "Delete Movie", description = "Remove movie from favorites")
    @ApiResponses(value = {
        @ApiResponse(responseCode="204", description="Movie removed from favorites"),
        @ApiResponse(responseCode="404", description="Movie not found in favorites"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delFavors(@PathVariable Long id){
        logger.info("Attempt to get user from token");
        User user = jwtUtil.getUserFromToken();
        logger.info("User successfully received");
        try {
            logger.info("Attempt to delete favorite movie: {}", id);
            service.delFavorsFilms(user, id);
            logger.info("Successfully deleting favorite movie: {} for user: {}", id, user.getUsername());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponseDTO("Movie removed from favorites", null, HttpStatus.NO_CONTENT));
        } catch (EntityNotFoundException e) {
            logger.warn("Movie with id: {} not found in favorite movies for user: {}", id, user.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO("Not Found Error", e, HttpStatus.NOT_FOUND));
        } catch (DatabaseException e) {
            logger.warn("DatabaseException while deleting from list of favorite movies: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error deleting movie from MyList", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.warn("UnexpectedException while deleting from list of favorite movies: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
