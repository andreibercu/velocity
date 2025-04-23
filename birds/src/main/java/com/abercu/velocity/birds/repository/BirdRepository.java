package com.abercu.velocity.birds.repository;

import com.abercu.velocity.birds.entity.BirdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BirdRepository extends JpaRepository<BirdEntity, Long>, JpaSpecificationExecutor<BirdEntity> {

    boolean existsByName(String name);
}
