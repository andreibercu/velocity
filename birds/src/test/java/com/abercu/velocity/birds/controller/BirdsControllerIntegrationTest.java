package com.abercu.velocity.birds.controller;

import com.abercu.velocity.birds.dto.BirdDto;
import com.abercu.velocity.birds.entity.BirdEntity;
import com.abercu.velocity.birds.repository.BirdRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BirdsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BirdRepository birdRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void cleanUp() {
        birdRepository.deleteAll();
    }

    @Test
    void createBird_Ok() throws Exception {
        BirdDto birdDto = new BirdDto();
        birdDto.setName("bird-A");
        birdDto.setColor("green");
        birdDto.setHeight(10.0);
        birdDto.setWeight(10.0);

        String response = mockMvc.perform(post("/birds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(birdDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("bird-A"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BirdDto created = objectMapper.readValue(response, BirdDto.class);

        mockMvc.perform(get("/birds/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("bird-A"));
    }

    @Test
    void getAllBirds_Ok() throws Exception {
        BirdDto bird = new BirdDto();
        bird.setName("bird-A");
        bird.setColor("green");
        bird.setHeight(10.0);
        bird.setWeight(10.0);

        birdRepository.save(objectMapper.convertValue(bird, BirdEntity.class));

        mockMvc.perform(get("/birds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].name").value("bird-A"));
    }

    @Test
    void deleteBirdById() throws Exception {
        BirdDto birdDto = new BirdDto();
        birdDto.setName("bird-A");
        birdDto.setColor("green");
        birdDto.setWeight(10.0);
        birdDto.setHeight(10.0);

        String response = mockMvc.perform(post("/birds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(birdDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BirdDto created = objectMapper.readValue(response, BirdDto.class);

        mockMvc.perform(delete("/birds/{id}", created.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/birds/{id}", created.getId()))
                .andExpect(status().isBadRequest());
    }
}
