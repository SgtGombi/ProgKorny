package com.progkorny.beadando.feature;

import org.springframework.data.jpa.repository.JpaRepository;

// DB muveletek a feature entitasra
public interface FeatureRepository extends JpaRepository<Feature, Long> {
}
