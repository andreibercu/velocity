package com.abercu.velocity.birds.controller;

import com.abercu.velocity.birds.dto.OnCreate;
import com.abercu.velocity.birds.dto.OnUpdate;
import com.abercu.velocity.birds.dto.SightingDto;
import com.abercu.velocity.birds.entity.SightingDocument;
import com.abercu.velocity.birds.service.ElasticSightingService;
import com.abercu.velocity.birds.service.SightingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/sightings")
@Tag(name = "Sightings", description = "CRUD Operations on Sightings")
@RequiredArgsConstructor
public class SightingsController {

    private final SightingService sightingService;
    private final ElasticSightingService elasticSightingService;

    @GetMapping
    @Operation(summary = "Get sightings, optionally by bird.Id, location and time interval")
    public Page<SightingDto> getSightings(
            @RequestParam(required = false) Long birdId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start, // expect a string like 2025-04-15T10:30:00Z
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("rest api call - getting sightings for bird: {}, location: {}, startTime: {}, endTime: {}, page: {}",
                birdId != null ? birdId : "*",
                location != null ? location : "*",
                start != null ? start : "*",
                end != null ? end : "*",
                pageable);

        return sightingService.getSightings(birdId, location, start, end, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sighting by Id")
    public ResponseEntity<SightingDto> getSighting(@PathVariable Long id) {
        log.info("rest api call - getting sighting by id: {}", id);

        return sightingService.getSighting(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Add new sightings")
    public SightingDto createSighting(@Validated(OnCreate.class) @RequestBody SightingDto sightingDto) {
        log.info("rest api call - creating sighting: {}", sightingDto);

        return sightingService.createSighting(sightingDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing sighting")
    public SightingDto updateSighting(@PathVariable Long id, @Validated(OnUpdate.class) @RequestBody SightingDto sightingDto) {
        log.info("rest api call - updating sighting: {}", sightingDto);

        return sightingService.updateSighting(id, sightingDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sighting by Id")
    public void deleteSighting(@PathVariable Long id) {
        log.info("rest api call - deleting sighting by id: {}", id);

        sightingService.deleteSighting(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Fuzzy search sightings")
    public Page<SightingDocument> fuzzySearch(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "location") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        log.info("rest api call - fuzzy search sightings by term: '{}', start: {}, end: {}, page: {}, page-size: {}, sortBy: {},{}",
                term != null ? term : "*", start != null ? start : "*", end != null ? end : "*",
                page, size, sortBy, sortDirection);

        return elasticSightingService.fuzzySearch(term, start, end, page, size, sortBy, sortDirection);
    }
}
