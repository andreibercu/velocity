package com.abercu.velocity.birds.service;

import com.abercu.velocity.birds.dto.BirdDto;
import com.abercu.velocity.birds.entity.BirdEntity;
import com.abercu.velocity.birds.mapper.BirdMapper;
import com.abercu.velocity.birds.repository.BirdRepository;
import com.abercu.velocity.birds.repository.ElasticBirdRepository;
import com.abercu.velocity.birds.repository.ElasticSightingRepository;
import com.abercu.velocity.birds.repository.SightingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BirdsServiceTest {

    @Mock
    private BirdRepository birdRepository;

    @Mock
    private SightingRepository sightingRepository;

    @Mock
    private ElasticBirdRepository elasticBirdRepository;

    @Mock
    private ElasticSightingRepository elasticSightingRepository;

    @Mock
    private BirdMapper mapper;

    @InjectMocks
    private BirdsService birdsService;

    @Test
    public void createBird_Ok() {
        BirdDto dto = new BirdDto();
        dto.setName("bird-A");
        dto.setColor("blue");

        BirdEntity entity = new BirdEntity();
        entity.setName("bird-A");
        entity.setColor("blue");

        BirdEntity savedEntity = new BirdEntity();
        savedEntity.setId(1L);
        savedEntity.setName("bird-A");
        savedEntity.setColor("blue");

        BirdDto resultDto = new BirdDto();
        resultDto.setId(1L);
        resultDto.setName("bird-A");
        resultDto.setColor("blue");

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(birdRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(resultDto);

        BirdDto created = birdsService.createBird(dto);

        assertEquals(1L, created.getId());
        assertEquals("bird-A", created.getName());
    }

    @Test
    public void updateBird_shouldUpdateAndReturnDto() {
        BirdEntity existing = new BirdEntity();
        existing.setId(1L);
        existing.setName("bird-A");
        existing.setColor("green");

        BirdDto update = new BirdDto();
        update.setColor("red");

        BirdEntity updated = new BirdEntity();
        updated.setId(1L);
        updated.setName("bird-A");
        updated.setColor("red");

        BirdDto resultDto = new BirdDto();
        resultDto.setId(1L);
        resultDto.setName("bird-A");
        resultDto.setColor("red");

        when(birdRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(birdRepository.save(existing)).thenReturn(updated);
        when(mapper.toDto(updated)).thenReturn(resultDto);

        BirdDto result = birdsService.updateBird(1L, update);

        assertEquals(1L, result.getId());
        assertEquals("bird-A", result.getName());
        assertEquals("red", result.getColor());
    }

    @Test
    public void getBirdById_Ok() {
        BirdEntity entity = new BirdEntity();
        entity.setId(1L);
        entity.setName("bird-A");

        BirdDto dto = new BirdDto();
        dto.setId(1L);
        dto.setName("bird-A");

        when(birdRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        Optional<BirdDto> optionalResult = birdsService.getBird(1L);

        assertTrue(optionalResult.isPresent());
        BirdDto result = optionalResult.get();
        assertEquals(1L, result.getId());
        assertEquals("bird-A", result.getName());
    }

    @Test
    public void deleteBird_Ok() {
        when(birdRepository.existsById(1L)).thenReturn(true);

        birdsService.deleteBird(1L);

        verify(birdRepository).deleteById(1L);
    }
}
