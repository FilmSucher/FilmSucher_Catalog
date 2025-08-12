package film_sucher.catalog.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="films")
@NoArgsConstructor
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "films_seq_gen")
    @SequenceGenerator(name = "films_seq_gen", sequenceName = "public.films_seq", allocationSize = 1)
    private Long id;
    private String title;
    private int year;
    private String genre;
    private int duration;
    private String country;
    private String description;
    @Column(name="bild_url")
    private String bildUrl;
    private BigDecimal rating;
}
