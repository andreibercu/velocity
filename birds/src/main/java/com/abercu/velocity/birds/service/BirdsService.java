package com.abercu.velocity.birds.service;

import com.abercu.velocity.birds.dto.BirdDto;
import com.abercu.velocity.birds.entity.BirdEntity;
import com.abercu.velocity.birds.entity.SightingDocument;
import com.abercu.velocity.birds.mapper.BirdMapper;
import com.abercu.velocity.birds.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.abercu.velocity.birds.configuration.RedisCacheConfig.BIRDS_CACHE;
import static com.abercu.velocity.birds.configuration.RedisCacheConfig.BIRDS_SEARCH_CACHE;

@Slf4j
@Service
@DependsOn("elasticIndexInitializer")
@RequiredArgsConstructor
public class BirdsService {

    private final BirdRepository birdRepository;
    private final SightingRepository sightingRepository;
    private final ElasticBirdRepository elasticBirdRepository;
    private final ElasticSightingRepository elasticSightingRepository;
    private final BirdMapper mapper;

    @Transactional(readOnly = true)
    @Cacheable(value = BIRDS_SEARCH_CACHE,
            key = "T(java.util.Objects).hash(#name, #color, #pageable.pageNumber, #pageable.pageSize, #pageable.sort)",
            unless = "#result.isEmpty()")
    public Page<BirdDto> getBirds(String name, String color, Pageable pageable) {
        Specification<BirdEntity> spec = Specification.where(null);

        if (isNotBlank(name)) {
            spec = spec.and(BirdSpecifications.nameContains(name));
        }

        if (isNotBlank(color)) {
            spec = spec.and(BirdSpecifications.colorContains(color));
        }

        return birdRepository.findAll(spec, pageable)
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = BIRDS_CACHE, key = "#id")
    public Optional<BirdDto> getBird(Long id) {
        return birdRepository.findById(id)
                .map(mapper::toDto);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = BIRDS_SEARCH_CACHE, allEntries = true)
    })
    public BirdDto createBird(BirdDto dto) {
        if (birdRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException(String.format("A bird with name '%s' already exists", dto.getName()));
        }

        BirdEntity entity = birdRepository.save(mapper.toEntity(dto));

        // save to elasticsearch
        try {
            elasticBirdRepository.save(mapper.toBirdDocument(entity));
        } catch (Exception e) {
            log.warn("Failed to save bird {} to Elasticsearch", entity.getId(), e);
        }

        return mapper.toDto(entity);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = BIRDS_CACHE, key = "#id"),
            @CacheEvict(value = BIRDS_SEARCH_CACHE, allEntries = true)
    })
    public BirdDto updateBird(Long id, BirdDto birdDto) {
        BirdEntity existing = birdRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Bird not found with id '%s'", id)));

        // if changed, validate new name is still unique
        if (isNotBlank(birdDto.getName())) {
            if (!birdDto.getName().equals(existing.getName()) && birdRepository.existsByName(birdDto.getName())) {
                throw new IllegalArgumentException(String.format("A bird with name '%s' already exists", birdDto.getName()));
            }

            existing.setName(birdDto.getName());
        }

        // update only non-null fields
        if (isNotBlank(birdDto.getColor())) existing.setColor(birdDto.getColor());
        if (birdDto.getWeight() != null) existing.setWeight(birdDto.getWeight());
        if (birdDto.getHeight() != null) existing.setHeight(birdDto.getHeight());

        // possible alternative for updating only non-null fields:
        // birdMapper.updateEntityFromDto(birdDto, existing);

        BirdEntity entity = birdRepository.save(existing);

        // save Bird in elasticsearch
        try {
            elasticBirdRepository.save(mapper.toBirdDocument(entity));

            // update affected Sightings
            List<SightingDocument> affectedSightings = sightingRepository.findByBirdId(id)
                    .stream()
                    .map(mapper::toDocument)
                    .collect(Collectors.toList());

            elasticSightingRepository.saveAll(affectedSightings);
        } catch (Exception e) {
            log.warn("Failed to save bird {} and affected sightings to Elasticsearch", entity.getId(), e);
        }

        return mapper.toDto(entity);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = BIRDS_CACHE, key = "#id"),
            @CacheEvict(value = BIRDS_SEARCH_CACHE, allEntries = true)
    })
    public void deleteBird(Long id) {
        if (!birdRepository.existsById(id)) {
            throw new IllegalArgumentException(String.format("Bird not found with id '%s'", id));
        }

        birdRepository.deleteById(id);

        // delete from elasticsearch
        try {
            elasticBirdRepository.deleteById(id.toString());

            // delete affected Sightings
            List<String> affectedSightingIds = sightingRepository.findByBirdId(id)
                    .stream()
                    .map(entity -> entity.getId().toString())
                    .collect(Collectors.toList());

            elasticSightingRepository.deleteAllById(affectedSightingIds);
        } catch (Exception e) {
            log.warn("Failed to delete bird {} from Elasticsearch", id, e);
        }
    }

    boolean isNotBlank(String str) {
        return str != null && !str.isBlank();
    }
}
