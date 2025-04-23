package com.abercu.velocity.birds.mapper;

import com.abercu.velocity.birds.dto.BirdDto;
import com.abercu.velocity.birds.dto.SightingDto;
import com.abercu.velocity.birds.entity.BirdDocument;
import com.abercu.velocity.birds.entity.BirdEntity;
import com.abercu.velocity.birds.entity.SightingDocument;
import com.abercu.velocity.birds.entity.SightingEntity;
import com.abercu.velocity.birds.service.SightingService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BirdMapper {

    // advanced mappings can be done with: @Mapping(source = "fieldA", target = "fieldB")

    // BIRDS

    BirdDto toDto(BirdEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BirdEntity toEntity(BirdDto dto);

    // use NullValuePropertyMappingStrategy.IGNORE to only map fields that are non-null in the source dto,
    // leaving the other fields unchanged; method below copies only non-null fields from dto into existing entity;
    void updateEntityFromDto(BirdDto dto, @MappingTarget BirdEntity entity);

    @Mapping(target = "id", expression = "java(entity.getId() != null ? String.valueOf(entity.getId()) : null)")
    BirdDocument toBirdDocument(BirdEntity entity);


    // SIGHTINGS

    @Mapping(source = "bird.id", target = "birdId")
    @Mapping(source = "bird.name", target = "birdName")
    @Mapping(source = "bird.color", target = "birdColor")
    SightingDto toDto(SightingEntity entity);

    @Mapping(target = "bird", ignore = true) // set in the SightingService
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SightingEntity toEntity(SightingDto dto);

    // todo abercu - make sure entity.bird maps correctly to document.bird
    @Mapping(target = "id", expression = "java(entity.getId() != null ? String.valueOf(entity.getId()) : null)")
    @Mapping(target = "bird", source = "bird")
    SightingDocument toDocument(SightingEntity entity);
}
