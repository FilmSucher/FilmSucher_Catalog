package film_sucher.catalog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="Films")
public class Film {
    @Id
    private Long id;
    private String title;
    private int year;
    private String ganre;
    private int duration;
    private String country;
    private String description;
    private String bildUrl;
    private long rating;
}
