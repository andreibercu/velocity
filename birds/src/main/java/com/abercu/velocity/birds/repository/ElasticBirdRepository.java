package com.abercu.velocity.birds.repository;

import com.abercu.velocity.birds.entity.BirdDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticBirdRepository extends ElasticsearchRepository<BirdDocument, String> {}