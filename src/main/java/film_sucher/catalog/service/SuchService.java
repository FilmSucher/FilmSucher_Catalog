package film_sucher.catalog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import film_sucher.catalog.entity.ElasticFilm;
import film_sucher.catalog.entity.Film;
import film_sucher.catalog.exceptions.DatabaseException;
import film_sucher.catalog.exceptions.ElasticException;
import film_sucher.catalog.repository.SqlSuchRepo;
import jakarta.persistence.EntityNotFoundException;

@Service
public class SuchService {

    private final SqlSuchRepo sqlRepo;
    // Service for komplex query to Elastic
    private final ElasticSuchService elasticSuchService;

    private static final Logger logger = LoggerFactory.getLogger(SuchService.class);

    @Autowired
    public SuchService(SqlSuchRepo sqlRepo, ElasticSuchService elasticSuchService){
        this.sqlRepo = sqlRepo;
        this.elasticSuchService = elasticSuchService;
    }

    public List<Film> findFilms(String prompt){
        if ("".equals(prompt)){
            logger.info("Query is empty");
            List<Film> results = new ArrayList<>();
            try {
                results = (List<Film>) sqlRepo.findAllByOrderByRatingDesc();
                logger.info("Alle movie successfully getted from DB");
            } catch (DataAccessException e) {
                throw new DatabaseException("Error getting movies from PostgresSQL", e);
            }
            return results;
        }
        else{
            logger.info("Query is not empty: {}", prompt);
            List<ElasticFilm> elasticFilms;
            try{
                elasticFilms = elasticSuchService.search(prompt);
                logger.info("Movie successfully getted from Elastic");
            }
            catch(RuntimeException e){
                throw new ElasticException("Error getting movies from ElasticSearch", e);
            }
            List<Long> ids = new ArrayList<>();
            if (!elasticFilms.isEmpty()){
                for(ElasticFilm film : elasticFilms){
                    ids.add(film.getId());
                }
                logger.info("List with FilmId succesfully collected from ElasticFilms");
                logger.info("Founded {} movies", ids.size());
            }
            List<Film> results = new ArrayList<>();
            try {
                if (!ids.isEmpty()){
                    results = (List<Film>) sqlRepo.findAllById(ids);
                    logger.info("Movies getted from Postgres");
                }
            } catch (DataAccessException e) {
                throw new DatabaseException("Error getting movies from PostgresSQL", e);
            }
            return results;
        }
    }

    public Film findFilm(Long id){
        Optional<Film> result;
        try{
            result = sqlRepo.findById(id);
            logger.info("Movie with id: {} getted from Postgres", id);
        }
        catch(DataAccessException e){
            throw new DatabaseException("Error getting movie from PostgresSQL", e);
        }
        if (result.isEmpty()) throw new EntityNotFoundException("Film with id: " + id + " not found");
        return result.get();
    }
}
