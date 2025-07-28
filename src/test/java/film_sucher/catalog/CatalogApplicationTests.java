package film_sucher.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ActiveProfiles;

import film_sucher.catalog.repository.ElasticRepo;

@SpringBootTest
@ActiveProfiles("test")
class CatalogApplicationTests {
	@MockBean
    private ElasticRepo elasticRepo;
	@MockBean
    private ElasticsearchOperations elasticsearchOperations;

	@Test
	void contextLoads() {
	}
}
