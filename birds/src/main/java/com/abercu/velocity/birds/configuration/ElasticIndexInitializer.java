package com.abercu.velocity.birds.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class ElasticIndexInitializer {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public ElasticIndexInitializer(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @PostConstruct
    public void initIndices() {
        log.info("Verifying elasticsearch indexes");

        createIndexIfNotExists("birds", "/elasticsearch/birds-mapping.json");
        createIndexIfNotExists("sightings", "/elasticsearch/sightings-mapping.json");
    }

    private void createIndexIfNotExists(String indexName, String mappingPath) {
        IndexOperations indexOps = elasticsearchTemplate.indexOps(IndexCoordinates.of(indexName));

        if (indexOps.exists()) {
            return;
        }

        log.info("Initializing index {}, based on mapping json: {}", indexName, mappingPath);
        indexOps.create();

        try (InputStream input = new ClassPathResource(mappingPath).getInputStream()) {
            Map<String, Object> mappingMap = mapper.readValue(input, Map.class);
            Map<String, Object> properties = (Map<String, Object>) mappingMap.get("mappings");
            Document mappingDoc = Document.from(properties);

            indexOps.putMapping(mappingDoc);
        } catch (Exception e) {
            log.error("Error initializing index, exc:", e);
        }
    }
}
