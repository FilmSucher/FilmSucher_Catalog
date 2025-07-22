package film_sucher.catalog.exceptions;

public class ElasticException extends RuntimeException{
    public ElasticException(String message, Throwable cause){
        super(message, cause);
    }   
}
