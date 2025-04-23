package com.abercu.velocity.birds.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.Instant;

@Entity
@Table(name = "birds")
@Data
@NoArgsConstructor
public class BirdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment column in the database
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String color;

    @Positive
    @Nullable
    private Double weight;

    @Positive
    @Nullable
    private Double height;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private Long version; // used for optimistic locking

    // only if necessary
//    @OneToMany(mappedBy = "bird", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<SightingEntity> sightings = new ArrayList<>();
}
