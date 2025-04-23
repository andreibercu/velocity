package com.abercu.velocity.birds.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Document(indexName = "sightings")
@Data
@NoArgsConstructor
public class SightingDocument {

    @Id
    private String id;

    // embedded bird data for fuzzy search
    @Field(type = FieldType.Object)
    private BirdDocument bird;

    @Field(type = FieldType.Text)
    private String location;

    @Field(type = FieldType.Date)
    private Instant datetime;

    @Field(type = FieldType.Date)
    private Instant createdAt;

    @Field(type = FieldType.Date)
    private Instant updatedAt;
}
