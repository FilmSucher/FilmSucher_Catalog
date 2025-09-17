package film_sucher.catalog.utils;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import film_sucher.catalog.entity.User;

@ConfigurationProperties(prefix="token")
@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    // variant if token is as argument
    // -------------------------------
    // public static User getUserFromToken(String token){
    //     if (token != null && token.startsWith("Bearer ")) {
    //         token = token.substring(7); // delete "Bearer "
            
    //         Claims claims = Jwts.parserBuilder()
    //             .setSigningKey(secret.getBytes())
    //             .build()
    //             .parseClaimsJws(token)
    //             .getBody();
            
    //         String username = claims.getSubject();
    //         Long id = claims.get("id", Integer.class).longValue();
    //         String roleString = claims.get("role", String.class);
            
    //         return new User(id, username, User.Role.valueOf(roleString));
    //     }

    //     throw new IllegalArgumentException("Invalid Authorization header format");
    // }

    // from SecurityContextHolder
    // ---------------------------
    public User getUserFromToken(){
        // name and id
        Map<String, Object> principal = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("Getted principals");
        Long id = ((Integer) principal.get("id")).longValue();
        String username = ((String) principal.get("username"));
        logger.info("Getted id and username");

        // role
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        logger.info("Getted authorities");
        String roleString = (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) ? "ADMIN" : "USER";
        logger.info("Getted role");
            
        return new User(id, username, User.Role.valueOf(roleString));
    }
}
