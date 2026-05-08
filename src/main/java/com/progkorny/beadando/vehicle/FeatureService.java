package com.progkorny.beadando.vehicle;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class FeatureService {

    private final FeatureRepository featureRepository;

    public FeatureService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    public List<Feature> getAll() {
        return featureRepository.findAll();
    }

    public Optional<Feature> getById(Long id) {
        return featureRepository.findById(id);
    }

    public List<Feature> getByIds(Set<Long> ids) {
        return featureRepository.findAllById(ids);
    }

    public Feature save(Feature feature) {
        return featureRepository.save(feature);
    }

    public void deleteById(Long id) {
        featureRepository.deleteById(id);
    }
}
