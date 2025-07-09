package film_sucher.catalog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private Role role;

    public enum Role {
        ADMIN, USER
    }
}
