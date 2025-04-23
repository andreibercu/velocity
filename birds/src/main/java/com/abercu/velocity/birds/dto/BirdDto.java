package com.abercu.velocity.birds.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
public class BirdDto implements Serializable {

    private Long id;

    @NotBlank(message = "Bird name is required and can't be blank", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "Bird color is required and can't be blank", groups = OnCreate.class)
    private String color;

    @Positive(message = "Weight must be greater than 0 if specified", groups = {OnCreate.class, OnUpdate.class})
    @Nullable
    private Double weight;

    @Positive(message = "Height must be greater than 0 if specified", groups = {OnCreate.class, OnUpdate.class})
    @Nullable
    private Double height;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant updatedAt;
}
