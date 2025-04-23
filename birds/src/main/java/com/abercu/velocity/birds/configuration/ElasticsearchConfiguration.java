package com.abercu.velocity.birds.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

@Configuration
public class ElasticsearchConfiguration {

    @Bean("elasticIndexInitializer")
    public ElasticIndexInitializer elasticIndexInitializer(ElasticsearchRestTemplate elasticsearchTemplate) {
        return new ElasticIndexInitializer(elasticsearchTemplate);
    }
}
