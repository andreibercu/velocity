package com.abercu.velocity.birds.service;

import com.abercu.velocity.birds.dto.BirdDto;
import com.abercu.velocity.birds.entity.BirdDocument;
import com.abercu.velocity.birds.mapper.BirdMapper;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElasticBirdService {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    public Page<BirdDocument> fuzzySearch(
            String term, int page, int size, String sortBy, String sortDirection) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy + ".keyword"));

//        Criteria criteria = new Criteria("name").fuzzy(term)
//                .or(new Criteria("color").fuzzy(term));
//
//        Query query = new CriteriaQuery(criteria, pageable);

        QueryBuilder query = createQuery(term);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(query)
                .withPageable(pageable)
                .build();

        SearchHits<BirdDocument> hits = elasticsearchTemplate.search(searchQuery, BirdDocument.class);
        List<BirdDocument> results = hits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, hits.getTotalHits());
    }

    private QueryBuilder createQuery(String term) {
        if (term != null && !term.isBlank()) {
            return QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("name", term).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("color", term).fuzziness(Fuzziness.AUTO));
        }

        return QueryBuilders.matchAllQuery();
    }
}
