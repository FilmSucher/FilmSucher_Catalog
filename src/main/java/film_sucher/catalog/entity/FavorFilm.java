package film_sucher.catalog.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name="Favorites")
@Data
@AllArgsConstructor
public class FavorFilm {
    // composite key entity reference
    @EmbeddedId
    private FavorFilmId id;

    // one movie for many records
    @ManyToOne
    // alias for FavorFilmId
    @MapsId("filmId")
    // column as reference
    @JoinColumn(name = "filmId")
    private Long filmId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "userId")
    private Long userId;

    // column as normal column
    @Column(name="addedAt")
    private LocalDateTime addedAt =  LocalDateTime.now();
}
