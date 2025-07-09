package film_sucher.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import film_sucher.catalog.entity.Film;

public interface SqlSuchRepo extends CrudRepository<Film, Long>{

    public Optional<List<Film>> findAllById(List<Long> id);
}
