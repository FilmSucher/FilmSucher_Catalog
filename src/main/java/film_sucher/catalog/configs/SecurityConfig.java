package film_sucher.catalog.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import film_sucher.catalog.security.JWTFilter;
import film_sucher.catalog.security.JWTProps;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTProps jwtProps;
    public SecurityConfig(JWTProps jwtProps){
        this.jwtProps = jwtProps;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .csrf(csrfCustomizer -> csrfCustomizer.disable())
            .authorizeHttpRequests(authorizeHttpRequestsCustomizer ->
                    authorizeHttpRequestsCustomizer
                    .requestMatchers("/films/**").permitAll()
                    .requestMatchers("/favorites/**").authenticated()
                    .requestMatchers("/admin_films/**").hasRole("ADMIN")
                    // other is closed
                    .anyRequest().denyAll()  
            )
            .addFilterBefore(new JWTFilter(jwtProps), UsernamePasswordAuthenticationFilter.class);   
        return http.build();
    }

}
