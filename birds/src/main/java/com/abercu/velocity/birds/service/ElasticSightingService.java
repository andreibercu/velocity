package com.abercu.velocity.birds.service;

import com.abercu.velocity.birds.entity.SightingDocument;
import com.abercu.velocity.birds.mapper.BirdMapper;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ElasticSightingService {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    public Page<SightingDocument> fuzzySearch(String termExpression, Instant start, Instant end,
                                              int page, int size, String sortBy, String sortDirection) {

        BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();

        if (termExpression != null && !termExpression.isBlank()) {
            List<String> terms = Stream.of(termExpression.split(","))
                    .filter(term -> !term.isBlank())
                    .map(String::trim)
                    .collect(Collectors.toList());

            for (String term : terms) {
                finalQuery.must(QueryBuilders
                        .multiMatchQuery(term, "location", "bird.name", "bird.color")
                        .fuzziness(Fuzziness.AUTO));
            }
        }

        if (start != null || end != null) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("datetime");
            if (start != null)
                rangeQuery.gte(start.toString());
            if (end != null)
                rangeQuery.lte(end.toString());

            finalQuery.must(rangeQuery);
        }

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy + ".keyword"));

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(finalQuery)
                .withPageable(pageable)
                .build();

        SearchHits<SightingDocument> searchHits = elasticsearchTemplate.search(searchQuery, SightingDocument.class);

        List<SightingDocument> results = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, searchHits.getTotalHits());
    }
}
