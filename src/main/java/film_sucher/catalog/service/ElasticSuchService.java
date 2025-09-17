package film_sucher.catalog.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import film_sucher.catalog.entity.ElasticFilm;


// Service for komplex query to Elasticsearch
@Service
public class ElasticSuchService {
    private final ElasticsearchOperations elasticsearchOperations;

    private static final Logger logger = LoggerFactory.getLogger(ElasticSuchService.class);

    public ElasticSuchService(ElasticsearchOperations elasticsearchOperations){
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<ElasticFilm> search (String queryText){
        // Query-object builder (match query)
        MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(
            mmQuery -> mmQuery
                .query(queryText)
                .fields("title^2", "description", "genre", "country")
                .type(TextQueryType.BestFields)
        );

        // Universal container for any query: match, bool, range, term
        Query query = Query.of(q -> q.multiMatch(multiMatchQuery));

        // Container for highlights, sorting, pagination...
        NativeQuery nativeQuery = NativeQuery.builder()
                                    .withQuery(query)
                                    .build();
        
        logger.info("Objects and containers builded to query in Elastic");

        // Query GO!
        // return container with Films and Metadata
        SearchHits<ElasticFilm> searchHits = elasticsearchOperations.search(nativeQuery, ElasticFilm.class);
        logger.info("Container with Films and Meta successfully getted from Elastic");
        
        // Parse results
        List<ElasticFilm> results = new ArrayList<>();
        for (SearchHit<ElasticFilm> hit : searchHits){
            results.add(hit.getContent());
        }
        logger.info("Results from Elastic successfully parsed");
        return results;
    }
}
