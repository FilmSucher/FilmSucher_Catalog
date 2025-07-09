package film_sucher.catalog.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import film_sucher.catalog.entity.Film;
import film_sucher.catalog.service.SuchService;

@RestController
@RequestMapping("/films")
public class SuchController {

    private final SuchService service;

    public SuchController(SuchService service){
        this.service = service;
    }
    
    @GetMapping(params="prompt")
    public ResponseEntity<List<Film>> getList(@RequestParam String prompt){
        Optional<List<Film>> films = service.findFilms(prompt);
        if (films.isPresent()){
            return new ResponseEntity<>(films.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}
