package com.abercu.velocity.birds.repository;

import com.abercu.velocity.birds.entity.SightingEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public interface SightingSpecifications {

    /**
     *
     * @param birdId given_birdId
     * @return where bird_id = 'given_birdId'
     */
    static Specification<SightingEntity> hasBirdId(Long birdId) {
        return (root, query, cb) -> cb.equal(root.get("bird").get("id"), birdId);
    }

    /**
     *
     * @param location given_location
     * @return where lower(location) like '%given_location%'
     */
    static Specification<SightingEntity> locationContains(String location) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }

    /**
     *
     * @param start given_start
     * @return where datetime >= given_start
     */
    static Specification<SightingEntity> datetimeAfter(Instant start) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("datetime"), start);
    }

    /**
     *
     * @param end given_end
     * @return where datetime <= given_end
     */
    static Specification<SightingEntity> datetimeBefore(Instant end) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("datetime"), end);
    }
}
