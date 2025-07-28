package film_sucher.catalog;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import film_sucher.catalog.repository.ElasticRepo;

@TestConfiguration
public class ElasticRepoMockConfig {
    
    @Bean
    @Primary
    public ElasticRepo elasticRepo(){
        return Mockito.mock(ElasticRepo.class);
    }
}
