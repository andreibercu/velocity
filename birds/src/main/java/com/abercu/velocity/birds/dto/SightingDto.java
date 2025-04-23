package com.abercu.velocity.birds.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
public class SightingDto implements Serializable {

    private Long id;

    @NotNull(groups = OnCreate.class)
    @Positive
    private Long birdId;

    @NotBlank(groups = OnCreate.class)
    private String location;

    @NotNull(groups = OnCreate.class)
    private Instant datetime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String birdName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String birdColor;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant updatedAt;
}
