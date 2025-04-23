package com.abercu.velocity.birds.repository;

import com.abercu.velocity.birds.entity.SightingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SightingRepository extends JpaRepository<SightingEntity, Long>, JpaSpecificationExecutor<SightingEntity> {

    List<SightingEntity> findByBirdId(Long birdId);
}
