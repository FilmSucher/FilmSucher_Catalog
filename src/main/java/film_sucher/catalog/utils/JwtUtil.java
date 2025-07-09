package film_sucher.catalog.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import film_sucher.catalog.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@ConfigurationProperties(prefix="token")
@Component
public class JwtUtil {
    private final static String secret = "super_duper_secret_testkey_filler";
    
    public static User getUserFromToken(String token){
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // delete "Bearer "
            
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            String username = claims.getSubject();
            Long id = claims.get("id", Integer.class).longValue();
            String roleString = claims.get("role", String.class);
            
            return new User(id, username, User.Role.valueOf(roleString));
        }

        throw new IllegalArgumentException("Invalid Authorization header format");
    }
}
