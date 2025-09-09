package film_sucher.catalog.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Document(indexName="films")
public class ElasticFilm {
    @Id
    private Long id;
    
    @Field(type = FieldType.Text, analyzer = "standart")
    private String title;
    // automatish default settings
    private String description;
    private String genre;
    private String country;
}
