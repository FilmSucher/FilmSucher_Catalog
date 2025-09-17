package film_sucher.catalog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import film_sucher.catalog.entity.ElasticFilm;
import film_sucher.catalog.entity.Film;
import film_sucher.catalog.exceptions.DatabaseException;
import film_sucher.catalog.exceptions.ElasticException;
import film_sucher.catalog.repository.ElasticRepo;
import film_sucher.catalog.repository.SqlSuchRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class AdminService {
    private final SqlSuchRepo sqlRepo;
    private final ElasticRepo elRepo;

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    public AdminService(SqlSuchRepo sqlRepo, ElasticRepo elRepo){
        this.sqlRepo = sqlRepo;
        this.elRepo = elRepo;
    }

    // make new
    @Transactional
    public void addFilm(Film newFilm){
        // save in DB
        Film savedFilm;
        try{
            savedFilm = sqlRepo.save(newFilm);
            logger.info("New movie successfull saved in Postgres");
        }
        catch(DataAccessException e){
            logger.warn("New movie not saved in Postgres");
            throw new DatabaseException("Error saving movie to DB", e);
        }

        //set in Elastic
        try{
            elRepo.save(new ElasticFilm(
                savedFilm.getId(), 
                savedFilm.getTitle(), 
                savedFilm.getDescription(), 
                savedFilm.getGenre(), 
                savedFilm.getCountry()
                )
            ); 
            logger.info("New movie successfull saved in Elastic");
        }
        catch(RuntimeException e){
            logger.warn("New movie not saved in Elastic");
            throw new ElasticException("Error saving movie to Elastic", e);
        }
    }
    
    // change
    @Transactional
    public void changeFilm(Long id, Film film){
        Film updatedFilm;
        try{
            // check in DB
            if (!sqlRepo.existsById(id)) throw new EntityNotFoundException("Film with id: " + id + " not found");
            // set really ID
            film.setId(id);
    
            // try update in DB
            updatedFilm = sqlRepo.save(film);
            logger.info("Changed movie successfull saved in Postgres");
        }
        catch (DataAccessException e){
            logger.warn("Changed movie not saved in Postgres");
            throw new DatabaseException("Error saving movie to PostgresSQL", e);
        }
        // try update in Elastic
        try{
            elRepo.save(new ElasticFilm(
                updatedFilm.getId(), 
                updatedFilm.getTitle(), 
                updatedFilm.getDescription(), 
                updatedFilm.getGenre(), 
                updatedFilm.getCountry()
                )
            ); 
            logger.info("Changed movie successfull saved in Elastic");
        }
        catch (RuntimeException e){
            logger.warn("Changed movie not saved in Elastic");
            throw new ElasticException("Error updating movie to ElasticSearch", e);
        }
    }

    // delete
    @Transactional
    public void delFilm(Long id){
        try{
            // check in DB
            if (!sqlRepo.existsById(id)) throw new EntityNotFoundException("Film with id: " + id + " not found");
            // try update in DB
            sqlRepo.deleteById(id);
            logger.info("Movie successfull deleted in Postgres");
        }
        catch (DataAccessException e){
            logger.warn("Movie not deleted in Postgres");
            throw new DatabaseException("Error deleting movie from PostgresSQL", e);
        }
        
        // try update in Elastic
        try{
            elRepo.deleteById(id);
            logger.info("Movie successfull deleted in Elastic");
        }
        catch (RuntimeException e){
            logger.warn("Movie not deleted in Elastic");
            throw new ElasticException("Error deleting movie from Elastic", e);
        }
    }
}
