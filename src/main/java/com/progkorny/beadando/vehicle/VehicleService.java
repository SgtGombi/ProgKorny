package com.progkorny.beadando.vehicle;

import com.progkorny.beadando.feature.Feature;
import com.progkorny.beadando.feature.FeatureRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final FeatureRepository featureRepository;

    public VehicleService(VehicleRepository vehicleRepository, FeatureRepository featureRepository) {
        this.vehicleRepository = vehicleRepository;
        this.featureRepository = featureRepository;
    }

    // --- Vehicle vonatkozású függvények ---

    public List<Vehicle> getActiveVehicles(String type, Integer maxKm, Integer maxYear, String fuel) {
        String normalizedFuel = (fuel == null || fuel.isBlank()) ? null : fuel.trim();
        String normalizedType = (type == null || type.isBlank()) ? null : type.trim();
        return vehicleRepository.findActiveByFilters(normalizedType, maxKm, maxYear, normalizedFuel);
    }

    public List<Vehicle> getAllWithFeatures() {
        return vehicleRepository.findAllWithFeatures();
    }

    public Optional<Vehicle> getById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle getByIdWithFeatures(Long id) {
        return vehicleRepository.findByIdWithFeatures(id);
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void deleteById(Long id) {
        vehicleRepository.deleteById(id);
    }

    // --- Feature vonatkozású függvények ---

    public List<Feature> getAllFeatures() {
        return featureRepository.findAll();
    }

    public Optional<Feature> getFeatureById(Long id) {
        return featureRepository.findById(id);
    }

    public List<Feature> getFeaturesByIds(Set<Long> ids) {
        return featureRepository.findAllById(ids);
    }

    public Feature saveFeature(Feature feature) {
        return featureRepository.save(feature);
    }

    public void deleteFeatureById(Long id) {
        featureRepository.deleteById(id);
    }
}