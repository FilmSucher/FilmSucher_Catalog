package film_sucher.catalog.security;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class JWTFilter extends OncePerRequestFilter{

    private final JWTProps jwtProps;

    public JWTFilter(JWTProps jwtProps) {
        this.jwtProps = jwtProps;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException{
        // get authorization header 
        String header = request.getHeader("Authorization");

        // Check true header
        if (header == null || !header.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // get pure token
        String token = header.substring(7);

        try {
            // parse and get claims
            Claims claims = parseToken(token);

            // get props
            // id and name in principal    
            Map<String,Object> principal = new HashMap<>();
            principal.put("id", (Integer) claims.get("id"));
            principal.put("username", claims.getSubject());

            // roles in authorities
            List<String> roles = (List<String>) claims.get("roles");
            // List of String to List of Rights
            // Преобразуем список строк в список прав (GrantedAuthority)
            List<GrantedAuthority> authorities = new ArrayList<>();
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }

            // make Auth-object
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);

            // set im context
            SecurityContextHolder.getContext().setAuthentication(auth);
        } 
        catch (JwtException | IllegalArgumentException e) {
            // clear context, if token is invalid
            SecurityContextHolder.clearContext();
        }
        // continue request-process
        filterChain.doFilter(request, response);
    }

    private Claims parseToken(String token) throws JwtException{
        Key key = Keys.hmacShaKeyFor(jwtProps.getSecret().getBytes());
        return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    }
}

// достать ИД из контекста
// Map<String, Object> principal = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
// Long userId = ((Integer) principal.get("id")).longValue();
