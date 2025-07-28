package film_sucher.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="Films")
public class Film {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String title;
    private int year;
    private String genre;
    private int duration;
    private String country;
    private String description;
    @Column(name="bild_url")
    private String bildUrl;
    private long rating;
}
