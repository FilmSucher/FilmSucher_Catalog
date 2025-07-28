package film_sucher.catalog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@AllArgsConstructor
public class User {
    @Id
    private Long id;
    private String username;
    private Role role;

    public enum Role {
        ADMIN, USER
    }
}
