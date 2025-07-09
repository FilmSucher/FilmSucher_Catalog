package film_sucher.catalog.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Embeddable
public class FavorFilmId implements Serializable{
    @Column(name="user_id")
    private Long userId;

    @Column(name="film_id")
    private Long filmId;
}
