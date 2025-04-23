package com.abercu.velocity.birds.repository;

import com.abercu.velocity.birds.entity.BirdEntity;
import org.springframework.data.jpa.domain.Specification;

public interface BirdSpecifications {

    /**
     *
     * @param name given_name
     * @return where lower(name) like '%given_name%'
     */
    static Specification<BirdEntity> nameContains(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    /**
     *
     * @param color given_color
     * @return where lower(color) like '%given_color%'
     */
    static Specification<BirdEntity> colorContains(String color) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("color")), "%" + color.toLowerCase() + "%");
    }
}
