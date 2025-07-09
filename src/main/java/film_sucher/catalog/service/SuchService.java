package film_sucher.catalog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import film_sucher.catalog.entity.Film;
import film_sucher.catalog.repository.ElasticRepo;
import film_sucher.catalog.repository.SqlSuchRepo;

@Service
public class SuchService {

    private SqlSuchRepo sqlRepo;
    private ElasticRepo elasticRepo;

    public SuchService(SqlSuchRepo sqlRepo, ElasticRepo elasticRepo){
        this.sqlRepo = sqlRepo;
        this.elasticRepo = elasticRepo;
    }

    public Optional<List<Film>> findFilms(String prompt){
        Optional<List<Long>> ids = elasticRepo.findByPrompt(prompt);
        if (ids.isPresent()){
            return sqlRepo.findAllById(ids.get());
        }
        return Optional.empty();
    }
}
