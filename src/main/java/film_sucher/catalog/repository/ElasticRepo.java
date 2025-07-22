package film_sucher.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import film_sucher.catalog.entity.ElasticFilm;

@Repository
public interface ElasticRepo extends ElasticsearchRepository<ElasticFilm, Long>{
    
    @Query("SELECT * FROM Films")
    Optional<List<ElasticFilm>> findByPrompt(String prompt);
    // List<ProductDocument> findByNameContainingOrDescriptionContaining(String name, String description);
}
