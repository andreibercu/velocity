package com.abercu.velocity.birds.repository;

import com.abercu.velocity.birds.entity.SightingDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticSightingRepository extends ElasticsearchRepository<SightingDocument, String> {
}
