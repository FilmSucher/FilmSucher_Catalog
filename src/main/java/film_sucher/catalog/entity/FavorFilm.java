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
import lombok.NoArgsConstructor;

@Entity
@Table(name="Favorites")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavorFilm {
    // composite key entity reference
    @EmbeddedId
    private FavorFilmId id;

    // one movie for many records
    @ManyToOne
    // alias for FavorFilmId
    @MapsId("filmId")
    // column as reference
    @JoinColumn(name = "film_id")
    private Film film;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    // column as normal column
    @Column(name="added_at")
    private LocalDateTime addedAt = LocalDateTime.now();
}

// надо убедиться что именно будет в колонке
// и если это прям фильм как фильм, то в сервисе убрать блок запроса в базу фильмов
// кроме того создать мок эластика в тестах и добавить в тест контекста