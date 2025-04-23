package com.abercu.velocity.birds.service;

import com.abercu.velocity.birds.dto.SightingDto;
import com.abercu.velocity.birds.entity.SightingEntity;
import com.abercu.velocity.birds.mapper.BirdMapper;
import com.abercu.velocity.birds.repository.BirdRepository;
import com.abercu.velocity.birds.repository.ElasticSightingRepository;
import com.abercu.velocity.birds.repository.SightingRepository;
import com.abercu.velocity.birds.repository.SightingSpecifications;
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

import java.time.Instant;
import java.util.Optional;

import static com.abercu.velocity.birds.configuration.RedisCacheConfig.*;

@Slf4j
@Service
@DependsOn("elasticIndexInitializer")
@RequiredArgsConstructor
public class SightingService {

    private final BirdRepository birdRepository;
    private final SightingRepository sightingRepository;
    private final ElasticSightingRepository elasticSightingRepository;
    private final BirdMapper mapper;

    @Transactional(readOnly = true)
    @Cacheable(value = SIGHTINGS_SEARCH_CACHE,
            key = "T(java.util.Objects).hash(#birdId, #location, #start, #end, #pageable.pageNumber, #pageable.pageSize, #pageable.sort)", unless = "#result.isEmpty()")
    public Page<SightingDto> getSightings(
            Long birdId, String location, Instant start, Instant end, Pageable pageable) {

        Specification<SightingEntity> spec = Specification.where(null);

        if (birdId != null) {
            spec = spec.and(SightingSpecifications.hasBirdId(birdId));
        }

        if (isNotBlank(location)) {
            spec = spec.and(SightingSpecifications.locationContains(location));
        }

        if (start != null) {
            spec = spec.and(SightingSpecifications.datetimeAfter(start));
        }

        if (end != null) {
            spec = spec.and(SightingSpecifications.datetimeBefore(end));
        }

        return sightingRepository.findAll(spec, pageable)
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = SIGHTINGS_CACHE, key = "#id")
    public Optional<SightingDto> getSighting(Long id) {
        return sightingRepository.findById(id)
                .map(mapper::toDto);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = SIGHTINGS_SEARCH_CACHE, allEntries = true)
    })
    public SightingDto createSighting(SightingDto dto) {
        if (!birdRepository.existsById(dto.getBirdId())) {
            throw new IllegalArgumentException(String.format("Bird not found with id '%s'", dto.getBirdId()));
        }

        SightingEntity sighting = mapper.toEntity(dto);

        // use a proxy entity for Bird, to not load the complete entity from the db
        sighting.setBird(birdRepository.getReferenceById(dto.getBirdId()));
        sighting = sightingRepository.save(sighting);

        // save to Elasticsearch
        try {
            elasticSightingRepository.save(mapper.toDocument(sighting));
        } catch (Exception e) {
            log.warn("Failed to save sighting {} to Elasticsearch", sighting.getId(), e);
        }

        return mapper.toDto(sighting);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = SIGHTINGS_CACHE, key = "#id"),
            @CacheEvict(value = SIGHTINGS_SEARCH_CACHE, allEntries = true)
    })
    public SightingDto updateSighting(Long id, SightingDto dto) {
        SightingEntity existing = sightingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Sighting not found with id '%s'", id)));

        // update only non-null fields
        if (dto.getBirdId() != null && !dto.getBirdId().equals(existing.getBird().getId())) {
            if (!birdRepository.existsById(dto.getBirdId())) {
                throw new IllegalArgumentException(String.format("Bird not found with id '%s'", dto.getBirdId()));
            }

            existing.setBird(birdRepository.getReferenceById(dto.getBirdId()));
        }

        if (isNotBlank(dto.getLocation())) existing.setLocation(dto.getLocation());
        if (dto.getDatetime() != null) existing.setDatetime(dto.getDatetime());

        SightingEntity sighting = sightingRepository.save(existing);

        // save to Elasticsearch
        try {
            elasticSightingRepository.save(mapper.toDocument(sighting));
        } catch (Exception e) {
            log.warn("Failed to save sighting {} to Elasticsearch", sighting.getId(), e);
        }

        return mapper.toDto(sighting);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = SIGHTINGS_CACHE, key = "#id"),
            @CacheEvict(value = SIGHTINGS_SEARCH_CACHE, allEntries = true)
    })
    public void deleteSighting(Long id) {
        if (!sightingRepository.existsById(id)) {
            throw new IllegalArgumentException(String.format("Sighting not found with id '%s'", id));
        }

        sightingRepository.deleteById(id);

        // delete from elasticsearch
        try {
            elasticSightingRepository.deleteById(id.toString());
        } catch (Exception e) {
            log.warn("Failed to delete sighting {} from Elasticsearch", id, e);
        }
    }

    boolean isNotBlank(String str) {
        return str != null && !str.isBlank();
    }
}
