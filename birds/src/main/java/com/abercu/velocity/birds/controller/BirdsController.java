package com.abercu.velocity.birds.controller;

import com.abercu.velocity.birds.dto.BirdDto;
import com.abercu.velocity.birds.dto.OnCreate;
import com.abercu.velocity.birds.dto.OnUpdate;
import com.abercu.velocity.birds.entity.BirdDocument;
import com.abercu.velocity.birds.service.BirdsService;
import com.abercu.velocity.birds.service.ElasticBirdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/birds")
@Tag(name = "Birds", description = "CRUD Operations on Birds")
@RequiredArgsConstructor
public class BirdsController {

    // todo abercu - add unit tests and integration tests
    // todo abercu - test - dockerfile and docker-compose.yml

    private final BirdsService birdsService;
    private final ElasticBirdService elasticBirdService;

    @GetMapping
    @Operation(summary = "Get birds, optionally by name and color")
    public Page<BirdDto> getBirds(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("rest api call - getting birds page: {}", pageable);

        return birdsService.getBirds(name, color, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bird by Id")
    public ResponseEntity<BirdDto> getBird(@PathVariable Long id) {
        log.info("rest api call - getting bird by id: {}", id);

        return birdsService.getBird(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create bird")
    public BirdDto createBird(@Validated(OnCreate.class) @RequestBody BirdDto bird) {
        log.info("rest api call - creating bird: {}", bird);

        return birdsService.createBird(bird);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing bird")
    public BirdDto updateBird(@PathVariable Long id, @Validated(OnUpdate.class) @RequestBody BirdDto bird) {
        log.info("rest api call - updating bird: {}", bird);

        return birdsService.updateBird(id, bird);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete bird by ID")
    public void deleteBird(@PathVariable Long id) {
        log.info("rest api call - deleting bird by id: {}", id);

        birdsService.deleteBird(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Fuzzy search birds")
    public Page<BirdDocument> fuzzySearch(
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        log.info("rest api call - fuzzy search birds by term: {}, page: {}, page-size: {}, sortBy: {},{}",
                term != null ? term : "*", page, size, sortBy, sortDirection);

        return elasticBirdService.fuzzySearch(term, page, size, sortBy, sortDirection);
    }
}
