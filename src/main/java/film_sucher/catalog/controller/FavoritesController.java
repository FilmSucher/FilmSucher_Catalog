package film_sucher.catalog.controller;

import java.util.ArrayList;
import java.util.List;

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
        User user = jwtUtil.getUserFromToken();
        List<Film> films;
        try {
            films = service.getAllFavorsFilms(user);
            return ResponseEntity.status(HttpStatus.OK).body(films);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ArrayList<>());
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error receiving movie from MyList", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
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
        User user = jwtUtil.getUserFromToken();
        try {
            service.addFavorsFilms(user, id);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO("Movie added to favorites", null, HttpStatus.CREATED));
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error adding movie in MyList", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    //-----------------------------------------------------------------------
    //del
    @Operation(summary = "Delete Movie", description = "Remove movie from favorites")
    @ApiResponses(value = {
        @ApiResponse(responseCode="201", description="Movie removed from favorites"),
        @ApiResponse(responseCode="404", description="Movie not found in favorites"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delFavors(@PathVariable Long id){
        User user = jwtUtil.getUserFromToken();
        try {
            service.delFavorsFilms(user, id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponseDTO("Movie removed from favorites", null, HttpStatus.NO_CONTENT));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO("Not Found Error", e, HttpStatus.NOT_FOUND));
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error deleting movie from MyList", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
