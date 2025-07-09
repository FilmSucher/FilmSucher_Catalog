package film_sucher.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import film_sucher.catalog.entity.Film;

public interface ElasticRepo extends ElasticsearchRepository<Film, Long>{
    
    @Query("SELECT * FROM Films")
    public Optional<List<Long>> findByPrompt(String prompt);
}
