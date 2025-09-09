package film_sucher.catalog.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import film_sucher.catalog.entity.Film;

@Repository
public interface SqlSuchRepo extends CrudRepository<Film, Long>{
    List<Film> findAllByOrderByRatingDesc();
}
