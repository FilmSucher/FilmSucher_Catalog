package film_sucher.catalog.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponseDTO {
    public String message;
    public Exception e;
    public HttpStatus status;
}
