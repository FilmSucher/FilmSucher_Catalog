package film_sucher.catalog.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import film_sucher.catalog.entity.FavorFilm;
import film_sucher.catalog.entity.Film;
import film_sucher.catalog.entity.User;
import film_sucher.catalog.service.FavorsService;
import film_sucher.catalog.utils.JwtUtil;

@Controller
@RequestMapping("/favors")
public class FavoritesController {

    private final FavorsService service;

    @Autowired
    private FavoritesController(FavorsService service){
        this.service=service;
    }

    @GetMapping
    public ResponseEntity<List<Film>> getFavors(@RequestHeader("Authorization") String token){
        // User from Token in Header
        User user = JwtUtil.getUserFromToken(token);
        Optional<List<Film>> films = service.getAllFavorsFilms(user);
        return films.isEmpty() ? new ResponseEntity<>(null, HttpStatus.NOT_FOUND) : new ResponseEntity<>(films.get(), HttpStatus.OK);

    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> addFavors(@RequestHeader("Authorization") String token, @RequestAttribute Long id){
        User user = JwtUtil.getUserFromToken(token);
        Optional<FavorFilm> result = service.addFavorsFilms(user, id);
        return result.isEmpty() ? new ResponseEntity<>(null, HttpStatus.NOT_FOUND) : new ResponseEntity<>(null, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delFavors(@RequestHeader("Authorization") String token, @RequestAttribute Long id){
        User user = JwtUtil.getUserFromToken(token);
        service.delFavorsFilms(user, id);
        return ResponseEntity.noContent().build();
    }
    
}
